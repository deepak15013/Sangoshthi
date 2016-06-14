package uk.ac.openlab.radio.datatypes;

import java.io.Serializable;

/**
 * Created by kylemontague on 15/03/16.
 */
public class AudioRecording implements Serializable{


    private String id;
    private String uri;

    public AudioRecording(){
        this.id  = null;
        this.uri = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    @Override
    public String toString() {
        return "AudioRecording [id="+this.id+", uri="+this.uri+"]";
    }
}
