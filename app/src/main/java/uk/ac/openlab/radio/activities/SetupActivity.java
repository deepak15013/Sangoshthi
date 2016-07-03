package uk.ac.openlab.radio.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.fragments.NumberFragment;
import uk.ac.openlab.radio.network.CloudStudioApi;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

/**
 * Created by Kyle Montague on 16/05/16.
 */
public class SetupActivity extends AppIntro2 implements NumberFragment.OnFragmentInteractionListener{

    private int index = 0;
    private String status;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        status = getIntent().getStringExtra("STATUS");

        GlobalUtils.shared().init(getApplicationContext());
        addSlide(NumberFragment.newInstance(getResources().getString(R.string.app_intro_slide1), NumberInputActivity.InputMode.ADD_PRESENTER));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.app_intro_slide2_1),getResources().getString(R.string.app_intro_slide2_2), R.drawable.ic_radio, Color.BLACK));

        showStatusBar(true);
        setNavBarColor("#3F51B5");
        setSwipeLock(true);
        setProgressButtonEnabled(false);
    }

    @Override
    public void onNextPressed() {
        index++;
        pager.setCurrentItem(index);
    }

    @Override
    public void onDonePressed() {
        finish();

        if(status.equalsIgnoreCase("insert")) {
            Intent i = new Intent(this, CreateShow.class);
            i.putExtra("STATUS",status);
            startActivity(i);
        }
        else if(status.equalsIgnoreCase("update")) {
            MessageHelper.shared().init(getApplicationContext());
            CloudStudioApi.shared().init(getApplicationContext());
            FreeSwitchApi.shared().init(getApplicationContext());

            FreeSwitchApi.shared().createHost(new IMessageListener() {
                @Override
                public void success() {

                    FreeSwitchApi.shared().getShowId(new IMessageListener() {
                        @Override
                        public void success() {
                            Log.v("tag","getShowId successful");

                        }

                        @Override
                        public void fail() {
                            Log.v("tag","getShowId fail");
                        }

                        @Override
                        public void error() {
                            Log.v("tag","getShowId error");
                        }

                        @Override
                        public void message(String message) {
                            if(message != null) {

                                Boolean updated = GlobalUtils.shared().setStudioID(message);
                                if(updated) {
                                    Log.v("dks","studioid updated successfully");
                                }

                                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                                e.putBoolean("firstStart", false);
                                e.apply();
                                finish();

                                Intent i = new Intent(SetupActivity.this, MainActivity.class);

                                finish();
                                startActivity(i);
                            }
                        }
                    });
                }

                @Override
                public void fail() {
                    Log.v("tag", "host updation failed");
                }

                @Override
                public void error() {
                    Log.v("tag", "host updation error");
                    finish();
                }

                @Override
                public void message(String message) {
                    Log.v("tag", "message: "+message);
                }
            }, status);
        }

    }

    @Override
    public void onSlideChanged() {
        if(index == this.getSlides().size()-1)
            setProgressButtonEnabled(true);
        else{
            setProgressButtonEnabled(false);
        }
    }

    @Override
    public void done(NumberInputActivity.InputMode mode) {
        onNextPressed();
    }

    @Override
    public void onBackPressed() {
        if(index == 0) {
            super.onBackPressed();
        }else{
            index--;
            pager.setCurrentItem(index);
        }
    }
}
