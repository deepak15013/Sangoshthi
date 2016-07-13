package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.ShowGuestsAdapter;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowGuestsActivity extends AppCompatActivity {

    private ArrayList<String> guestArrayList;
    private RecyclerView showGuestsRecyclerView;

    ChecklistItemView toolbarItemView;

    LinearLayoutManager linearLayoutManager;

    TextView tvNoGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_guests);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getResources().getString(R.string.show_guests_title));
        toolbarItemView.setIcon(R.drawable.ic_person);

        tvNoGuest = (TextView) findViewById(R.id.tv_no_guest);

        guestArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        showGuestsRecyclerView = (RecyclerView) findViewById(R.id.rv_show_guests);
        assert showGuestsRecyclerView != null;
        showGuestsRecyclerView.setLayoutManager(linearLayoutManager);
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
                parse(message);
            }
        });
    }

    private void parse(String message) {

        guestArrayList.clear();

        String[] guestNames = message.split(",");

        if(guestNames.length == 1 && guestNames[0].equals("")) {
            tvNoGuest.setVisibility(View.VISIBLE);
        }
        else {
            tvNoGuest.setVisibility(View.GONE);

            for(String names: guestNames) {
                if(names.equalsIgnoreCase("") || names.equalsIgnoreCase(" ")) {
                    continue;
                }
                guestArrayList.add(names);
            }

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
