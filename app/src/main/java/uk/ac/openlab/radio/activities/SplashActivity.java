package uk.ac.openlab.radio.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

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

        GlobalUtils.shared().setCallDisconnected(false);

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

    private void getSetup() {

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
                    // start further process
                    finish();
                    Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("STATUS","insert");
                    startActivity(i);
                }

                @Override
                public void fail() {
                    showUpdateNumberDialog();
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

                    // no show running, insert new host
                    finish();
                    Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("STATUS","insert");
                    startActivity(i);
                }

                @Override
                public void fail() {

                    FreeSwitchApi.shared().getHost(new IMessageListener() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void fail() {
                            // insert new user, database is empty
                            finish();
                            Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("STATUS","insert");
                            startActivity(i);
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {
                            Log.v("dks: ",message);
                            if(message.equals(GlobalUtils.shared().phoneNumber())) {
                                startPreparation();
                            }
                            else {
                                showUpdateNumberDialog();
                            }

                        }
                    });
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

    private void showUpdateNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setMessage(R.string.dialog_update_number);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                Intent i = new Intent(SplashActivity.this, SetupActivity.class);
                i.putExtra("STATUS","update");
                startActivity(i);
            }
        });

        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create();
        builder.show();
    }
}
