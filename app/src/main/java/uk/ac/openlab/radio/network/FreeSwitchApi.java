package uk.ac.openlab.radio.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.zeromq.ZMQ;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;


/**
 * Created by Kyle Montague on 29/04/16.
 */
public class FreeSwitchApi {

    private static FreeSwitchApi sharedInstance;
    public static FreeSwitchApi shared(){
        if(sharedInstance == null)
            sharedInstance = new FreeSwitchApi();
        return sharedInstance;
    }

    private FreeSwitchMQ freeSwitchMQ;

    private boolean isInit = false;



    public void init(Context context){
        this.freeSwitchMQ = new FreeSwitchMQ(context);
        isInit = true;
    }

    public void checkShowStatus(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().checkShowStatus());
    }

    public void getHost(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().getHost());
    }

    public void getShowId(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().getShowId());
    }

    public void createShow(IMessageListener callback, String date, String time, String category) {
        sendMessage(callback, MessageHelper.shared().createShow(date, time, category));
    }

    public void addListener(IMessageListener callback, String locale, String phone, String role, String roleCategory) {
        sendMessage(callback, MessageHelper.shared().addListener(locale, phone, role, roleCategory));
    }

    public void showListeners(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().showListeners());
    }

    public void showGuests(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().showGuests());
    }

    public void createHost(IMessageListener callback, String status) {
        sendMessage(callback, MessageHelper.shared().createHost(status));
    }

    public void createTrailer(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().createTrailer());
    }

    public void playTrailer(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().playTrailer());
    }

    public void deleteTrailer(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().deleteTrailer());
    }

    public void deleteListener(IMessageListener callback, String phone) {
        sendMessage(callback, MessageHelper.shared().deleteListener(phone));
    }

    public void deleteCategoryListener(IMessageListener callback, String role_category) {
        sendMessage(callback, MessageHelper.shared().deleteCategoryListeners(role_category));
    }

    public void spreadWord(IMessageListener callback, String date, String time, String listener_category) {
        sendMessage(callback, MessageHelper.shared().spreadWord(date, time, listener_category));
    }

    public void checkTrailerStatus(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().checkTrailerStatus());
    }

    public void startQuiz(IMessageListener callback, String quizId, String startTime) {
        sendMessage(callback, MessageHelper.shared().startQuiz(quizId, startTime));
    }

    public void stopQuiz(IMessageListener callback, String stopTime) {
        sendMessage(callback, MessageHelper.shared().stopQuiz(stopTime));
    }

    public void showResults(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().showResults());
    }

    public void startListenerRating(IMessageListener callback, String phoneNum) {
        sendMessage(callback, MessageHelper.shared().startListenerRating(phoneNum));
    }

    public void stopListenerRating(IMessageListener callback, String phoneNum) {
        sendMessage(callback, MessageHelper.shared().stopListenerRating(phoneNum));
    }

    /**
     * Tell FreeSwitch to mute / unmute a caller using their UUID from the conference.
     * @param uuid
     * @param state
     * @param callback
     */
    public void setCallerMute(IMessageListener callback, String uuid, String state){
        sendMessage(callback,MessageHelper.shared().muteState(uuid,state));
    }

    /**
     * Tell FreeSwitch to initialise a conference call in preparation for running the show. FreeSwitch will then dial the PRESENTER device.
     * @param callback
     */
    public void initShow(IMessageListener callback){
        sendMessage(callback, MessageHelper.shared().initShow());
    }

    /**
     * Tell FreeSwitch to call the guests and listeners, then add them to the current conference call.
     * @param callback
     */
    public void startShow(IMessageListener callback){
        sendMessage(callback, MessageHelper.shared().startShow());
    }

    public void endShow(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().endShow());
    }

    public void flushCallers(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().flushCallers());
    }

    public void callRejected(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().callRejected());
    }

    private void sendMessage(IMessageListener callback, String message){
        if(!isInit) {
            callback.message("Not initialised.");
            callback.fail();
            return;
        }
        MessageListenerHandler handler= new MessageListenerHandler(callback, GlobalUtils.PAYLOAD_KEY);
        freeSwitchMQ.messageTask(handler).execute(message);
    }

    public class FreeSwitchMQ{

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        Context mContext;
        boolean isConnected = false;
        public FreeSwitchMQ(Context context) {
            this.mContext = context;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    socket.connect(String.format("%s:%d",mContext.getString(R.string.pref_ivr_host),mContext.getResources().getInteger(R.integer.pref_ivr_port)));
                    isConnected = true;
                }
            }).start();
        }

        public ZeroMQMessageTask messageTask(Handler uiThreadHandler){
            while (!isConnected)
            {
                try {
                    Log.d("FREESWITCH","Not Connected yet.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new ZeroMQMessageTask(uiThreadHandler,this.socket);
        }


    }

}

