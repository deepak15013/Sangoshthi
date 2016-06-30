package uk.ac.openlab.radio;

import android.app.Application;
import android.widget.Toast;

import uk.ac.openlab.radio.network.CloudStudioApi;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

/**
 * Created by Kyle Montague on 28/04/16.
 */
public class AgileRadioApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CloudStudioApi.shared().init(getApplicationContext());
        GlobalUtils.shared().init(getApplicationContext());
        MessageHelper.shared().init(getApplicationContext());
        FreeSwitchApi.shared().init(getApplicationContext());

    }
}
