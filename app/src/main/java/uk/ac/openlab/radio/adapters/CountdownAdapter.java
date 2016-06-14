package uk.ac.openlab.radio.adapters;

import android.os.CountDownTimer;

import java.util.ArrayList;

/**
 * Created by kylemontague on 26/04/16.
 */
public class CountdownAdapter {


    long duration;
    long interval;
    ArrayList<CountdownListener> listeners;
    CountDownTimer timer;

    /**
     * Create a new CountdownAdapter with duration (in milliseconds), update interval (in milliseconds).
     * @param duration
     */
    public CountdownAdapter(long duration, long intervalUpdates){
       init(duration,intervalUpdates);
    }

    /**
     * Create a new CountdownAdapter with duration (in milliseconds) update interval (in milliseconds) and listener
     * @param duration
     */
    public CountdownAdapter(long duration, long intervalUpdates, CountdownListener listener){


        init(duration,intervalUpdates);
        registerListener(listener);
    }

    private void init(long duration, long intervalUpdates){
        this.duration = duration;
        this.interval = intervalUpdates;
        listeners = new ArrayList<>();
        this.timer = new CountDownTimer(duration,intervalUpdates) {
            @Override
            public void onTick(long millisUntilFinished) {
                for(CountdownListener l:listeners)
                    l.tick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                for(CountdownListener l:listeners)
                    l.finished();
            }
        };
    }

    public void registerListener(CountdownListener listener){
        if(!listeners.contains(listener)) {
            listeners.add(listener);
            listener.registered(this.duration);
        }
    }

    public void unregisterListener(CountdownListener listener){
        if(listeners.contains(listener))
            listeners.remove(listener);
    }


    public void start(){
        timer.start();
        for(CountdownListener l:listeners)
            l.started(this.duration);
    }


    public interface CountdownListener{
        void registered(long duration);
        void finished();
        void tick(long remaining);
        void started(long remaining);
    }
}
