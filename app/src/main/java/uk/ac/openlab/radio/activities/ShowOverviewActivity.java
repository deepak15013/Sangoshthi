package uk.ac.openlab.radio.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;

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
    private static final long TWENTY_SECOND_CLOCK = 20 * 1000;

    private Button startStopButton;
    public static Chronometer chronometer;
    private boolean showStarted = false;

    ImageButton ibFlush;

    public static RecyclerView callerListRecyclerView;

    private ChecklistItemView toolbarItemView;

    public static List<Callers> callersArrayList;

    // for retaining the old list of callers.
    public static List<Callers> oldCallersArrayList;

    public static CallerListAdapter callerListAdapter;

    private static TextView tvTotalCallers;

    public static boolean callReceived = false;
    public static AlertDialog alertDialog;

    //private RangeBar rangebarTimeline;
    private TextView tvMinutes;
    //private static Thread timelineThread;
    private static volatile boolean chronometerRunning = false;
    private Button btnSaveTimestamp;

    private ToggleButton tbPlayPrerecorded;

    private static Context context;
    private static Activity activity;

    public static Thread dismissThread;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_show_overview);

        context = getApplicationContext();
        activity = this;

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getResources().getString(R.string.show_overview_title));
        toolbarItemView.setIcon(R.drawable.ic_person);

        chronometer = (Chronometer) findViewById(R.id.show_chronometer);

        btnStartQuiz = (Button) findViewById(R.id.btn_start_quiz);
        chronoQuizTimer = (Chronometer) findViewById(R.id.chrono_quiz_timer);
        llShowTimer = (LinearLayout) findViewById(R.id.ll_show_timer);
        ibFlush = (ImageButton) findViewById(R.id.ib_flush);

        //rangebarTimeline = (RangeBar) findViewById(R.id.rangebar_timeline);
        tvMinutes = (TextView) findViewById(R.id.tv_minutes);
        btnSaveTimestamp = (Button) findViewById(R.id.btn_save_timestamp);

        callerListRecyclerView = (RecyclerView) findViewById(R.id.callerList);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        callerListRecyclerView.setLayoutManager(new WrapContentGridLayoutManager(this, 2));

        callersArrayList = new ArrayList<>();
        oldCallersArrayList = new ArrayList<>();

        callerListAdapter = new CallerListAdapter(callersArrayList);
        callerListRecyclerView.setAdapter(callerListAdapter);

        tvTotalCallers = (TextView) findViewById(R.id.tv_total_callers);
        tvTotalCallers.setText(getResources().getString(R.string.string_total_callers, String.valueOf(0), String.valueOf(ShowGuestsActivity.getNumOfGuests() + ShowListenersActivity.getAshaListeners() + ShowListenersActivity.getOtherListeners())));

        tbPlayPrerecorded = (ToggleButton) findViewById(R.id.tb_play_prerecorded);

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

        startStopButton = (Button) findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        boolean shouldDial = true;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            shouldDial = extras.getBoolean(EXTRA_SHOULD_DIAL, true);
        }

        if (shouldDial) {
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

        if (!callReceived) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_call_waiting_show_overview));
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);

            dismissThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(TWENTY_SECOND_CLOCK);

                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            finishActivity();
                        }

                    } catch (InterruptedException e) {
                        Log.d("dks", "thread stopped because incoming call received");
                        e.printStackTrace();
                    }
                }
            });
            dismissThread.start();
        }

        //initTimelineThread();

        /*btnSaveTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowOverviewActivity.this, "Timestamp saved", Toast.LENGTH_SHORT).show();
                Log.v("dks","leftPinValue: "+rangebarTimeline.getLeftPinValue());
                Log.v("dks","rightPinValue: "+rangebarTimeline.getRightPinValue());
            }
        });*/

        tbPlayPrerecorded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (showStarted) {
                    if (isChecked) {
                        Toast.makeText(ShowOverviewActivity.this, "Started playing audio", Toast.LENGTH_SHORT).show();
                        tbPlayPrerecorded.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_stop_black_24dp, 0, 0);
                        tbPlayPrerecorded.setBackgroundColor(context.getResources().getColor(R.color.green));

                        FreeSwitchApi.shared().playPrerecordedMaterial(new IMessageListener() {
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

                    } else {
                        Toast.makeText(ShowOverviewActivity.this, "End playing audio", Toast.LENGTH_SHORT).show();
                        tbPlayPrerecorded.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_play_arrow_black_24dp, 0, 0);

                        tbPlayPrerecorded.setBackgroundColor(context.getResources().getColor(R.color.red));
                    }
                } else {
                    AlertDialog.Builder startShow = new AlertDialog.Builder(ShowOverviewActivity.this);
                    startShow.setMessage(getString(R.string.dialog_start_show_first));
                    startShow.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog startShowDialog = startShow.create();
                    startShowDialog.show();
                }
            }
        });
    }

    public class WrapContentGridLayoutManager extends GridLayoutManager {

        public WrapContentGridLayoutManager(ShowOverviewActivity showOverviewActivity, int i) {
            super(showOverviewActivity, i);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("dks", "meet a IOOBE in RecyclerView");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void setCallerObjects(TopicInfoResult callers) {


        final TopicInfoResult result = new TopicInfoResult(callers.getListeners(), callers.getCallers());
        Log.v("dks", "result object" + result);

        try {
            if (callers.getCallers() != null) {
                if (callersArrayList != null && oldCallersArrayList != null) {

                    oldCallersArrayList.clear();

                    oldCallersArrayList.addAll(result.getCallers());

                    for (Callers i : oldCallersArrayList) {
                        boolean callerFound = false;
                        for (Callers j : callersArrayList) {
                            if (j.getPhone_number().equals(i.getPhone_number())) {
                                callerFound = true;
                            }
                        }
                        if (!callerFound) {
                            callersArrayList.add(i);
                            Log.d("dks", "new caller added");
                        }
                    }

                    //callersArrayList.addAll(result.getCallers());

                    //callerListRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

                    callerListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            //callerListAdapter.notifyDataSetChanged();
                            callerListAdapter.notifyItemInserted(callersArrayList.size() - 1);
                            tvTotalCallers.setText(context.getResources().getString(R.string.string_total_callers, String.valueOf(result.getListeners() - 1), String.valueOf(ShowGuestsActivity.getNumOfGuests() + ShowListenersActivity.getAshaListeners() + ShowListenersActivity.getOtherListeners())));
                        }
                    });
                }
            } else {
                if (callers.getListeners() >= 0) {
                    callerListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            tvTotalCallers.setText(Resources.getSystem().getString(R.string.string_total_callers, String.valueOf(result.getListeners() - 1), String.valueOf(ShowGuestsActivity.getNumOfGuests() + ShowListenersActivity.getAshaListeners() + ShowListenersActivity.getOtherListeners())));
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startTimers() {

        if (!chronometerRunning) {

            ShowOverviewActivity.chronometer.post(new Runnable() {
                @Override
                public void run() {
                    ShowOverviewActivity.chronometer.setBase(SystemClock.elapsedRealtime());
                    ShowOverviewActivity.chronometer.start();
                }
            });

            chronometerRunning = true;
            /*if(!timelineThread.isAlive()) {
                timelineThread.start();
            }*/

        }
    }

    /*private void initTimelineThread() {
        timelineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int tickCount = 0;
                while(chronometerRunning) {
                    try {
                        Thread.sleep(ONE_MINUTE_CLOCK);
                        tickCount++;
                        setTickData(tickCount);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }*/

    /*private void setTickData(final int tickCount) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v("rangebar","tickCount: "+tickCount);
                rangebarTimeline.setTickEnd(tickCount);
                tvMinutes.setText(String.valueOf(tickCount));
            }
        });
    }*/


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startStop() {

        if (startStopButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_start_show))) {
            Toast.makeText(ShowOverviewActivity.this, getString(R.string.action_start_show), Toast.LENGTH_SHORT).show();

            showStarted = true;

            startStopButton.setText(getString(R.string.action_stop_show));
            startStopButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_phone_muted, 0, 0);

            FreeSwitchApi.shared().startShow(new IMessageListener() {
                @Override
                public void success() {

                    chronometerRunning = false;

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
        } else {
            Toast.makeText(ShowOverviewActivity.this, getResources().getString(R.string.action_stop_show), Toast.LENGTH_SHORT).show();

            FreeSwitchApi.shared().endShow(new IMessageListener() {
                @Override
                public void success() {
                    chronometer.stop();
                    chronometerRunning = false;

                    startStopButton.setText(getString(R.string.action_show_done));
                    startStopButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_phone_muted), null, null);

                    SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    e.putBoolean("firstStart", true);
                    e.apply();

                    Intent intent = new Intent(ShowOverviewActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
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
    String quizId;

    public void overviewActivityStartQuiz(View view) {

        if (btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_start_quiz))) {

            Toast.makeText(ShowOverviewActivity.this, "Quiz starting", Toast.LENGTH_SHORT).show();

            String timeStamp = new SimpleDateFormat("dd_MM_yyyy_hh_mm").format(new Date());
            quizId = "quiz_" + timeStamp;

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
        } else if (btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_stop_quiz))) {

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
        } else if (btnStartQuiz.getText().toString().equalsIgnoreCase(getString(R.string.action_results))) {

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
                    intent.putExtra("MESSAGE", message);
                    intent.putExtra("QUIZ_ID", quizId);
                    startActivity(intent);
                    btnStartQuiz.setText(getString(R.string.action_start_quiz));
                }
            });

            llShowTimer.setVisibility(LinearLayout.INVISIBLE);
        }
    }

    public static void finishActivity() {
        Log.v("dks", "finish activiy");
        if (activity != null) {
            activity.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if app closed then last state of the callers is retained in recycler view
        // this is used to stop that and flush the old callers

        if (callerListRecyclerView != null) {
            callerListRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        }

    }
}
