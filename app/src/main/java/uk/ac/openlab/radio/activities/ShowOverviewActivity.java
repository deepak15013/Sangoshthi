package uk.ac.openlab.radio.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.CallerAdapter;
import uk.ac.openlab.radio.adapters.CallerListAdapter;
import uk.ac.openlab.radio.adapters.CountdownAdapter;
import uk.ac.openlab.radio.adapters.TopicAdapter;
import uk.ac.openlab.radio.datatypes.Callers;
import uk.ac.openlab.radio.datatypes.Icon;
import uk.ac.openlab.radio.datatypes.Topic;
import uk.ac.openlab.radio.datatypes.TopicInfoResult;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.drawables.TopicView;
import uk.ac.openlab.radio.drawables.VisualizerView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

/**
 * Created by kylemontague on 21/03/16.
 */
public class ShowOverviewActivity extends AppCompatActivity {

    public static String EXTRA_SHOULD_DIAL = "EXTRA_SHOULD_DIAL";

//    TextView countdownView;

    Button startStopButton;
    Chronometer chronometer;

    public static RecyclerView callerListRecyclerView;


    /*Button recordedMaterial;
    Button previousClips;*/

//    CallerButton guest;
    private RecyclerView mCallerRecyclerView;
    public static CallerAdapter mCallerAdapter;
    private RecyclerView.LayoutManager mCallerLayoutManager;

    private RecyclerView mTopicRecyclerView;
    private TopicAdapter mTopicAdapter;
    private RecyclerView.LayoutManager mTopicLayoutManager;

    private CountdownAdapter mCountdownAdapter;

    private ChecklistItemView toolbarItemView;

    public static List<Callers> callersArrayList;
    public static CallerListAdapter callerListAdapter;

    private static TextView tvTotalCallers;

    private HorizontalBarChart chartTimeline;

    ArrayList<BarEntry> entries;
    BarDataSet dataset;

    VisualizerView mVisualizerView;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_show_overview);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Show Dashboard");
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        chronometer = (Chronometer) findViewById(R.id.show_chronometer);

        btnStartQuiz = (Button) findViewById(R.id.btn_start_quiz);
        chronoQuizTimer = (Chronometer) findViewById(R.id.chrono_quiz_timer);
        llShowTimer = (LinearLayout) findViewById(R.id.ll_show_timer);

        callerListRecyclerView = (RecyclerView) findViewById(R.id.callerList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        callerListRecyclerView.setLayoutManager(gridLayoutManager);

        callersArrayList = new ArrayList<>();

        callerListAdapter = new CallerListAdapter(callersArrayList);
        callerListRecyclerView.setAdapter(callerListAdapter);

        tvTotalCallers = (TextView) findViewById(R.id.tv_total_callers);

        //mVisualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);

        //initAudio();


        /*chartTimeline = (HorizontalBarChart) findViewById(R.id.chart_timeline);

        entries = new ArrayList<>();
        entries.add(new BarEntry(1, 0));
        *//*entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(12f, 3));
        entries.add(new BarEntry(18f, 4));
        entries.add(new BarEntry(9f, 5));*//*

        dataset = new BarDataSet(entries, "# of Calls");
        //BarData data = new BarData(getXAxisValues(), getDataSet());

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("");
        *//*labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");*//*

        BarData data = new BarData(labels, dataset);
        chartTimeline.setData(data);
        chartTimeline.setDescription("");
        chartTimeline.getAxisLeft().setDrawLabels(false);
        chartTimeline.getAxisRight().setDrawLabels(false);
        chartTimeline.getXAxis().setDrawLabels(false);
        chartTimeline.getLegend().setEnabled(false);
        //chartTimeline.setAutoScaleMinMaxEnabled(true);

        //chartTimeline.setData(dataset);
        //chartTimeline.setDescription("My Chart");

        //chartTimeline.animateXY(2000, 2000);

        chartTimeline.invalidate();
*/

        /*recordedMaterial = (Button)findViewById(R.id.button_material);
        recordedMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeSwitchApi.shared().playMedia(new IMessageListener() {
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
                },"1");//todo remove hardcoded value
            }
        });*/

        /*previousClips = (Button)findViewById(R.id.button_clips);
        previousClips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeSwitchApi.shared().stopMedia(new IMessageListener() {
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
        });*/

        startStopButton = (Button)findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });



