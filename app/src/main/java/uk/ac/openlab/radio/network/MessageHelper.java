package uk.ac.openlab.radio.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

import uk.ac.openlab.radio.GlobalUtils;

/**
 * Created by Kyle Montague on 05/04/16.
 */
public class MessageHelper {


    /**
     * FreeSwitch Commands and Actions.
     */
    private final String FS_CMD_PREPARE_SHOW = "prepare_show";
    private final String FS_CMD_AUTHENTICATE = "auth";
    private final String FS_CMD_START_SHOW = "start_show";
    private final String FS_CMD_DIAL_LISTENERS = "dial_listeners";
    private final String FS_CMD_MUTE_STATE = "mute_state";
    private final String FS_CMD_MEDIA_CONTROL = "media_control";
    private final String FS_CMD_SHOW_INFO = "show_info";

    // dks
    private final String FS_CREATE_SHOW="create_show";
    private final String FS_ADD_HOST = "add_host";
    private final String FS_CHECK_SHOW_STATUS = "check_show_status";
    private final String FS_GET_SHOW_ID = "get_show_id";
    private final String FS_ADD_PERSON = "add_person";
    private final String FS_SHOW_LISTENERS = "show_listeners";

    private final String FS_ACTION_START_PREP = "init_call";
    private final String FS_ACTION_START_RECORD = "start_record";
    private final String FS_ACTION_STOP_RECORD = "stop_record";
    private final String FS_ACTION_START_PLAYBACK = "start_playback";
    private final String FS_ACTION_STOP_PLAYBACK = "stop_playback";
    private final String FS_ACTION_SAVE_RECORDING = "save_record";
    private final String FS_ACTION_DELETE_RECORDING = "delete_record";


    public enum MuteState{
        mute,
        unmute
    };


    public String session_id = "";
    public String studio_id = "";

    public String host_phone_num = "";
    public String host_area_code= "";

    private static  MessageHelper sharedInstance;
    public static MessageHelper shared(){
        if(sharedInstance == null) {
            sharedInstance = new MessageHelper();
        }
        return sharedInstance;
    }

    public void init(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.session_id = preferences.getString(GlobalUtils.PREF_SESSION_ID,null);
        this.studio_id = preferences.getString(GlobalUtils.PREF_STUDIO_ID,null);
        this.host_phone_num = preferences.getString(GlobalUtils.PREF_TELEPHONE_NUMBER, null);
        this.host_area_code = preferences.getString(GlobalUtils.PREF_AREA_CODE, null);
    }

    public void init(String session_id,String studio_id){
        this.session_id = session_id;
        this.studio_id = studio_id;
    }


    /**            NEW FREESWITCH API COMMANDS            **/

    public String checkShowStatus() {
        return String.format("{\"cmd\":\"%s\" }",FS_CHECK_SHOW_STATUS);
    }

    public String getShowId() {
        return String.format("{\"cmd\":\"%s\" }",FS_GET_SHOW_ID);
    }

    public String createShow(String date, String time, String code) {
        String create = String.format("{\"cmd\":\"%s\", \"date\" : \"%s\", \"time\" : \"%s\", \"code\" : \"%s\" }", FS_CREATE_SHOW, date, time, code);
        Log.v("Create Show String: ", create);
        return create;
    }

    public String createHost(String status) {
        return String.format("{\"cmd\":\"%s\", \"host_phone_num\" : \"%s\", \"locale\" : \"%s\", \"status\" : \"%s\"}",FS_ADD_HOST, this.host_phone_num, this.host_area_code, status);
    }

    public String addListener(String locale, String phone, String role, String roleCategory) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"locale\" : \"%s\", \"phone\" : \"%s\", \"role\" : \"%s\", \"role_category\" : \"%s\"}",FS_ADD_PERSON, this.studio_id, locale, phone, role, roleCategory);
    }

    public String showListeners() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}",FS_SHOW_LISTENERS, this.studio_id);
    }

    public String startPrep(){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW,FS_ACTION_START_PREP,this.studio_id, this.session_id);
    }

    public String startRecord(int duration){
        return String.format(Locale.ENGLISH,"{\"cmd\":\"%s\", \"action\" : \"%s\", \"params\" : { \"max_length\" : \"%d\" } , \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW,FS_ACTION_START_RECORD,duration, this.studio_id, this.session_id);
    }

    public String startRecordingPlayback(){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW, FS_ACTION_START_PLAYBACK, this.studio_id, this.session_id);
    }

    public String stopRecordingPlayback(){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW, FS_ACTION_STOP_PLAYBACK, this.studio_id, this.session_id);
    }

    public String stopRecording(){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW,FS_ACTION_STOP_RECORD, this.studio_id, this.session_id);
    }

    public String saveRecording() {
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_PREPARE_SHOW,FS_ACTION_SAVE_RECORDING, this.studio_id, this.session_id);
    }

    public String deleteRecording(String mediaID){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\", \"params\" : { \"media\" : \"%s\" } }", FS_CMD_PREPARE_SHOW,FS_ACTION_DELETE_RECORDING, this.studio_id, this.session_id,mediaID);
    }


    /*public String authenticate(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_AUTHENTICATE, this.studio_id, this.session_id);
    }*/


    public String muteState(String uuid, MuteState state){
        return String.format(Locale.ENGLISH,"{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\", \"timestamp\" : \"%d\", \"params\" : { \"uuid\" : \"%s\", \"state\" : \"%s\" } }", FS_CMD_MUTE_STATE, this.studio_id, this.session_id, System.currentTimeMillis(), uuid, state.name());

    }

    public String initShow(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_START_SHOW,this.studio_id, this.session_id);
    }

    public String startShow(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_DIAL_LISTENERS,this.studio_id, this.session_id);
    }

    public String showInfo(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_SHOW_INFO,this.studio_id, this.session_id);
    }


    public String playMedia(String mediaID){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\", \"params\" : { \"id\" : \"%s\" } }", FS_CMD_MEDIA_CONTROL, FS_ACTION_START_PLAYBACK, this.studio_id, this.session_id,mediaID);
    }

    public String stopMedia(){
        return String.format("{\"cmd\":\"%s\", \"action\" : \"%s\", \"studio\": \"%s\", \"session\" : \"%s\" }", FS_CMD_MEDIA_CONTROL, FS_ACTION_STOP_PLAYBACK, this.studio_id, this.session_id);
    }


}
