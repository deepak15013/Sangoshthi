package uk.ac.openlab.radio.network;

/**
 * Created by kylemontague on 25/03/16.
 */

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageSubscriberHandler extends Handler {
    private HashMap<String,ArrayList<IMessageListener>> listeners;



    public MessageSubscriberHandler() {
        this.listeners = new HashMap<>();
    }


    public void registerListener(IMessageListener messageListener, String... keywords){
        if(!this.listeners.containsValue(messageListener)){
            for(String keyword:keywords) {

            }
        }
    }

    public void unregisterListener(IMessageListener messageListener){
        if(!this.listeners.containsValue(messageListener))
            return;
        Iterator it = this.listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue() == messageListener)
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    @Override
    public void handleMessage(Message msg) {

//        if(msg.getData().getString(payloadKey).equals(RESULT_OK))
//            messageListener.success();
//        else if(msg.getData().getString(payloadKey).equals(RESULT_FAILED))
//            messageListener.fail();
//        else if(msg.getData().getString(payloadKey).equals(RESULT_NO_COMMAND))
//            messageListener.error();
//        else
//            messageListener.message(msg.getData().getString(payloadKey));
    }
}