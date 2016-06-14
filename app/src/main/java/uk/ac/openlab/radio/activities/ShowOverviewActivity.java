package uk.ac.openlab.radio.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.CallerAdapter;
import uk.ac.openlab.radio.adapters.CountdownAdapter;
import uk.ac.openlab.radio.adapters.IRecyclerViewItemClickedListener;
import uk.ac.openlab.radio.adapters.TopicAdapter;
import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.datatypes.Icon;
import uk.ac.openlab.radio.datatypes.Topic;
import uk.ac.openlab.radio.datatypes.TopicInfoResult;
import uk.ac.openlab.radio.drawables.CallerButton;
import uk.ac.openlab.radio.drawables.TopicView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by kylemontague on 21/03/16.
 */
public class ShowOverviewActivity extends AppCompatActivity {


    public static String EXTRA_SHOULD_DIAL = "EXTRA_SHOULD_DIAL";

    TextView countdownView;
    Button startStopButton;
    Button recordedMaterial;
    Button previousClips;
    CallerButton guest;
    private RecyclerView mCallerRecyclerView;
    private CallerAdapter mCallerAdapter;
    private RecyclerView.LayoutManager mCallerLayoutManager;

    private RecyclerView mTopicRecyclerView;
    private TopicAdapter mTopicAdapter;
    private RecyclerView.LayoutManager mTopicLayoutManager;


    private CountdownAdapter mCountdownAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_show_overview);


        recordedMaterial = (Button)findViewById(R.id.button_material);
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
        });

        previousClips = (Button)findViewById(R.id.button_clips);
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
        });

        startStopButton = (Button)findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        countdownView = (TextView)findViewById(R.id.countdown);

        //set up the call queue and adapters
        mCallerRecyclerView = (RecyclerView)findViewById(R.id.caller_controls);
        mCallerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCallerRecyclerView.setLayoutManager(mCallerLayoutManager);
        mCallerAdapter = new CallerAdapter(new ArrayList<Caller>(), new IRecyclerViewItemClickedListener() {
            @Override
            public void recyclerViewItemClicked(View view, int position) {
                //todo handle mute clicked if needed.
            }
        }, GlobalUtils.fetchColor(getApplicationContext(), R.attr.colorPrimary));


        mCallerRecyclerView.setAdapter(mCallerAdapter);


        mTopicRecyclerView = (RecyclerView)findViewById(R.id.timeline);
        mTopicLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTopicRecyclerView.setLayoutManager(mTopicLayoutManager);
        mTopicAdapter = new TopicAdapter(new ArrayList<Topic>());
        mTopicRecyclerView.setAdapter(mTopicAdapter);

        addDummyData();

        guest = (CallerButton)findViewById(R.id.button_guest);
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });


        // FIXME: 26/04/16 - remove hardcoded duration and pull from the object itself.
        mCountdownAdapter = new CountdownAdapter(2*60*1000,1000);
        mCountdownAdapter.registerListener(clockListener);
        mCountdownAdapter.registerListener(topicTimeListener);




        boolean shouldDial = true;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            shouldDial = extras.getBoolean(EXTRA_SHOULD_DIAL,true);
        }


        if(shouldDial) {
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
                Gson gson = new GsonBuilder().create();
                TopicInfoResult status = gson.fromJson(message,TopicInfoResult.class);

                if(status!=null) {
                    ArrayList<Caller> callers = status.callers.getList();
                    mCallerAdapter.setDataset(callers);
                    Toast.makeText(getApplicationContext(),"Total:"+status.listeners,Toast.LENGTH_LONG).show();
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

        FreeSwitchApi.shared().startShow(new IMessageListener() {
            @Override
            public void success() {
//                if(!hasFinished){
//                    mCountdownAdapter.start();
//                }
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



    CountdownAdapter.CountdownListener clockListener = new CountdownAdapter.CountdownListener() {
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
    };


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
            startStopButton.setVisibility(View.VISIBLE);
            startStopButton.setText("End Show");
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
            startStopButton.setVisibility(View.GONE);
            startStopButton.setText("Start Show");
        }
    };




}