//        countdownView = (TextView)findViewById(R.id.countdown);

        //set up the call queue and adapters
        /*mCallerRecyclerView = (RecyclerView)findViewById(R.id.caller_controls);
        mCallerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCallerRecyclerView.setLayoutManager(mCallerLayoutManager);
        mCallerAdapter = new CallerAdapter(new ArrayList<Caller>(), new IRecyclerViewItemClickedListener() {
            @Override
            public void recyclerViewItemClicked(View view, int position) {
                //todo handle mute clicked if needed.
            }
        }, GlobalUtils.fetchColor(getApplicationContext(), R.attr.colorPrimary));


        mCallerRecyclerView.setAdapter(mCallerAdapter);*/


        //mTopicRecyclerView = (RecyclerView)findViewById(R.id.timeline);
       /* mTopicLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTopicRecyclerView.setLayoutManager(mTopicLayoutManager);
        mTopicAdapter = new TopicAdapter(new ArrayList<Topic>());
        mTopicRecyclerView.setAdapter(mTopicAdapter);*/

//        addDummyData();

        /*guest = (CallerButton)findViewById(R.id.button_guest);
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });*/


        // FIXME: 26/04/16 - remove hardcoded duration and pull from the object itself.
        /*mCountdownAdapter = new CountdownAdapter(2*60*1000,1000);
        mCountdownAdapter.registerListener(clockListener);
        mCountdownAdapter.registerListener(topicTimeListener);*/




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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /*private void initAudio() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMediaPlayer = MediaPlayer.create(this, R.raw.test);

        setupVisualizerFxAndUI();
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);
        // When the stream ends, we don't need to collect any more data. We
        // don't do this in
        // setupVisualizerFxAndUI because we likely want to have more,
        // non-Visualizer related code
        // in this callback.
        mMediaPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVisualizer.setEnabled(false);
                    }
                });
        mMediaPlayer.start();

    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }*/

    public static void setCallerObjects (final TopicInfoResult callers) {
        //Log.v("dks","calers: "+callers.getCallers().get(0).getPhone_number());

        try {
            if(callers.getCallers() != null) {
                if(callersArrayList != null) {
                    callersArrayList.clear();
                    callersArrayList.addAll(callers.getCallers());

                    callerListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            callerListAdapter.notifyDataSetChanged();
                            tvTotalCallers.setText("Total callers "+(callers.getListeners()-1));
                        }
                    });
                }
                else {
                    if(callers.getListeners() >= 0) {
                        tvTotalCallers.setText("Total callers "+(callers.getListeners()-1));
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateInfo() {
        FreeSwitchApi.shared().showInfo(new IMessageListener() {
            @Override
            public void success() {
                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void fail() {
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void error() {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void message(String message) {

                Log.v("dks","message for parse: "+message);

                Gson gson = new GsonBuilder().create();
                TopicInfoResult status = gson.fromJson(message,TopicInfoResult.class);

                if(status!=null) {
//                    ArrayList<Caller> callers = status.callers.getList();
//                    mCallerAdapter.setDataset(callers);
//                    Toast.makeText(getApplicationContext(),"Total:"+status.listeners,Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void addDummyData(){
        mTopicAdapter.add(new Topic("Topic 1", new Icon("1",Icon.resURI(this,R.drawable.ic_person)),R.color.white));
        mTopicAdapter.add(new Topic("Topic 2", new Icon("2",Icon.resURI(this,R.drawable.ic_radio)),R.color.grey));
    }


    /**
     * play an audio clip from the pre-recorded show material
     *
     * @param view
     */
    public void PlayRecordedMaterial(View view) {

    }

    /**
     * play an audio clip from a previously aired show.
     *
     * @param view
     */
    public void PlayPreviousClips(View view) {

    }


    boolean hasFinished = false;
    private void startStop() {

        if(startStopButton.getText().toString().equalsIgnoreCase("start show")) {
            Toast.makeText(ShowOverviewActivity.this, "start show", Toast.LENGTH_SHORT).show();

            FreeSwitchApi.shared().startShow(new IMessageListener() {
                @Override
                public void success() {
                    Log.v("dks","show started");

                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();

                    /*getChronometerValue();
                    showRunning = true;*/

                    startStopButton.setText("Stop show");

                }

                @Override
                public void fail() {

                }

                @Override
                public void error() {

                }

                @Override
                public void message(String message) {
                    Log.v("dks","show_start_message: "+message);
                }
            });
        }
        else {
            Toast.makeText(ShowOverviewActivity.this, "stop show", Toast.LENGTH_SHORT).show();

            FreeSwitchApi.shared().endShow(new IMessageListener() {
                @Override
                public void success() {
                    chronometer.stop();
                    startStopButton.setText("start show");
                    //showRunning = false;
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

   /* public volatile boolean showRunning = true;

    int secondElapsed;
    public void getChronometerValue() {
        Thread thread = new Thread (new Runnable() {
            @Override
            public void run() {
                while(showRunning) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    Log.v("dks","elapsedMillis: "+elapsedMillis);

                    secondElapsed = (int) (elapsedMillis/1000);

                    entries.clear();
                    entries.add(new BarEntry(secondElapsed, 0));

                    chartTimeline.post(new Runnable() {
                        @Override
                        public void run() {
                            chartTimeline.setX(secondElapsed+3);

                            dataset.notifyDataSetChanged();
                            chartTimeline.notifyDataSetChanged();
                            chartTimeline.invalidate();

                        }
                    });
                    //addEntry(secondElapsed);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
*/


    /*CountdownAdapter.CountdownListener clockListener = new CountdownAdapter.CountdownListener() {
        @Override
        public void registered(long duration) {
            countdownView.setText(countdownRemaining(duration));

        }

        //Countdown Timer Methods
        @Override
        public void finished() {
            Toast.makeText(ShowOverviewActivity.this, "Timer has finished", Toast.LENGTH_LONG).show();
            countdownView.setText("0:00");

        }

        @Override
        public void tick(long remaining) {
            countdownView.setText(countdownRemaining(remaining));
        }

        @Override
        public void started(long remaining) {
            countdownView.setText(countdownRemaining(remaining));
        }

        private String countdownRemaining(long remaining) {
            return String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(remaining),
                    TimeUnit.MILLISECONDS.toSeconds(remaining) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remaining))
            );
        }
    };*/


    float stepShrink = 5;
    float offset = 0;
    TopicView currentTopic = null;
    CountdownAdapter.CountdownListener topicTimeListener = new CountdownAdapter.CountdownListener() {
        @Override
        public void registered(long duration) {
            long topicLength = (duration/1000)/mTopicAdapter.getItemCount();// FIXME: 26/04/16 - this should be provided by each topic
            stepShrink =  400.0f/topicLength;
        }

        @Override
        public void finished() {
            //add end show button to the canvas
            /*startStopButton.setVisibility(View.VISIBLE);
            startStopButton.setText("End Show");*/
            hasFinished = true;
        }

        @Override
        public void tick(long remaining) {
            offset+=stepShrink;
            mTopicRecyclerView.getLayoutManager().offsetChildrenHorizontal(-1*(int)offset);
            mTopicRecyclerView.invalidate();

        }

        @Override
        public void started(long remaining) {
            //remove the start show button and initialise the topics.
            /*startStopButton.setVisibility(View.GONE);
            startStopButton.setText("Start Show");*/
        }
    };

    Button btnStartQuiz;
    Chronometer chronoQuizTimer;
    LinearLayout llShowTimer;
    public void overviewActivityStartQuiz(View view) {

        if(btnStartQuiz.getText().toString().equalsIgnoreCase("start quiz")) {

            Toast.makeText(ShowOverviewActivity.this, "Quiz starting", Toast.LENGTH_SHORT).show();

            String timeStamp = new SimpleDateFormat("dd_MM_yyyy_hh_mm").format(new Date());
            String quizId = "quiz_"+timeStamp;
            Log.v("dks","quizId: "+quizId);

            String startTime = new SimpleDateFormat("hh:mm:ss").format(new Date());
            Log.v("dks","startTime: "+startTime);

            FreeSwitchApi.shared().startQuiz(new IMessageListener() {
                @Override
                public void success() {
                    btnStartQuiz.setText("Stop Quiz");

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
        else if(btnStartQuiz.getText().toString().equalsIgnoreCase("stop quiz")) {

            String stopTime = new SimpleDateFormat("hh:mm:ss").format(new Date());

            FreeSwitchApi.shared().stopQuiz(new IMessageListener() {
                @Override
                public void success() {
                    btnStartQuiz.setText("Results");
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
        } else if(btnStartQuiz.getText().toString().equalsIgnoreCase("results")) {

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

            Toast.makeText(ShowOverviewActivity.this, "Show Results", Toast.LENGTH_SHORT).show();
            llShowTimer.setVisibility(LinearLayout.INVISIBLE);
        }
    }

}
