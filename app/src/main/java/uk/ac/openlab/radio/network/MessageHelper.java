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

    private final String FS_CMD_START_SHOW = "start_show";
    private final String FS_CMD_DIAL_LISTENERS = "dial_listeners";
    private final String FS_CMD_MUTE_STATE = "mute_state";

    private final String FS_CREATE_SHOW="create_show";
    private final String FS_ADD_HOST = "add_host";
    private final String FS_CHECK_SHOW_STATUS = "check_show_status";
    private final String FS_GET_HOST = "get_host";
    private final String FS_GET_SHOW_ID = "get_show_id";
    private final String FS_ADD_PERSON = "add_person";
    private final String FS_SHOW_LISTENERS = "show_listeners";
    private final String FS_END_SHOW = "end_show";
    private final String FS_PLAY_TRAILER = "play_trailer";
    private final String FS_DELETE_TRAILER = "delete_trailer";
    private final String FS_FLUSH_CALLERS = "flush_callers";
    private final String FS_SHOW_GUESTS = "show_guests";

    private final String FS_DELETE_LISTENER = "del_listeners";
    private final String FS_DELETE_ALL = "del_all";
    private final String FS_SPREAD_WORD = "spread_word";
    private final String FS_CHECK_TRAILER_STATUS = "check_trailer_status";
    private final String FS_START_QUIZ = "start_quiz";
    private final String FS_STOP_QUIZ = "stop_quiz";
    private final String FS_SHOW_RESULTS = "show_results";
    private final String FS_QUIZ_DETAILS = "quiz_details";
    private final String FS_START_LISTENER_RATING = "start_listener_rating";
    private final String FS_STOP_LISTENER_RATING = "stop_listener_rating";
    private final String FS_CALL_REJECTED = "call_rejected";
    private final String FS_END_CONFERENCE = "end_conference";
    private final String FS_CANCEL_SHOW = "cancel_show";

    private final String FS_PLAY_PRERECORDED_MATERIAL = "play_recording";
    private final String FS_STOP_MEDIA = "stop_media";
    private final String FS_TRAILER_UPLOAD = "trailer_upload";
    private final String FS_CONTENT_UPLOAD = "content_upload";
    private final String FS_PLAY_CONTENT = "play_content";
    private final String FS_DELETE_CONTENT = "delete_content";

    /**
     * Commands for ivr library handle - add, play, record and delete
     */
    private final String FS_ADD_IVR_LIBRARY = "add_ivr_library";
    private final String FS_PLAY_PREVIOUS_IVR_INTRO = "play_previous_ivr_intro";
    private final String FS_RECORD_IVRLIBRARY_INTRO = "record_ivrlibrary_intro";
    private final String FS_DELETE_IVRLIBRARY_INTRO = "delete_ivrlibrary_intro";

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
        Log.v("dks","studio id updated in pref: "+this.studio_id);
        this.host_phone_num = preferences.getString(GlobalUtils.PREF_TELEPHONE_NUMBER, null);
        this.host_area_code = preferences.getString(GlobalUtils.PREF_AREA_CODE, null);
    }

    public void init(String session_id,String studio_id){
        this.session_id = session_id;
        this.studio_id = studio_id;
    }

    public String checkShowStatus() {
        return String.format("{\"cmd\":\"%s\" }",FS_CHECK_SHOW_STATUS);
    }

    public String getHost() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}",FS_GET_HOST, this.studio_id);
    }

    public String getShowId() {
        return String.format("{\"cmd\":\"%s\" }",FS_GET_SHOW_ID);
    }

    public String createShow(String date, String time, String category) {
        return String.format("{\"cmd\":\"%s\", \"date\" : \"%s\", \"time\" : \"%s\", \"category\" : \"%s\"}", FS_CREATE_SHOW, date, time, category);
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

    public String showGuests() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}",FS_SHOW_GUESTS, this.studio_id);
    }

    public String playTrailer() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_PLAY_TRAILER, this.studio_id);
    }

    public String deleteTrailer() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_DELETE_TRAILER, this.studio_id);
    }

    public String muteState(String uuid, String state){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"uuid\" : \"%s\", \"state\" : \"%s\" }", FS_CMD_MUTE_STATE, this.studio_id, uuid, state);
    }

    public String initShow(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"session\" : \"%s\" }", FS_CMD_START_SHOW,this.studio_id, this.session_id);
    }

    public String startShow(){
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_CMD_DIAL_LISTENERS,this.studio_id);
    }

    public String endShow() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_END_SHOW, this.studio_id);
    }

    public String flushCallers() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_FLUSH_CALLERS, this.studio_id);
    }

    public String deleteListener (String phone) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"phone\": \"%s\"}", FS_DELETE_LISTENER, this.studio_id, phone);
    }

    public String deleteCategoryListeners(String role_category) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"role\": \"%s\", \"role_category\" : \"%s\" }", FS_DELETE_ALL, this.studio_id, "LISTENER", role_category);
    }

    public String spreadWord(String date, String time, String listenerCategory) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"date\": \"%s\", \"time\" : \"%s\", \"listener_category\" : \"%s\" }", FS_SPREAD_WORD, this.studio_id, date, time, listenerCategory);
    }

    public String checkTrailerStatus() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_CHECK_TRAILER_STATUS, this.studio_id);
    }

    public String startQuiz(String quizId, String startTime) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"quiz_id\" : \"%s\", \"quiz_start_time\" : \"%s\"}", FS_START_QUIZ, this.studio_id, quizId, startTime);
    }

    public String stopQuiz(String stopTime) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"quiz_stop_time\" : \"%s\"}", FS_STOP_QUIZ, this.studio_id, stopTime);
    }

    public String showResults() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_SHOW_RESULTS, this.studio_id);
    }

    public String quizDetails(String quizId, String countOption, String correctOption) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"quiz_id\": \"%s\", \"count_option\" : \"%s\", \"correct_option\" : \"%s\" }", FS_QUIZ_DETAILS, this.studio_id, quizId, countOption, correctOption);
    }

    public String startListenerRating(String phoneNum) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"phone_number\" : \"%s\"}", FS_START_LISTENER_RATING, this.studio_id, phoneNum);
    }

    public String stopListenerRating(String phoneNum) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"phone_number\" : \"%s\"}", FS_STOP_LISTENER_RATING, this.studio_id, phoneNum);
    }

    public String callRejected() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"phone_number\" : \"%s\"}", FS_CALL_REJECTED, this.studio_id, this.host_phone_num);
    }

    /**
     * End conference if user directly closes the app from the recent screen and the show is running.
     */
    public String endConference() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_END_CONFERENCE, this.studio_id);
    }

    /**
     * Cancel the show when clicked on button in mainActivity for ending the show so that he can make a new show
     * without starting the current show.
     * This can arise if he has given wrong number while creating the show and then he didn't got call.
     */
    public String cancelShow() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_CANCEL_SHOW, this.studio_id);
    }

    /**
     *  Play preRecorded material from amazon s3 in ShowOverViewActivity
     *
     */
    public String playPrerecordedMaterial() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_PLAY_PRERECORDED_MATERIAL, this.studio_id);
    }

    /**
     *  Stop the preRecorded material play.
     */
    public String stopMedia() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_STOP_MEDIA, this.studio_id);
    }

    /**
     *  Command trailer upload
     */
    public String trailerUpload(String fileName) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"filename\" : \"%s\"}", FS_TRAILER_UPLOAD, this.studio_id, fileName);
    }

    /**
     * Command content upload
     */
    public String contentUpload(String fileName) {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\", \"filename\" : \"%s\"}", FS_CONTENT_UPLOAD, this.studio_id, fileName);
    }

    /**
     * Command play content
     * @return
     */
    public String playContent() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_PLAY_CONTENT, this.studio_id);
    }

    /**
     * Command delete content
     * @return
     */
    public String deleteContent() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_DELETE_CONTENT, this.studio_id);
    }

    /**
     * Command for ivr libraries, add, play, record and delete
     * @return
     */
    public String addIvrLibrary() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_ADD_IVR_LIBRARY, this.studio_id);
    }

    public String playPreviousIvrIntro() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_PLAY_PREVIOUS_IVR_INTRO, this.studio_id);
    }

    public String recordIvrLibraryIntro() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_RECORD_IVRLIBRARY_INTRO, this.studio_id);
    }

    public String deleteIvrLibraryIntro() {
        return String.format("{\"cmd\":\"%s\", \"studio\" : \"%s\"}", FS_DELETE_IVRLIBRARY_INTRO, this.studio_id);
    }

}
