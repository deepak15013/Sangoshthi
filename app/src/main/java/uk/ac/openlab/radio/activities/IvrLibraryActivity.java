package uk.ac.openlab.radio.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * This activity lets user to hear previous ivr or add new show to the ivr
 */

public class IvrLibraryActivity extends AppCompatActivity implements View.OnClickListener{

    private static final long TWENTY_SECOND_CLOCK = 20 *1000;

    private ChecklistItemView toolbarItemView;

    private ImageButton ibRecordIvr;
    private ImageButton ibPlayPreviousIvr;
    private ImageButton ibDeleteIvr;
    private Button btnClose;

    public static AlertDialog ivrAlertDialog;
    public static Thread failThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivr_library);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getResources().getString(R.string.ivr_library_title));
        toolbarItemView.setIcon(R.drawable.ic_person);

        ibRecordIvr = (ImageButton) findViewById(R.id.ib_record_ivr);
        ibPlayPreviousIvr = (ImageButton) findViewById(R.id.ib_play_previous_ivr);
        ibDeleteIvr = (ImageButton) findViewById(R.id.ib_delete_ivr);
        btnClose = (Button) findViewById(R.id.btn_close);

        ibRecordIvr.setOnClickListener(this);
        ibPlayPreviousIvr.setOnClickListener(this);
        ibDeleteIvr.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_record_ivr:
                recordIvrLibraryIntro();
                break;

            case R.id.ib_play_previous_ivr:
                playPreviousIvr();
                break;

            case R.id.ib_delete_ivr:
                deleteIvrLibraryIntro();
                break;

            case R.id.btn_close:
                onBackPressed();
                break;
        }
    }

    /**
     * Sends a command with play_previous_ivr_intro to server and then waits for a phone call
     */
    private void playPreviousIvr() {

        AlertDialog.Builder builder = new AlertDialog.Builder(IvrLibraryActivity.this);
        builder.setMessage(R.string.dialog_call_waiting_ivr_dialog);
        builder.setCancelable(false);
        ivrAlertDialog = builder.create();
        ivrAlertDialog.show();
        ivrAlertDialog.setCancelable(false);

        failThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TWENTY_SECOND_CLOCK);
                    if(ivrAlertDialog.isShowing()) {
                        ivrAlertDialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        failThread.start();

        FreeSwitchApi.shared().playPreviousIvrIntro(new IMessageListener() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                ivrAlertDialog.dismiss();

                AlertDialog.Builder failDialogBuilder = new AlertDialog.Builder(IvrLibraryActivity.this);
                failDialogBuilder.setMessage(R.string.dialog_no_previous_recording);
                failDialogBuilder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog failDialog = failDialogBuilder.create();
                failDialog.show();

            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {

            }
        });
    }

    private void recordIvrLibraryIntro() {

        AlertDialog.Builder builder = new AlertDialog.Builder(IvrLibraryActivity.this);
        builder.setMessage(R.string.dialog_call_waiting_ivr_dialog);
        builder.setCancelable(false);
        ivrAlertDialog = builder.create();
        ivrAlertDialog.show();
        ivrAlertDialog.setCancelable(false);

        failThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TWENTY_SECOND_CLOCK);
                    if(ivrAlertDialog.isShowing()) {
                        ivrAlertDialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        failThread.start();

        FreeSwitchApi.shared().recordIvrLibraryIntro(new IMessageListener() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                Toast.makeText(IvrLibraryActivity.this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {

            }
        });
    }

    private void deleteIvrLibraryIntro() {
        FreeSwitchApi.shared().deleteIvrLibraryIntro(new IMessageListener() {
            @Override
            public void success() {
                Toast.makeText(IvrLibraryActivity.this, R.string.toast_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail() {
                Toast.makeText(IvrLibraryActivity.this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
