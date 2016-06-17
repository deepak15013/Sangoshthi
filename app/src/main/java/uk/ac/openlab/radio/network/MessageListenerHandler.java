package uk.ac.openlab.radio.network;

/**
 * Created by kylemontague on 25/03/16.
 */
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MessageListenerHandler extends Handler {
    private final IMessageListener messageListener;
    private final String payloadKey;


    public final static String RESULT_OK = "+OK";
    public final static String RESULT_FAILED = "+FAIL";
    public final static String RESULT_NO_COMMAND = "+NC";


    public MessageListenerHandler(IMessageListener messageListener, String payloadKey) {
        this.messageListener = messageListener;
        this.payloadKey = payloadKey;
    }

    @Override
    public void handleMessage(Message msg) {

        if(msg.getData().getString(payloadKey).equals(RESULT_OK))
            messageListener.success();
        else if(msg.getData().getString(payloadKey).equals(RESULT_FAILED))
            messageListener.fail();
        else if(msg.getData().getString(payloadKey).equals(RESULT_NO_COMMAND))
            messageListener.error();
        else
            messageListener.message(msg.getData().getString(payloadKey));
    }
}