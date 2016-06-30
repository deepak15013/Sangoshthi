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

    @Expose(deserialize = false)
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
