package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;

/**
 * Created by kylemontague on 29/03/16.
 */
public class ChecklistItemView extends LinearLayout {

    protected View linearLayout;
    protected ImageView icon;
    protected TextView title;

    public ChecklistItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChecklistItemView, 0, 0);
        try {
            int iconRes = a.getResourceId(R.styleable.ChecklistItemView_iconRes,-1);
            String text = a.getString(R.styleable.ChecklistItemView_text);
            boolean checked = a.getBoolean(R.styleable.ChecklistItemView_isComplete,false);
            @ColorRes int color = a.getColor(R.styleable.ChecklistItemView_forgroundColor, Color.WHITE);
            init(color, text,iconRes,checked);
        } finally {
            a.recycle();
        }
    }

    int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };


    private void init(@ColorRes int color, String text, int iconRes, boolean checked) {
        linearLayout = inflate(getContext(), R.layout.checklist_item_view, this);

        ColorStateList colors = new ColorStateList(states,new int[]{color,color,color,color});

        icon = (ImageView)findViewById(R.id.icon);
        icon.setColorFilter(color);

        title = (TextView)findViewById(R.id.title);
        title.setTextColor(colors);

        setTitle(text);
        setIcon(iconRes);
    }



    public void setTitle(String text){
        if(text!=null) {
            title.setText(GlobalUtils.capitalizeWords(text));
            title.setVisibility(VISIBLE);
        }else
            title.setVisibility(INVISIBLE);
    }


    public void setTitle(@StringRes int text){
        if(text!=-1) {
            title.setText(GlobalUtils.capitalizeWords(getContext(),text));
            title.setVisibility(VISIBLE);
        }else
            title.setVisibility(INVISIBLE);
    }

    public void setIcon(@DrawableRes int iconRes){
        if(iconRes != -1){
            icon.setImageResource(iconRes);
            icon.setVisibility(VISIBLE);
        }else{
            icon.setVisibility(GONE);
        }
    }

    public void setIcon(Drawable iconDrawable){
        if(iconDrawable != null){
            icon.setImageDrawable(iconDrawable);
            icon.setVisibility(VISIBLE);
        }else{
            icon.setVisibility(GONE);
        }
    }


    public void setEnabled(boolean isEnabled){
        linearLayout.setFocusableInTouchMode(false);
    }

}
