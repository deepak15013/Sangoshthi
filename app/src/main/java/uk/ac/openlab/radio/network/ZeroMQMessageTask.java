package uk.ac.openlab.radio.network;

/**
 * Created by Kyle Montague on 25/03/16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.zeromq.ZMQ;

import uk.ac.openlab.radio.GlobalUtils;

public class ZeroMQMessageTask extends AsyncTask<String, Void, String> {


    public static ZeroMQMessageTask zmqStudio(Handler uiThreadHandler, ZMQ.Socket socket){
//        return new ZeroMQMessageTask(uiThreadHandler,context.getString(R.string.pref_studio_host),context.getResources().getInteger(R.integer.pref_studio_sub_port));
        return new ZeroMQMessageTask(uiThreadHandler,socket);

    }

    public static ZeroMQMessageTask zmqMediaControl(Handler uiThreadHandler, ZMQ.Socket socket){
//        return new ZeroMQMessageTask(uiThreadHandler,context.getString(R.string.pref_studio_host),context.getResources().getInteger(R.integer.pref_studio_control_port));
        return new ZeroMQMessageTask(uiThreadHandler,socket);

    }

    private final Handler uiThreadHandler;
    private ZMQ.Socket socket;

    public ZeroMQMessageTask(Handler uiThreadHandler, ZMQ.Socket socket) {
        this.uiThreadHandler = uiThreadHandler;
        this.socket = socket;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("ZMQ","msg:"+params[0]);
        socket.send(params[0].getBytes(), 0);
        String result = new String(socket.recv(0));
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("ZMQ","result:"+result);
        Bundle b = new Bundle();
        b.putString(GlobalUtils.PAYLOAD_KEY,result);
        Message message = Message.obtain();
        message.setData(b);
        message.setTarget(uiThreadHandler);
        uiThreadHandler.sendMessage(message);

        return;
    }
}