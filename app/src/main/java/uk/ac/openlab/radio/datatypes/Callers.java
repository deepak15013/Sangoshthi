package uk.ac.openlab.radio.datatypes;

import java.util.ArrayList;

/**
 * Created by deepaksood619 on 17/6/16.
 */
public class Callers {
    String uuid;
    String phone_number;
    String current_studio;
    String conf_member_id;
    boolean mute_state;
    String current_role;

    public Callers(String uuid, String phone_number, String current_studio, String conf_member_id, boolean mute_state, String current_role) {
        this.uuid = uuid;
        this.phone_number = phone_number;
        this.current_studio = current_studio;
        this.conf_member_id = conf_member_id;
        this.mute_state = mute_state;
        this.current_role = current_role;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCurrent_studio() {
        return current_studio;
    }

    public void setCurrent_studio(String current_studio) {
        this.current_studio = current_studio;
    }

    public String getConf_member_id() {
        return conf_member_id;
    }

    public void setConf_member_id(String conf_member_id) {
        this.conf_member_id = conf_member_id;
    }

    public boolean isMute_state() {
        return mute_state;
    }

    public void setMute_state(boolean mute_state) {
        this.mute_state = mute_state;
    }

    public String getCurrent_role() {
        return current_role;
    }

    public void setCurrent_role(String current_role) {
        this.current_role = current_role;
    }
}
