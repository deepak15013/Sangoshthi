package uk.ac.openlab.radio.datatypes;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by Kyle Montague on 11/04/16.
 */
public class Studio  {

    int id = -1;
    String name = "";
    String joincode_guest;
    String joincode_presenter;
    String joincode_producer;
    String joincode_annotator;

    @Expose(deserialize = false)
    Scaffold scaffold;
    String starts;
    String ends;
    String tweetterms;
    String document;
    String createdAt;
    String updatedAt;
    String joincode_type;
    String sipurl;
    int sipport;
    String freeswitch_phonenumber =null;

    public static Studio fromJson(String json){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json,Studio.class);
    }

    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getFreeswitch_phonenumber(){
        return freeswitch_phonenumber;
    }
}
