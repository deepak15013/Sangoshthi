package uk.ac.openlab.radio.services;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.StringTokenizer;

import uk.ac.openlab.radio.activities.ShowOverviewActivity;
import uk.ac.openlab.radio.adapters.CallerAdapter;
import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.datatypes.TopicInfoResult;

/**
 * Created by deepaksood619 on 16/6/16.
 */
public class ZMQSubscriber {

    ZMQ.Context context = ZMQ.context(1);
    ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

    private volatile boolean subscriberRunning = true;

    public void startSubscriber() {
        Thread subscriberThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("dks", "thread started");

                subscriber.connect("tcp://52.38.67.78:6003");
                subscriber.subscribe("DTMF_Speak".getBytes());


                while(subscriberRunning) {
                    byte[] msg = subscriber.recv(0);

                    String message = new String(msg);
                    Log.v("dks","String: "+message);

                    String[] result = message.split(",",2);

                    Log.v("dks",result[0]);
                    Log.v("dks",result[1]);

                    Gson gson = new GsonBuilder().create();
                    TopicInfoResult callerObjects = gson.fromJson(result[1],TopicInfoResult.class);

                    if(callerObjects != null) {

                        ShowOverviewActivity.setCallerObjects(callerObjects);


                        //ArrayList<Caller> callers = status.callers.getList();
                        //ShowOverviewActivity.mCallerAdapter.setDataset(callers);
                        //mCallerAdapter.setDataset(callers);
//                        Toast.makeText(getApplicationContext(),"Total:"+status.listeners,Toast.LENGTH_LONG).show();
                    }
                }

                subscriber.close();
                context.term();

                Log.v("tag","thread stopped");
            }
        });
        subscriberThread.start();
    }

}
