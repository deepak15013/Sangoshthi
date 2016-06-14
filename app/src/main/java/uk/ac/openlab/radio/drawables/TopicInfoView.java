package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.ac.openlab.radio.R;

/**
 * Created by kylemontague on 22/03/16.
 */
public class TopicInfoView extends LinearLayout{

    TextView text;
    ImageView image;

    private int mCount=-1;
    private Drawable mIcon = null;

    public TopicInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable icon;
        int count;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopicInfoView, 0, 0);
        try {
            icon = a.getDrawable(R.styleable.TopicInfoView_iconRes);
            count = a.getInt(R.styleable.TopicInfoView_number,0);
            init(icon, count);
        } finally {
            a.recycle();
        }

    }



    public TopicInfoView(Context context) {
        super(context);
        init(null, 0);
    }

    private void init(Drawable icon, int counter) {
        inflate(getContext(), R.layout.topic_info_view, this);
        text = (TextView)findViewById(R.id.text);
        image = (ImageView)findViewById(R.id.icon);

        mCount = counter;
        mIcon = icon;
        text.setText(String.valueOf(mCount));
        image.setImageDrawable(mIcon);
        update();

    }


    private void update(){
        if(mIcon == null || mCount ==-1){
            text.setVisibility(GONE);
            image.setVisibility(GONE);
        }else{
            text.setVisibility(VISIBLE);
            image.setVisibility(VISIBLE);

        }
    }


    public void setCount(int count){
        text.setText(""+count);
        update();
    }
}
