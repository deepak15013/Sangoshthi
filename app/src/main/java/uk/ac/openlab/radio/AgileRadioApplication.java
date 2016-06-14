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

    String host;
    int port;
    FreeSwitchApi freeSwitchApi;
    CloudStudioApi cloud;

    @Override
    public void onCreate() {
        super.onCreate();


        // FIXME CHECK NETWORK STATUS
        //initialise the global utils with the current context.
        CloudStudioApi.shared().init(getApplicationContext());
//        CloudStudioApi.shared().join("chicken","0044","7401233454");//todo remove this.
        GlobalUtils.shared().init(getApplicationContext());
        MessageHelper.shared().init(getApplicationContext());
        FreeSwitchApi.shared().init(getApplicationContext());

//
//        host = getString(R.string.pref_studio_host);
//        port = getResources().getInteger(R.integer.pref_studio_control_port);
//        //zmq = new ZeroMQMessageTask();
    }


    IMessageListener authListener = new IMessageListener() {
        @Override
        public void success() {
            Toast.makeText(getApplicationContext(),"Authenticated",Toast.LENGTH_SHORT).show();
            freeSwitchApi.prepareShow(prepListener);
        }

        @Override
        public void fail() {
            Toast.makeText(getApplicationContext(),"Failed to Auth",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void error() {
            Toast.makeText(getApplicationContext(),"Error with Auth command",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void message(String message) {

        }
    };
    IMessageListener prepListener = new IMessageListener() {
        @Override
        public void success() {
            Toast.makeText(getApplicationContext(),"Initiated Prep",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void fail() {
            Toast.makeText(getApplicationContext(),"Failed to init prep",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void error() {

        }

        @Override
        public void message(String message) {

        }
    };
}
