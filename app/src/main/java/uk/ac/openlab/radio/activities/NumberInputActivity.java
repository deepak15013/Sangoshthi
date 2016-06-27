/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.activities;

import android.app.Activity;
import android.app.backup.RestoreObserver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.LocaleAdapter;
import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.CloudStudioApi;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageListenerHandler;
import uk.ac.openlab.radio.utilities.ContactManager;

public class NumberInputActivity extends AppCompatActivity {

    private static final String TAG = NumberInputActivity.class.getSimpleName();

    public static final int REQUEST_CODE = 1002;


    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    public static final String EXTRA_MODE = "EXTRA_MODE"; //adding guest, or adding listener
    public static final String EXTRA_RESULT_VALUE = "EXTRA_RESULT_VALUE"; //value being returned

    //todo need to add in some validation of the telephone numbers

    //todo could possibly add button(s) for 'add' and 'finished/done'

    EditText editText;
    TextView textView;
    ChecklistItemView toolbarItemView;
    Spinner localeSpinner;
    LocaleAdapter localeAdapter;

    InputMode mode;

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phoneNumber;
    String lang;

    RadioGroup rgRoleCategory;
    RadioButton rbRoleCategory;

    LinearLayout llRoleCategory;
    Button btnAddNumber;

    public enum InputMode {
        ADD_LISTENER,
        REMOVE_LISTENER,
        ADD_GUEST,
        SET_PIN,
        ENTER_PIN,
        ADD_PRESENTER,
        JOIN_CODE,
        PROMOTE
    }

