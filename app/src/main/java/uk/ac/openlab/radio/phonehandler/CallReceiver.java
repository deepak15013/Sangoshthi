package uk.ac.openlab.radio.phonehandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.activities.IvrLibraryActivity;
import uk.ac.openlab.radio.activities.MainActivity;
import uk.ac.openlab.radio.activities.ShowOverviewActivity;

/**
 * Created by deepaksood619 on 27/6/16.
 */
public class CallReceiver extends PhoneCallReceiver {

    private static boolean callReceived = false;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        super.onIncomingCallStarted(ctx, number, start);
        Log.v("dks","call incoming: "+number+" date start: "+start);

        if(ShowOverviewActivity.dismissThread.isAlive()) {
            ShowOverviewActivity.dismissThread.interrupt();
        }

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        super.onIncomingCallEnded(ctx, number, start, end);
        Log.v("dks","call incoming ended: "+number+" date start: "+start + "date end: "+end);
        if(number.contains(ctx.getString(R.string.server_number) )) {

            if(ShowOverviewActivity.alertDialog != null) {

                Log.v("dks","Call disconnected in showOverViewActivity");

                if(ShowOverviewActivity.alertDialog.isShowing()) {
                    ShowOverviewActivity.alertDialog.dismiss();
                }

                GlobalUtils.shared().setCallDisconnected(true);
                ShowOverviewActivity.finishActivity();
            }

        }

    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);


        if(state == 0 && number != null) {

            if(number.contains(context.getString(R.string.server_number)) && callReceived) {
                Log.v("dks","call disconnected in other activity");
                callReceived = false;
                if(ShowOverviewActivity.alertDialog != null) {
                        if(ShowOverviewActivity.alertDialog.isShowing()) {
                            ShowOverviewActivity.alertDialog.dismiss();
                        }
                    GlobalUtils.shared().setCallDisconnected(true);
                    ShowOverviewActivity.finishActivity();
                }

                if(MainActivity.alertDialogPlayTrailer != null) {
                    if(MainActivity.alertDialogPlayTrailer.isShowing()) {
                        MainActivity.alertDialogPlayTrailer.dismiss();
                    }
                }

                if(IvrLibraryActivity.ivrAlertDialog != null && IvrLibraryActivity.ivrAlertDialog.isShowing()) {
                    IvrLibraryActivity.ivrAlertDialog.dismiss();
                    IvrLibraryActivity.failThread.interrupt();
                }
            }
        }

        if(state == 1 && number != null) {
            if(number.contains(context.getString(R.string.server_number))) {

                callReceived = true;

            }
        }

        Log.v("dks","state changed: "+state + " number: "+number);
        if(state == 2 && number != null) {
            if(number.contains(context.getString(R.string.server_number))) {

                if(ShowOverviewActivity.alertDialog != null) {
                    if(ShowOverviewActivity.alertDialog.isShowing()) {
                        ShowOverviewActivity.alertDialog.dismiss();
                    }
                }

                if(MainActivity.alertDialogPlayTrailer != null) {
                    if(MainActivity.alertDialogPlayTrailer.isShowing()) {
                        MainActivity.alertDialogPlayTrailer.dismiss();
                    }
                }

                if(IvrLibraryActivity.ivrAlertDialog != null && IvrLibraryActivity.ivrAlertDialog.isShowing()) {
                    IvrLibraryActivity.ivrAlertDialog.dismiss();
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
