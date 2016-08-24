package uk.ac.openlab.radio.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class SpreadTheWordActivity extends AppCompatActivity {

    private ChecklistItemView toolbarItemView;

    private static EditText etSpreadDate, etSpreadTime;

    RadioGroup rgSpreadWordCategory;

    private int year, month, day, hour, minute;
    private Calendar calendar;

    String roleCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread_the_word);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getString(R.string.spread_the_word_title));

        etSpreadDate = (EditText) findViewById(R.id.et_spread_date);
        etSpreadTime = (EditText) findViewById(R.id.et_spread_time);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        showdate(year, month, day);
        showTime(hour, minute);

        rgSpreadWordCategory = (RadioGroup) findViewById(R.id.rg_spread_word_category);

    }

    public static void showdate(int year, int month, int day) {
        month = month+1;
        etSpreadDate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public static void showTime(int hourOfDay, int minute) {
        etSpreadTime.setText(new StringBuilder().append(hourOfDay).append(":").append(minute));
    }

    public void spreadDateChooser(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            showdate(year, monthOfYear,dayOfMonth);
        }
    };

    public void spreadTimeChooser(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void spreadAddMore(View view) {
        Toast.makeText(SpreadTheWordActivity.this, getString(R.string.toast_add_more), Toast.LENGTH_SHORT).show();

        int selectedId = rgSpreadWordCategory.getCheckedRadioButtonId();
        if(selectedId == R.id.rb_spread_asha) {
            roleCategory = "ASHA";
        }
        else if(selectedId == R.id.rb_spread_others) {
            roleCategory = "OTHERS";
        }
        else {
            roleCategory = "ALL";
        }

        FreeSwitchApi.shared().spreadWord(new IMessageListener() {
            @Override
            public void success() {
                Toast.makeText(SpreadTheWordActivity.this, getString(R.string.toast_broadcast_time_saved), Toast.LENGTH_SHORT).show();
                etSpreadDate.setText("");
                etSpreadTime.setText("");
            }

            @Override
            public void fail() {

            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {

            }
        }, etSpreadDate.getText().toString(), etSpreadTime.getText().toString(), roleCategory.toUpperCase());

    }

    public void spreadSubmit(View view) {
        Toast.makeText(SpreadTheWordActivity.this, getString(R.string.toast_submit), Toast.LENGTH_SHORT).show();

        int selectedId = rgSpreadWordCategory.getCheckedRadioButtonId();
        if(selectedId == R.id.rb_asha) {
            roleCategory = "ASHA";
        }
        else if(selectedId == R.id.rb_spread_others) {
            roleCategory = "OTHERS";
        }
        else {
            roleCategory = "ALL";
        }

        FreeSwitchApi.shared().spreadWord(new IMessageListener() {
            @Override
            public void success() {
                Toast.makeText(SpreadTheWordActivity.this, getString(R.string.toast_broadcast_time_saved), Toast.LENGTH_SHORT).show();
                etSpreadDate.setText("");
                etSpreadTime.setText("");
                finish();
            }

            @Override
            public void fail() {

            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {

            }
        }, etSpreadDate.getText().toString(), etSpreadTime.getText().toString(), roleCategory.toUpperCase());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
