package uk.ac.openlab.radio.phonehandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import uk.ac.openlab.radio.activities.MainActivity;
import uk.ac.openlab.radio.activities.ShowOverviewActivity;

/**
 * Created by deepaksood619 on 27/6/16.
 */
public class CallReceiver extends PhoneCallReceiver {

    private static boolean callPickedUp = false;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        super.onIncomingCallStarted(ctx, number, start);
        Log.v("dks","call incoming: "+number+" date start: "+start);

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        super.onIncomingCallEnded(ctx, number, start, end);
        Log.v("dks","call incoming ended: "+number+" date start: "+start + "date end: "+end);
        if(number.contains("8860244278")) {

            ShowOverviewActivity.alertDialog.dismiss();
            ShowOverviewActivity.finishActivity();
        }

    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);

        Log.v("dks","state changed: "+state + " number: "+number);
        if(state == 2 && number != null) {
            if(number.contains("8860244278")) {

                if(ShowOverviewActivity.alertDialog != null) {
                    if(ShowOverviewActivity.alertDialog.isShowing()) {

                        callPickedUp = true;
                        ShowOverviewActivity.callReceived = true;
                        ShowOverviewActivity.alertDialog.dismiss();
                    }
                }
                if(MainActivity.alertDialogRecordTrailer != null) {
                    if(MainActivity.alertDialogRecordTrailer.isShowing()) {
                        MainActivity.alertDialogRecordTrailer.dismiss();
                    }
                }

                if(MainActivity.alertDialogPlayTrailer != null) {
                    if(MainActivity.alertDialogPlayTrailer.isShowing()) {
                        MainActivity.alertDialogPlayTrailer.dismiss();
                    }
                }
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        super.onOutgoingCallStarted(ctx, number, start);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        super.onOutgoingCallEnded(ctx, number, start, end);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        super.onMissedCall(ctx, number, start);
    }
}
