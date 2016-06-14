package uk.ac.openlab.radio.network;

import android.os.Handler;
import android.os.Message;
import org.zeromq.ZMQ;
/**
 * Created by Kyle Montague on 05/04/16.
 */
public class ZeroMQSubscriber implements Runnable {

    private final Handler uiThreadHandler;
    private ZMQ.Socket socket;

    public ZeroMQSubscriber(Handler uiThreadHandler, ZMQ.Socket socket) {
        this.uiThreadHandler = uiThreadHandler;
        this.socket = socket;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            byte[] msg = socket.recv(0);
            Message message = Message.obtain();
            message.obj = new String(msg);
            message.setTarget(uiThreadHandler);
            uiThreadHandler.sendMessage(message);
//            socket.send(Util.reverseInPlace(msg), 0);
        }
    }
}
