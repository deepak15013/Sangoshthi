package uk.ac.openlab.radio.services;

import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.zeromq.ZMQ;

import uk.ac.openlab.radio.activities.ShowOverviewActivity;
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

                subscriber.connect("tcp://52.38.67.78:6003");
                subscriber.subscribe("DTMF_Speak".getBytes());
                subscriber.subscribe("timer".getBytes());

                while(subscriberRunning) {
                    byte[] msg = subscriber.recv(0);

                    String message = new String(msg);
                    Log.v("dks","String: "+message);

                    if(message.equalsIgnoreCase("start_timer")) {
                        Log.v("dks","start_timer");
                        ShowOverviewActivity.chronometer.setBase(SystemClock.elapsedRealtime());
                        ShowOverviewActivity.chronometer.start();
                    }
                    else {

                        String[] result = message.split(",",2);

                        Gson gson = new GsonBuilder().create();
                        TopicInfoResult callerObjects = gson.fromJson(result[1],TopicInfoResult.class);

                        if(callerObjects != null) {

                            ShowOverviewActivity.setCallerObjects(callerObjects);

                        }
                    }
                }

                subscriber.close();
                context.term();
            }
        });
        subscriberThread.start();
    }

}
