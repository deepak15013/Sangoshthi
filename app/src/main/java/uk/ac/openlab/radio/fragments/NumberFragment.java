package uk.ac.openlab.radio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.activities.NumberInputActivity;
import uk.ac.openlab.radio.adapters.LocaleAdapter;
import uk.ac.openlab.radio.network.CloudStudioApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.utilities.ContactManager;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link NumberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NumberFragment extends Fragment {

    EditText editText;
    TextView textView;
    Spinner localeSpinner;
    LocaleAdapter localeAdapter;
    String lang;

    NumberInputActivity.InputMode mode = NumberInputActivity.InputMode.ADD_PRESENTER;

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phoneNumber;


    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_TEXT = "text";
    private static final String ARG_MODE = "mode";

    // TODO: Rename and change types of parameters
    private String text;
    private String modeID;

    private OnFragmentInteractionListener mListener;

    public NumberFragment() {
        // Required empty public constructor
    }



    public static NumberFragment newInstance(String text, NumberInputActivity.InputMode mode) {
        NumberFragment fragment = new NumberFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putString(ARG_MODE,mode.name());
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_TEXT);
            modeID = getArguments().getString(ARG_MODE);
        }
    }

    private boolean isPin(){
        return (mode == NumberInputActivity.InputMode.ENTER_PIN || mode == NumberInputActivity.InputMode.SET_PIN);
    }

    private boolean isJoinCode(){
        return (mode == NumberInputActivity.InputMode.JOIN_CODE);
    }


    private boolean isValid() {
        if(isPin())
            return validatePin();
        else if(isJoinCode())
            return validateCode();
        return validateNumber();
    }

    boolean asyncWait = false;
    boolean joinstate = false;
    int timeout = 10;
    int counter = 0;
    private boolean validateCode() {
        String code = editText.getText().toString();
        joinstate = false;
        if(code.length() > 0) {
            asyncWait = true;
            IMessageListener listener = new IMessageListener() {
                @Override
                public void success() {
                    joinstate = true;
                    asyncWait = false;
                    counter = timeout;
                    ContactManager.add(getContext(),GlobalUtils.shared().citizenRadioName(),GlobalUtils.shared().citizenRadioNumber());
                }

                @Override
                public void fail() {
                    asyncWait = false;
                    joinstate = false;
                    counter = timeout;
                }

                @Override
                public void error() {
                    asyncWait = false;
                    joinstate = false;
                    counter = timeout;
                }

                @Override
                public void message(String message) {
                    asyncWait = false;
                    joinstate = false;
                    counter = timeout;
                }
            };

            counter = 0;
            joinStudio(listener);
            while (counter<=timeout){
                try {
                    Thread.sleep(500L);
                    counter++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return joinstate;
                }
            }
        }
        return joinstate;
    }

    private boolean validatePin(){
        return false;
    }

    private boolean validateNumber(){
        String number = editText.getText().toString();
        Locale locale = (Locale)localeSpinner.getSelectedItem();
        try {
            phoneNumber = phoneUtil.parse(number, "IN");
            lang = locale.getLanguage();
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
        return phoneUtil.isValidNumber(phoneNumber);
    }


    private void handleInput(){
        switch (mode){
            case ADD_PRESENTER:
                saveTelephoneNumber();
                break;
        }
    }

    private void saveTelephoneNumber(){
        GlobalUtils.shared().setPhoneNumber(""+phoneNumber.getNationalNumber());
        GlobalUtils.shared().setAreacode(""+phoneNumber.getCountryCode());
        GlobalUtils.shared().setLang(""+lang);
    }

    private void joinStudio(IMessageListener listener){
        CloudStudioApi.shared().join(editText.getText().toString(),GlobalUtils.shared().areacode(),GlobalUtils.shared().phoneNumber(), GlobalUtils.shared().lang(),listener);
    }

    private @StringRes int errorMessage(){
        if(editText.getText().length() == 0){
            return R.string.error_null;
        }else if(isPin()){
            return R.string.error_invalid_pin;
        }else if(isJoinCode()){
            return R.string.error_invalid_joincode;
        }else{
            return R.string.error_invalid_number;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_number, container, false);

        localeSpinner = (Spinner) view.findViewById(R.id.locale);

        editText = (EditText) view.findViewById(R.id.number_input);
        editText.requestFocus();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(isValid()){
                        handleInput();
                        if (mListener != null) {
                            mListener.done(mode);
                        }

                    }else{
                        //todo handle the null or invalid input
                        editText.setError(getString(errorMessage()));
                        editText.requestFocus();
                    }
                }
                return false;
            }
        });


        textView = (TextView) view.findViewById(R.id.title);
        textView.setText(text);


        mode = NumberInputActivity.InputMode.valueOf(modeID);


        if(isJoinCode() || isPin()){
            localeSpinner.setVisibility(View.GONE);
        }else {
            //localeSpinner.setVisibility(View.VISIBLE);
            localeAdapter = new LocaleAdapter(getContext());
            localeSpinner.setAdapter(localeAdapter);
            //localeSpinner.setSelection(290);
        }

        if (isPin()) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }else if (isJoinCode()){
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void done(NumberInputActivity.InputMode mode);

    }

}
