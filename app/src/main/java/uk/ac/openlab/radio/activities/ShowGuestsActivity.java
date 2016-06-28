package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowGuestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_guests);

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

            }
        });
    }


}
