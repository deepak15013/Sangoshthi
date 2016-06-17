package uk.ac.openlab.radio.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by Kyle Montague on 13/04/16.
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();

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

                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {

                    FreeSwitchApi.shared().checkShowStatus(new IMessageListener() {
                        @Override
                        public void success() {
                            Log.v("tag","success no show already registered");

                            // start further process
                            finish();
                            Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("STATUS","insert");
                            startActivity(i);
                        }

                        @Override
                        public void fail() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage("Show already registered. Do you want to update your number?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();

                                    Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                                    i.putExtra("STATUS","update");
                                    startActivity(i);
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                            builder.create();
                            builder.show();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                }else{
                    startPreparation();
                }


        // Start the thread
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
