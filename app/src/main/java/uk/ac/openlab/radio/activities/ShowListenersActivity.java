package uk.ac.openlab.radio.activities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.ShowListenersAdapter;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowListenersActivity extends AppCompatActivity {

    ArrayList<String> ashaArrayList, othersArrayList;

    ChecklistItemView toolbarItemView;

    ListView lvShowListeners;

    ShowListenersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_listeners);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Show Listeners");
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        ashaArrayList = new ArrayList<>();
        othersArrayList = new ArrayList<>();

        ashaArrayList.add("9425592627");
        ashaArrayList.add("7587034008");

        lvShowListeners = (ListView) findViewById(R.id.lv_show_listeners);

        getListeners();
        //showListView();
    }

    private void getListeners() {
        FreeSwitchApi.shared().showListeners(new IMessageListener() {
            @Override
            public void success() {
                Log.v("tag", "listener list received");
            }

            @Override
            public void fail() {
                Log.v("tag", "failed");
            }

            @Override
            public void error() {
                Log.v("tag", "error");
            }

            @Override
            public void message(String message) {
                Log.v("tag", "message: "+message);
                parse(message);
                showListView();
            }
        });
    }

    public void removeListener (View v) {
        String itemToRemove = (String)v.getTag();
        adapter.remove(itemToRemove);
    }

    private void showListView() {
        adapter = new ShowListenersAdapter(ShowListenersActivity.this, R.layout.show_listeners_item, ashaArrayList);
        lvShowListeners.setAdapter(adapter);
    }

    private void parse(String message) {
        try {
            JSONObject rootObj = new JSONObject(message);

            JSONArray rootArr = rootObj.optJSONArray("results");

            for(int i=0; i<rootArr.length(); i++) {
                JSONObject categoryObject = rootArr.getJSONObject(i);
                JSONArray ashaListenersArr = categoryObject.optJSONArray("ASHA");
                JSONArray otherListenersArr = categoryObject.optJSONArray("OTHERS");

                if(ashaListenersArr != null) {
                    for(int j=0;j<ashaListenersArr.length(); j++) {
                        JSONObject listenerObject = ashaListenersArr.getJSONObject(j);
                        String phoneNum = listenerObject.optString("phone");
                        Log.v("dks", phoneNum);
                        ashaArrayList.add(phoneNum);
                    }
                }

                if(otherListenersArr != null) {
                    for(int j=0;j<otherListenersArr.length(); j++) {
                        JSONObject listenerObject = otherListenersArr.getJSONObject(j);
                        String phoneNum = listenerObject.optString("phone");
                        Log.v("dks", phoneNum);
                        othersArrayList.add(phoneNum);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
