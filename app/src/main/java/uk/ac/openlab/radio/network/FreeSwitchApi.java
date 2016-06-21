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

    public void getShowId(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().getShowId());
    }


    /**
     * Communicate with FreeSwitch server to verify that the session id and studio id are valid.
     * IMPORTANT, this is needed to ensure future commands can be actioned.
     * @param callback
     */
    /*public void authenticate(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().authenticate());
    }*/

    /**
     * Tell FreeSwitch to call this number to begin preparing the show.
     * @param callback
     */
    public void prepareShow(IMessageListener callback){
       sendMessage(callback,MessageHelper.shared().startPrep());
    }

    public void createShow(IMessageListener callback, String date, String time, String code) {
        sendMessage(callback, MessageHelper.shared().createShow(date, time, code));
    }

    public void addListener(IMessageListener callback, String locale, String phone, String role, String roleCategory) {
        sendMessage(callback, MessageHelper.shared().addListener(locale, phone, role, roleCategory));
    }

    public void showListeners(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().showListeners());
    }

    public void createHost(IMessageListener callback, String status) {
        sendMessage(callback, MessageHelper.shared().createHost(status));
    }

    public void createTrailer(IMessageListener callback) {
        sendMessage(callback, MessageHelper.shared().createTrailer());
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
     * Tell FreeSwitch to record the line (used only in Show Preparation activities i.e. record topic, trailer, filler material) for a maximum duration.
     * @param maxDuration
     * @param callback
     */
    public void startRecording(int maxDuration, IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().startRecord(maxDuration));
    }

    /**
     * Tell FreeSwitch to playback the last recording made by the PRESENTER (Show Preparation)
     * @param callback
     */
    public void startRecordingPlayback(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().startRecordingPlayback());
    }

    /**
     * Tell FreeSwitch to stop playing the current recording being played.
     * @param callback
     */
    public void stopRecordingPlayback(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().stopRecordingPlayback());
    }

    /**
     * Tell FreeSwtich to stop recording (Show Preparation only)
     * @param callback
     */
    public void stopRecording(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().stopRecording());
    }

    /**
     * Tell FreeSwitch to save this recording as a media item and return (via callback) the media item id.
     * @param callback
     */
    public void saveRecording(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().saveRecording());
    }

    /**
     * Tell FreeSwitch to delete the last recording made (Show Preperation)
     * @param callback
     * @param mediaID
     */
    public void deleteRecording(IMessageListener callback, String mediaID){
        sendMessage(callback,MessageHelper.shared().deleteRecording(mediaID));
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

    /**
     * Request the overview of the show i.e. guests, listeners, callers, likes, topics.
     * @param callback
     */
    public void showInfo(IMessageListener callback){
        sendMessage(callback, MessageHelper.shared().showInfo());
    }

    /**
     * Start playing a prerecorded media item on the conference call.
     * @param callback
     * @param mediaID
     */
    public void playMedia(IMessageListener callback, String mediaID){
        sendMessage(callback,MessageHelper.shared().playMedia(mediaID));

    }

    /**
     * Stop playing any media items in the conference call.
     * @param callback
     */
    public void stopMedia(IMessageListener callback){
        sendMessage(callback,MessageHelper.shared().stopMedia());
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

