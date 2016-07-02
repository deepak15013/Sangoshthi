package uk.ac.openlab.radio.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.CallerListAdapter;
import uk.ac.openlab.radio.datatypes.Callers;
import uk.ac.openlab.radio.datatypes.TopicInfoResult;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

/**
 * Created by kylemontague on 21/03/16.
 */
public class ShowOverviewActivity extends AppCompatActivity {

    public static String EXTRA_SHOULD_DIAL = "EXTRA_SHOULD_DIAL";

    Button startStopButton;
    public static Chronometer chronometer;

    ImageButton ibFlush;

    public static RecyclerView callerListRecyclerView;

    private ChecklistItemView toolbarItemView;

    public static List<Callers> callersArrayList;
    public static CallerListAdapter callerListAdapter;

    private static TextView tvTotalCallers;

    private MediaPlayer mMediaPlayer;

    public static boolean callReceived = false;
    public static AlertDialog alertDialog;

    private SeekBar sbTimeline;
    private volatile boolean startSeekBar = true;
    private Thread chronometerThread;

    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_show_overview);

        this.context = getApplicationContext();

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getResources().getString(R.string.show_overview_title));
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        chronometer = (Chronometer) findViewById(R.id.show_chronometer);

        btnStartQuiz = (Button) findViewById(R.id.btn_start_quiz);
        chronoQuizTimer = (Chronometer) findViewById(R.id.chrono_quiz_timer);
        llShowTimer = (LinearLayout) findViewById(R.id.ll_show_timer);
        ibFlush = (ImageButton) findViewById(R.id.ib_flush);
        sbTimeline = (SeekBar) findViewById(R.id.sb_timeline);
        assert sbTimeline != null;
        sbTimeline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        callerListRecyclerView = (RecyclerView) findViewById(R.id.callerList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        callerListRecyclerView.setLayoutManager(gridLayoutManager);

        callersArrayList = new ArrayList<>();

        callerListAdapter = new CallerListAdapter(callersArrayList);
        callerListRecyclerView.setAdapter(callerListAdapter);

        tvTotalCallers = (TextView) findViewById(R.id.tv_total_callers);
        tvTotalCallers.setText(getResources().getString(R.string.string_total_callers,0));

        ibFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeSwitchApi.shared().flushCallers(new IMessageListener() {
                    @Override
                    public void success() {
                        callersArrayList.clear();

                        callerListRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                callerListAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void fail() {

                    }

                    @Override
                    public void error() {

                    }

                    @Override
                    public void message(String message) {

                    }
                });
            }
        });

        startStopButton = (Button)findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        boolean shouldDial = true;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            shouldDial = extras.getBoolean(EXTRA_SHOULD_DIAL,true);
        }

        if(shouldDial) {
            MessageHelper.shared().init(this);
            FreeSwitchApi.shared().initShow(new IMessageListener() {
                @Override
                public void success() {

                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {

                }
            });
        }

        if(!callReceived) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_call_waiting_show_overview));
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);

        }

        chronometerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentTime;
                while(startSeekBar) {

                    currentTime = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
                    Log.v("dks","timeElaspsed: "+currentTime);
                    if(currentTime > 90) {
                        Log.v("dks","max changed");
                        sbTimeline.setMax((int) sbTimeline.getMax()*2);
                    }

                    sbTimeline.setProgress(currentTime);
                    Log.v("dks","max: "+sbTimeline.getMax()+" current val: "+sbTimeline.getProgress());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void setCallerObjects (final TopicInfoResult callers) {

        try {
            if(callers.getCallers() != null) {
                if(callersArrayList != null) {
                    callersArrayList.clear();

                    callersArrayList.addAll(callers.getCallers());

                    callerListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            callerListAdapter.notifyDataSetChanged();
                            tvTotalCallers.setText(context.getResources().getString(R.string.string_total_callers,callers.getListeners()-1));
                        }
                    });
                }
            } else {
                if(callers.getListeners() >= 0) {
                    callerListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            tvTotalCallers.setText(Resources.getSystem().getString(R.string.string_total_callers,callers.getListeners()-1));
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void startTimers() {

        ShowOverviewActivity.chronometer.post(new Runnable() {
            @Override
            public void run() {
                ShowOverviewActivity.chronometer.setBase(SystemClock.elapsedRealtime());
                ShowOverviewActivity.chronometer.start();
            }
        });

    }

    private void startStop() {

        if(startStopButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_start_show))) {
            Toast.makeText(ShowOverviewActivity.this, getString(R.string.action_start_show), Toast.LENGTH_SHORT).show();

            FreeSwitchApi.shared().startShow(new IMessageListener() {
                @Override
                public void success() {

                    startSeekBar = true;
                    chronometerThread.start();

                    startStopButton.setText(getString(R.string.action_stop_show));

                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {

                }
            });
        }
        else {
            Toast.makeText(ShowOverviewActivity.this, getResources().getString(R.string.action_stop_show), Toast.LENGTH_SHORT).show();

            FreeSwitchApi.shared().endShow(new IMessageListener() {
                @Override
                public void success() {
                    chronometer.stop();
                    startStopButton.setText(getString(R.string.action_show_done));
                    startSeekBar = false;

                    SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    e.putBoolean("firstStart", true);
                    e.apply();

                    Intent intent = new Intent(ShowOverviewActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT",true);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {

                }
            });
        }
    }

    Button btnStartQuiz;
    Chronometer chronoQuizTimer;
    LinearLayout llShowTimer;
    public void overviewActivityStartQuiz(View view) {

        if(btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_start_quiz))) {

            Toast.makeText(ShowOverviewActivity.this, "Quiz starting", Toast.LENGTH_SHORT).show();

            String timeStamp = new SimpleDateFormat("dd_MM_yyyy_hh_mm").format(new Date());
            String quizId = "quiz_"+timeStamp;

            String startTime = new SimpleDateFormat("hh:mm:ss").format(new Date());

            FreeSwitchApi.shared().startQuiz(new IMessageListener() {
                @Override
                public void success() {
                    btnStartQuiz.setText(getString(R.string.action_stop_quiz));

                    llShowTimer.setVisibility(LinearLayout.VISIBLE);

                    chronoQuizTimer.setBase(SystemClock.elapsedRealtime());
                    chronoQuizTimer.start();
                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {

                }
            }, quizId, startTime);
        }
        else if(btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_stop_quiz))) {

            String stopTime = new SimpleDateFormat("hh:mm:ss").format(new Date());

            FreeSwitchApi.shared().stopQuiz(new IMessageListener() {
                @Override
                public void success() {
                    btnStartQuiz.setText(getString(R.string.action_results));
                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {

                }
            }, stopTime);

            chronoQuizTimer.stop();
        } else if(btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_results))) {

            FreeSwitchApi.shared().showResults(new IMessageListener() {
                @Override
                public void success() {

                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {
                    Intent intent = new Intent(ShowOverviewActivity.this, ShowResultsActivity.class);
                    intent.putExtra("MESSAGE",message);
                    startActivity(intent);
                }
            });

            llShowTimer.setVisibility(LinearLayout.INVISIBLE);
        }
    }

}
