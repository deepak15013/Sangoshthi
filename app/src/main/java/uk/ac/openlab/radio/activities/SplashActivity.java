package uk.ac.openlab.radio.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by Kyle Montague on 13/04/16.
 */
public class SplashActivity extends Activity {

    private static final int MY_PERMISSION_REQUEST_PHONE = 101;
    private static final int MY_PERMISSION_REQUEST_EXTERNAL_STORAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_PHONE);
        } else if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_EXTERNAL_STORAGE);
        } else {
            getSetup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_PHONE: {
                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    } else {
                        getSetup();
                    }
                } else {
                    finish();
                }
                return;
            }

            case MY_PERMISSION_REQUEST_EXTERNAL_STORAGE: {
                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getSetup();

                } else {
                    finish();
                }
                return;
            }
        }
    }

    private void getSetup(){

        Locale locale = new Locale("hi");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);

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

        } else{

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
                    startPreparation();
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

    protected void startPreparation(){
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
