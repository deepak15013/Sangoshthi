package uk.ac.openlab.radio.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.CloudStudioApi;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

public class CreateShow extends AppCompatActivity {

    private static EditText editText;
    private static EditText etTime;

    private DatePicker datePicker;
    private int year, month, day, hour, minute;
    private Calendar calendar;

    FreeSwitchApi freeSwitchApi;
    IMessageListener prepListener;

    String status;

    private ChecklistItemView toolbarItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_show);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Create Show");
        toolbarItemView.hideCheckbox(true);

        status = getIntent().getStringExtra("STATUS");

        editText = (EditText) findViewById(R.id.editText);
        etTime = (EditText) findViewById(R.id.et_time);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        showdate(year, month, day);
        showTime(hour, minute);
    }

    public static void showdate(int year, int month, int day) {
        month = month+1;
        editText.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public void dateChooser(View view) {
        Log.v("TAG", "dateChooser");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            showdate(year, monthOfYear,dayOfMonth);
        }
    };

    public void timeChooser(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static void showTime(int hourOfDay, int minute) {
        etTime.setText(new StringBuilder().append(hourOfDay).append(":").append(minute));
    }

    public void createOnClick(View view) {
        Toast.makeText(this, "Create Show", Toast.LENGTH_SHORT).show();

        final Intent intent = new Intent(this, MainActivity.class);

        MessageHelper.shared().init(getApplicationContext());
        CloudStudioApi.shared().init(getApplicationContext());
        FreeSwitchApi.shared().init(getApplicationContext());

        FreeSwitchApi.shared().createHost(new IMessageListener() {
            @Override
            public void success() {
                Log.v("tag", "host created successfully");
            }

            @Override
            public void fail() {
                Log.v("tag", "host creation failed");
            }

            @Override
            public void error() {
                Log.v("tag", "host creation error");
            }

            @Override
            public void message(String message) {
                Log.v("tag", "message: "+message);
            }
        }, status);

        FreeSwitchApi.shared().createShow(new IMessageListener() {
            @Override
            public void success() {

                // -dks
                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                e.putBoolean("firstStart", false);
                e.apply();

                finish();
                Log.v("tag", "success");
                startActivity(intent);
            }

            @Override
            public void fail() {
                Log.v("tag", "fail");
            }

            @Override
            public void error() {
                Log.v("tag", "error");
            }

            @Override
            public void message(String message) {
                Log.v("tag", "message "+message);
                if(message.contains("show_")) {

                    GlobalUtils.shared().setStudioID(message);

                    SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                    finish();

                    startActivity(intent);

                }
            }
        }, editText.getText().toString(), etTime.getText().toString());

    }

    /**
     * Created by deepaksood619 on 20/6/16.
     */
    static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            showdate(year, month, day);
        }
    }

    /**
     * Created by deepaksood619 on 20/6/16.
     */
    static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            showTime(hourOfDay, minute);
        }
    }
}

