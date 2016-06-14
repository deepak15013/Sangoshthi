package uk.ac.openlab.radio.services;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.util.Date;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.activities.ShowOverviewActivity;

/**
 * Created by Kyle Montague on 05/05/16.
 */
public class FreeSwitchCallReceiver extends CallHandlerReceiver {



    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        GlobalUtils.shared().init(ctx);

        if(GlobalUtils.shared().citizenRadioNumber().equalsIgnoreCase(number))
            Toast.makeText(ctx,"Incoming call from FreeSwitch",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onIncomingCallAnswered(final Context ctx, String number, Date start) {
        GlobalUtils.shared().init(ctx);

        if(GlobalUtils.shared().citizenRadioNumber().equalsIgnoreCase(number)) {
            Toast.makeText(ctx, "FreeSwitch Call Answered", Toast.LENGTH_LONG).show();
            Handler callActionHandler = new Handler();
            Runnable runShowActivity = new Runnable() {
                @Override
                public void run() {
                    Intent intentPhoneCall = new Intent(ctx, ShowOverviewActivity.class);
                    intentPhoneCall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPhoneCall.putExtra(ShowOverviewActivity.EXTRA_SHOULD_DIAL,false);
                    ctx.startActivity(intentPhoneCall);
                }
            };
            callActionHandler.postDelayed(runShowActivity, 500);
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        GlobalUtils.shared().init(ctx);

        if(GlobalUtils.shared().citizenRadioNumber().equalsIgnoreCase(number))
            Toast.makeText(ctx,"FreeSwitch Disconnected ",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }
}
