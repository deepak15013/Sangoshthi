package uk.ac.openlab.radio.datatypes;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.ColorRes;

import java.io.Serializable;

import uk.ac.openlab.radio.R;

/**
 * Created by kylemontague on 15/03/16.
 */
public class Topic extends BaseObservable implements Serializable {

    private String id;
    private String url;
    private String name;
    private Icon icon;
    private @ColorRes int tint;


    public Topic() {
    }

    public Topic(String name, Icon icon){
        this.name = name;
        this.icon = icon;
        this.tint = R.color.grey;
    }

    public Topic(String name, Icon icon, @ColorRes int tint){
        this.name = name;
        this.icon = icon;
        this.tint = tint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public int getTint() {
        return tint;
    }

    public void setTint(int tint) {
        this.tint = tint;
    }


    @Override
    public String toString() {
        return "Topic [id="+this.id+", url="+this.url+", name="+this.name+", tint="+this.tint+", icon=["+this.icon.toString()+"]]";
    }


}
