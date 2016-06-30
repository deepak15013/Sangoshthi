/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.util.Locale;

/**
 * Created by Kyle Montague on 01/03/16.
 */
public class GlobalUtils {

    public static final String PREF_SESSION_ID = "PREF_SESSION_ID";
    public static final String PREF_STUDIO_ID = "PREF_STUDIO_ID";
    public static final String PREF_SESSION_COOKIE = "PREF_SESSION_COOKIE";
    public static final String PREF_TELEPHONE_NUMBER = "PREF_TELEPHONE_NUMBER";
    public static final String PREF_CITIZEN_RADIO_NUMBER = "PRE_CITIZEN_RADIO_NUMBER";
    public static final String PREF_CITIZEN_RADIO_NAME = "PREF_CITIZEN_RADIO_NAME";
    public static final String PREF_LANG = "PRE_LANG";

    public static final String PREF_AREA_CODE = "PREF_AREA_CODE";

    public static String PAYLOAD_KEY = "RHD_PAYLOAD";

    private SharedPreferences sharedPreferences;
    private String sessionID = null;
    private String studioID = null;
    private String cookie = null;
    private String citizenRadioNumber = null;
    private String citizenRadioName=null;
    private String lang = null;

    private String areacode = null;

    private static GlobalUtils sharedInstance;

    public static GlobalUtils shared(){
        if(sharedInstance == null)
            sharedInstance = new GlobalUtils();
        return sharedInstance;
    }

    public void init(Context context){
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences prefs(){
        return sharedPreferences;
    }

    public String phoneNumber(){
        return sharedPreferences.getString(PREF_TELEPHONE_NUMBER,null);
    }

    public boolean setPhoneNumber(String number){
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_TELEPHONE_NUMBER,number).commit();
    }

    public boolean setSession(String session){
        this.sessionID = session;
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_SESSION_ID,this.sessionID).commit();
    }

    public boolean setStudioID(String studioID){
        this.studioID = studioID;
        if(sharedPreferences == null) {
            return false;
        }

        return sharedPreferences.edit().putString(PREF_STUDIO_ID,this.studioID).commit();
    }

    public String sessionID(){
        if(this.sessionID == null)
            this.sessionID = sharedPreferences.getString(PREF_SESSION_ID,null);
        return this.sessionID;
    }


    public String getCookie() {
        if(this.cookie == null)
            this.cookie = sharedPreferences.getString(PREF_SESSION_COOKIE,null);
        return this.cookie;
    }

    public boolean setCookie(String cookie) {
        this.cookie = cookie;
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_SESSION_COOKIE,this.cookie).commit();
    }

    public boolean setCitizenRadioNumber(String number, String name){
        this.citizenRadioNumber = number;
        this.citizenRadioName = name;
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_CITIZEN_RADIO_NUMBER,this.citizenRadioNumber).putString(PREF_CITIZEN_RADIO_NAME,this.citizenRadioName).commit();
    }

    public String citizenRadioNumber(){
        if(this.citizenRadioNumber == null)
            this.citizenRadioNumber = sharedPreferences.getString(PREF_CITIZEN_RADIO_NUMBER,null);
        return this.citizenRadioNumber;
    }

    public String citizenRadioName(){
        if(this.citizenRadioName == null)
            this.citizenRadioName = sharedPreferences.getString(PREF_CITIZEN_RADIO_NAME,null);
        return this.citizenRadioName;
    }

    public boolean setAreacode(String number){
        this.areacode = number;
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_AREA_CODE,this.areacode).commit();
    }

    public String areacode(){
        if(this.areacode == null)
            this.areacode = sharedPreferences.getString(PREF_AREA_CODE,null);
        return this.areacode;
    }


    public boolean setLang(String lang){
        this.lang = lang;
        if(sharedPreferences == null)
            return false;
        return sharedPreferences.edit().putString(PREF_LANG,this.lang).commit();
    }

    public String lang(){
        if(this.lang == null)
            this.lang = sharedPreferences.getString(PREF_LANG,null);
        return this.lang;
    }


    public static Drawable iconWithTint(Context context, @DrawableRes int iconRes, @ColorRes int tintColor){
        return GlobalUtils.iconWithTint(context,ContextCompat.getDrawable(context, iconRes).mutate(),tintColor);
    }

    public static Drawable iconWithTint(Context context, Drawable icon, @ColorRes int tintColor){
        TypedValue typedValue = new TypedValue();
        icon.setColorFilter(context.getResources().getColor(tintColor), PorterDuff.Mode.SRC_ATOP);
        return icon;
    }

    public static String capitalizeWords(Context context, @StringRes int text){
        return  capitalizeWords(context.getString(text));
    }

    public static String capitalizeWords(String text){
        String[] words = text.split(" ");
        String result = "";
        for(int x=0;x<words.length;x++)
            result += words[x].substring(0,1).toUpperCase()+words[x].substring(1)+" ";
        return result;
    }

    public static @StyleRes int appTheme(){
        switch (BuildConfig.FLAVOR){
            case "bbc":
                return R.style.BBCTheme;
            case "lebanon":
                return R.style.Lebanon;
            case "india":
                return R.style.AppTheme;
        }
        return R.style.AppTheme;
    }
}