    Boolean showRadio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_number_input);

        Log.d(TAG,"inside");

        llRoleCategory = (LinearLayout) findViewById(R.id.ll_role_category);

        rgRoleCategory = (RadioGroup) findViewById(R.id.rg_role_category);

        localeSpinner = (Spinner) findViewById(R.id.locale);

        showRadio = getIntent().getBooleanExtra("RADIO",true);

        btnAddNumber = (Button) findViewById(R.id.btn_add_number);

        if(!showRadio) {
            llRoleCategory.setVisibility(View.GONE);
        }

        editText = (EditText) findViewById(R.id.number_input);
        editText.requestFocus();

        btnAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    handleInput();

                    editText.setText("");


                }
                else{
                    //todo handle the null or invalid input
                    editText.setError(getString(isPin()?R.string.error_invalid_pin:R.string.error_invalid_number));
                    editText.requestFocus();
                }
            }
        });

        /*editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //todo validate number

                }
                return false;
            }
        });*/





        textView = (TextView) findViewById(R.id.tv_title_string);
        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            String msg = getString(extras.getInt(EXTRA_TEXT, R.string.number_input_enter_guest_number));
            int modeID = extras.getInt(EXTRA_MODE, InputMode.ADD_GUEST.ordinal());
            textView.setText(msg);

            mode = InputMode.values()[modeID];
            String title = extras.getString(MainActivity.EXTRA_TITLE_ITEM_TEXT, null);
            int icon = extras.getInt(MainActivity.EXTRA_TITLE_ITEM_ICON);
            boolean state = extras.getBoolean(MainActivity.EXTRA_TITLE_ITEM_STATE, false);
            if (title != null) {
                toolbarItemView.setTitle(title);
                toolbarItemView.setChecked(state);
                toolbarItemView.setIcon(icon);
            } else {
                toolbarItemView.setVisibility(View.INVISIBLE);
            }

        }

        if(isJoinCode() || isPin()){
            localeSpinner.setVisibility(View.GONE);
        }else {
            localeSpinner.setVisibility(View.VISIBLE);
            localeAdapter = new LocaleAdapter(this);
            localeSpinner.setAdapter(localeAdapter);
            localeSpinner.setSelection(localeAdapter.indexOfLocale(Locale.getDefault()));
        }

        if (isPin()) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }


    }


    private boolean isPin(){
        return (mode == InputMode.ENTER_PIN || mode == InputMode.SET_PIN);
    }

    private boolean isJoinCode(){
        return (mode == InputMode.JOIN_CODE);
    }


    private boolean isValid() {
        if(isPin())
            return validatePin();
        else if(isJoinCode()){
            return validateCode();
        }
        return validateNumber();
    }

    private boolean validatePin(){
        return false;
    }

    private boolean validateNumber(){
        Log.v("dks","is valid number");
        String number = editText.getText().toString();
        Locale locale = (Locale)localeSpinner.getSelectedItem();
        try {
            phoneNumber = phoneUtil.parse(number, locale.getCountry());
            lang = locale.getCountry();//todo change
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
        return phoneUtil.isValidNumber(phoneNumber);
    }

    boolean asyncWait = false;
    boolean joinstate = false;
    long timeout = 10000;
    private boolean validateCode() {
        String code = editText.getText().toString();
        Log.d(TAG,"validating code: "+code);
        if(code.length() > 0) {
            asyncWait = true;
            IMessageListener listener = new IMessageListener() {
                @Override
                public void success() {
                    asyncWait = false;
                    joinstate = true;
                    ContactManager.add(getApplicationContext(),GlobalUtils.shared().citizenRadioName(),GlobalUtils.shared().citizenRadioNumber());

                }

                @Override
                public void fail() {
                    asyncWait = false;
                    joinstate = false;
                }

                @Override
                public void error() {
                    asyncWait = false;
                    joinstate = false;
                }

                @Override
                public void message(String message) {
                    asyncWait = false;
                    joinstate = false;
                }
            };

            int counter = 0;
            joinStudio(listener);
            while (asyncWait){
                counter++;
                if(counter>=timeout)
                    return false;
            }
            return joinstate;
        }
        return false;
    }

    private void handleInput(){
        switch (mode){
            case ADD_PRESENTER:
                saveTelephoneNumber();
                break;
            case ADD_LISTENER:
                addCallerNumber(Caller.TYPE.LISTENER);
                break;
            case ADD_GUEST:
                addCallerNumber(Caller.TYPE.GUEST);
                break;
        }
    }


    private void saveTelephoneNumber(){
        GlobalUtils.shared().setPhoneNumber(""+phoneNumber.getNationalNumber());
        GlobalUtils.shared().setAreacode(""+phoneNumber.getCountryCode());
        GlobalUtils.shared().setLang(""+lang);
    }


    private void joinStudio(IMessageListener listener){
        CloudStudioApi.shared().join(editText.getText().toString(),GlobalUtils.shared().areacode(),GlobalUtils.shared().phoneNumber(),GlobalUtils.shared().lang(),listener);
    }


    private void addCallerNumber(Caller.TYPE role){
        //CloudStudioApi.shared().addPerson(GlobalUtils.shared().studioID(),""+phoneNumber.getNationalNumber(),""+phoneNumber.getCountryCode(),role.name(), role.name());

        int selectedId = rgRoleCategory.getCheckedRadioButtonId();
        rbRoleCategory = (RadioButton) findViewById(selectedId);

        String roleCategory = null;
        if(showRadio) {
            roleCategory = rbRoleCategory.getText().toString();
        }
        else if(role.name().equals("GUEST")) {
            roleCategory = "GUEST";
        }
        else if(role.name().equals("PRESENTER")) {
            roleCategory = "PRESENTER";
        }

        FreeSwitchApi.shared().addListener(new IMessageListener() {
            @Override
            public void success() {
                Log.v("tag", "listener added successfully");
                Toast.makeText(NumberInputActivity.this, "Number added successfully", Toast.LENGTH_SHORT).show();
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
        }, String.valueOf(phoneNumber.getCountryCode()), String.valueOf(phoneNumber.getNationalNumber()), role.name(), roleCategory);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(EXTRA_RESULT_VALUE,phoneNumber.getCountryCode()+""+phoneNumber.getNationalNumber());
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
