package uk.ac.openlab.radio.datatypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by kylemontague on 21/03/16.
 */
public class Caller {

    private String uuid;
    private TYPE type;
    private boolean mute_state = true;
    private String topic;

    private String current_role;

    private String user;
//    private int status;
//    private int redialcount;
//    private int current_studio;
//    private int old_state;
//    private int conf_member_id;
//    private boolean mute_state;



    private long timestamp;

    public enum TYPE{
        LISTENER,
        GUEST,
        PRESENTER,
        PRODUCER,
        TAGGER
    };


    public static Caller fromJson(String json){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json,Caller.class);
    }

    public Caller(String uuid, TYPE type, String topic) {
        this.uuid = uuid;
        this.type = type;
        this.topic = topic;

        if(type == TYPE.PRESENTER || type == TYPE.GUEST)
            this.mute_state = false;
    }

    public Caller(String uuid, long timestamp, String topic) {
        this.uuid = uuid;
        this.type = TYPE.LISTENER;
        this.topic = topic;
        this.timestamp = timestamp;

        if(type == TYPE.PRESENTER || type == TYPE.GUEST)
            this.mute_state = false;
    }

    public String getID() {
        return uuid;
    }

    public void setID(String ID) {
        this.uuid = ID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }



    public boolean isMutestate() {
        return mute_state;
    }

    public void setMutestate(boolean mutestate) {
        this.mute_state = mutestate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TYPE getType() {
        if(current_role != null){
            this.type = TYPE.valueOf(current_role);
        }
        return type;
    }

    public void setType(TYPE type) {

        this.type = type;
    }
}
