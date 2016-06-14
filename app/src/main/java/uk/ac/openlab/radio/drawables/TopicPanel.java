package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import uk.ac.openlab.radio.R;

/**
 * Created by kylemontague on 23/03/16.
 */
public class TopicPanel extends LinearLayout {

    LinearLayout block;

    TopicInfoView callerView;
    TopicInfoView likesView;
    TopicInfoView listenersView;

    int background= Color.TRANSPARENT;
    int mLikes =0;
    int mCallers =0;
    int mListeners =0;
    int orientation=0;

    public TopicPanel(Context context) {
        super(context);
        init(Color.TRANSPARENT,0,0,0,0);
    }

    public TopicPanel(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopicPanel, 0, 0);
        try {
            background = a.getColor(R.styleable.TopicPanel_tint, Color.TRANSPARENT);
            mLikes = a.getInt(R.styleable.TopicPanel_likes,0);
            mCallers = a.getInt(R.styleable.TopicPanel_callers,0);
            mListeners = a.getInt(R.styleable.TopicPanel_listeners,0);
            orientation = a.getInt(R.styleable.TopicPanel_orientation,0);
        } finally {
            a.recycle();
        }
        init(background, mLikes, mCallers, mListeners, orientation);
    }


    private void init(int background, int likes, int callers, int listeners, int orientation){
        inflate(getContext(),((orientation == 0)?R.layout.vertical_topic_panel :R.layout.horizontal_topic_panel),this);

        block = (LinearLayout)findViewById(R.id.block);
        callerView = (TopicInfoView)findViewById(R.id.callers);
        likesView = (TopicInfoView)findViewById(R.id.likes);
        listenersView = (TopicInfoView)findViewById(R.id.listeners);

        this.mCallers = callers;
        this.mLikes = likes;
        this.mListeners = listeners;
        update();
    }

    public void update(){

        block.setBackgroundColor(background);
        callerView.setCount(mCallers);
        likesView.setCount(mLikes);
        listenersView.setCount(mListeners);

        setInfoVisability(callerView,mCallers);
        setInfoVisability(likesView,mLikes);
        setInfoVisability(listenersView,mListeners);
    }


    private void setInfoVisability(TopicInfoView view, int value){
        if(value == -1)
            view.setVisibility(GONE);
        else
            view.setVisibility(VISIBLE);
    }


    public TopicInfoView callers() {
        return callerView;
    }

    public TopicInfoView likes() {
        return likesView;
    }

    public TopicInfoView listeners() {
        return listenersView;
    }
}
