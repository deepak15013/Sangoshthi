package uk.ac.openlab.radio.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;

/**
 * Created by Kyle Montague on 13/04/16.
 */
public class SplashActivity extends Activity {


    private static final int REQUEST_JOIN = 0001;
    private static final int REQUEST_PHONE = 0002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        getSetup();

    }

    private void getSetup(){

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                //  If the activity has never started before...
                finish();
                if (isFirstStart) {
                    Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    startPreparation();
                }
            }
        });

        // Start the thread
        t.start();
    }


    private void getPhoneNumber(){
        if(GlobalUtils.shared().phoneNumber() != null && GlobalUtils.shared().studioID() != null && GlobalUtils.shared().sessionID() != null) {
            Intent i = new Intent(this, NumberInputActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(NumberInputActivity.EXTRA_TEXT, R.string.text_presenter_telephone_number);
            i.putExtra(NumberInputActivity.EXTRA_MODE, NumberInputActivity.InputMode.ADD_PRESENTER.ordinal());
            startActivity(i);
        }
    }

    private void getJoincode(){
        if(GlobalUtils.shared().sessionID() == null || GlobalUtils.shared().studioID() == null) {
            Intent i = new Intent(this, NumberInputActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(NumberInputActivity.EXTRA_TEXT, R.string.text_enter_joincode);
            i.putExtra(NumberInputActivity.EXTRA_MODE, NumberInputActivity.InputMode.JOIN_CODE.ordinal());
            startActivity(i);
        }
    }


    protected void startPreparation(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode){
            case REQUEST_PHONE:
                //todo store the number / deal with it
                Toast.makeText(getApplicationContext(),"BACK TO THE TOP - PHONE",Toast.LENGTH_SHORT).show();

                break;
            case REQUEST_JOIN:
                Toast.makeText(getApplicationContext(),"BACK TO THE TOP - JOIN",Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
