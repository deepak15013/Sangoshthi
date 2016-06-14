package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.Topic;

/**
 * Created by kylemontague on 22/03/16.
 */
public class TopicView extends RelativeLayout {

    int tint = Color.TRANSPARENT;
    String iconURI = null;
    String name = null;
    Drawable icon;

    TextView mTitle;
    ImageView mIcon;
    RelativeLayout mBackground;



    public TopicView(Context context) {
        super(context);
        init();
    }

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopicView, 0, 0);
        try {
            tint = a.getColor(R.styleable.TopicView_tint,Color.TRANSPARENT);
            iconURI = a.getString(R.styleable.TopicView_iconURI);
            icon = a.getDrawable(R.styleable.TopicView_iconRes);
            name = a.getString(R.styleable.TopicView_name);
        } finally {
            a.recycle();
        }
        init();
    }


    public void setTopic(Topic object){
        this.iconURI = object.getIcon().getUri();
        this.name = object.getName();
        update();
    }

    private void init(){
        inflate(getContext(),R.layout.topic_view,this);
        this.mTitle = (TextView)findViewById(R.id.title);
        this.mIcon = (ImageView)findViewById(R.id.icon);
        this.mBackground = (RelativeLayout) findViewById(R.id.background);
        update();
    }

    private void update(){
        if(name == null){
            this.mTitle.setVisibility(GONE);
        }else{
            this.mTitle.setText(name);
        }

        if(iconURI == null && icon == null){
            mIcon.setVisibility(INVISIBLE);
        }else if(icon == null){
            mIcon.setImageURI(Uri.parse(iconURI));
        }else{
            mIcon.setImageDrawable(icon);
        }

        this.mBackground.setBackgroundColor(tint);

    }
}
