package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.ShowGuestsAdapter;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowGuestsActivity extends AppCompatActivity {

    private ArrayList<String> guestArrayList;
    private RecyclerView showGuestsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_guests);

        guestArrayList = new ArrayList<>();
        showGuestsRecyclerView = (RecyclerView) findViewById(R.id.rv_show_guests);

        //getGuests();

    }

    private void getGuests() {
        FreeSwitchApi.shared().showGuests(new IMessageListener() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {

            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {
                Log.v("dks", "message: "+message);
                //parse(message);
            }
        });
    }

    private void parse(String message) {
        try {
            JSONObject rootObj = new JSONObject(message);

            JSONArray rootArr = rootObj.optJSONArray("results");

            for(int i=0; i<rootArr.length(); i++) {
                JSONObject categoryObject = rootArr.getJSONObject(i);
                JSONArray ashaListenersArr = categoryObject.optJSONArray("CALLERS");

                if(ashaListenersArr != null) {
                    for(int j=0;j<ashaListenersArr.length(); j++) {
                        JSONObject listenerObject = ashaListenersArr.getJSONObject(j);
                        String phoneNum = listenerObject.optString("phone");
                        Log.v("dks", phoneNum);
                        guestArrayList.add(phoneNum);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showGuests() {
        ShowGuestsAdapter showGuestsAdapter = new ShowGuestsAdapter(guestArrayList);
        showGuestsRecyclerView.setAdapter(showGuestsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
