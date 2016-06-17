package uk.ac.openlab.radio.datatypes;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by kylemontague on 01/06/16.
 */
public class CallerListResult extends Hashtable<String, Caller>{

    //callers
    //listeners

    public ArrayList<Caller> getList() {
        ArrayList<Caller> callers = new ArrayList<Caller>(this.values());
        ArrayList<String> keys = new ArrayList<String>(this.keySet());

        for(int x=0;x<callers.size();x++){
            callers.get(x).setID(keys.get(x));
        }

        return callers;
    }
}
