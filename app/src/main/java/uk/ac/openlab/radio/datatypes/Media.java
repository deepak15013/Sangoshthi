package uk.ac.openlab.radio.datatypes;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by kylemontague on 29/04/16.
 */
public class Media {


    public enum TYPE{
        call_leg,
        trailer,
        content,
        audiotag,
        music,
        icon
    }

    String studio;
    String title;
    String artist;
    String description;
    String language;
    String locale;
    String url;
    File file;
    TYPE type;


    public MediaType mediaType(){
        switch (type){
            case icon:
                return MediaType.parse("image/jpeg");
            default:
                return MediaType.parse("audio/aac");
        }
    }

    public Media(){
        type = TYPE.content;
    }


    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
}
