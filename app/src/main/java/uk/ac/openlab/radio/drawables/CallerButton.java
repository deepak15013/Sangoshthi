package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import uk.ac.openlab.radio.R;


/**
 * Created by Kyle Montague on 23/03/16.
 */
public class CallerButton extends ToggleButton {

    enum TYPE{
        LISTENER,
        GUEST,
        HOST,
        PRODUCER,
        TAGGER
    }

    TYPE mType = TYPE.LISTENER;
    int mTint;

    public CallerButton(Context context) {
        super(context);
        init();
    }

    public CallerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CallerButton, 0, 0);
        try {
            mType = TYPE.values()[a.getInt(R.styleable.CallerButton_callerType, TYPE.LISTENER.ordinal())];
            mTint = a.getColor(R.styleable.CallerButton_tint, Color.WHITE);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init(){

        this.setTextOff("");
        this.setTextOn("");
        this.setTextSize(0);
        switch (mType){
            case GUEST:
                this.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_phone_active,0,0);
                break;
            case HOST:
                this.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_people,0,0);
                break;
            default:
                this.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_person,0,0);
                break;
        }

        this.setBackgroundColor(mTint);
    }
}
