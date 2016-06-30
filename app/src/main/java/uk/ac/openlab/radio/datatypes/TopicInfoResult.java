package uk.ac.openlab.radio.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylemontague on 03/06/16.
 */
public class TopicInfoResult {

    public int listeners;
    public List<Callers> callers;

    public TopicInfoResult(int listeners, List<Callers> callers) {
        this.listeners = listeners;
        this.callers = callers;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public List<Callers> getCallers() {
        return callers;
    }

    public void setCallers(List<Callers> callers) {
        this.callers = callers;
    }
}
