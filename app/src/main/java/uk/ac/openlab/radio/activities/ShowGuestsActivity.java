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
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowGuestsActivity extends AppCompatActivity {

    private ArrayList<String> guestArrayList;
    private RecyclerView showGuestsRecyclerView;

    ChecklistItemView toolbarItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_guests);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Show Guests");
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        guestArrayList = new ArrayList<>();
        showGuestsRecyclerView = (RecyclerView) findViewById(R.id.rv_show_guests);

        getGuests();

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
                parse(message);
            }
        });
    }

    private void parse(String message) {

        String[] guestNames = message.split(",");
        for(String names: guestNames) {
            Log.v("dks","names: "+names);
            guestArrayList.add(names);
            showGuests();
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
