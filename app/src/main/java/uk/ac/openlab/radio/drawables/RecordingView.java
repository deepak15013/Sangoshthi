/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.drawables;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;

/**
 * Created by Kyle Montague on 29/02/16.
 */
public class RecordingView extends View {


    private Paint fillPaint;
    private Paint textPaint;
    private int progressColor;
    private int backgroundColor;
    private int textColor = Color.WHITE;

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    private int maxDuration = 30;

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    boolean showText = false;

    private String title = "Time";

    public void setTicker(float ticker) {
        Log.d("Ticker","value:"+ticker);
        if(ticker <= 1) {
            this.ticker=ticker;
            invalidate();
        }else{
            animationHandler.removeCallbacks(animationRunnable);
            shouldAnimate = false;
        }
    }

    private float ticker;


    public RecordingView(Context context) {
        super(context);
        setupPaints();

    }

    public RecordingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecordingView, 0, 0);
        try {
            progressColor = a.getColor(R.styleable.RecordingView_progressColor, Color.GREEN);
            backgroundColor = a.getColor(R.styleable.RecordingView_backgroundColor, Color.RED);
            textColor = a.getColor(R.styleable.RecordingView_textColor, Color.WHITE);
            ticker = a.getFloat(R.styleable.RecordingView_ticker, 0.0f);
            title = a.getString(R.styleable.RecordingView_time);
            maxDuration = a.getInt(R.styleable.RecordingView_max_duration,30);
        } finally {
            a.recycle();
        }

        setupPaints();
    }

    private void setupPaints() {
        fillPaint = new Paint();
        fillPaint.setColor(Color.BLACK);
        fillPaint.setStrokeWidth(1.0f);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(1.0f);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(60);
        textPaint.setAntiAlias(true);
    }

    int currentTime = 0;
    boolean shouldAnimate = false;
    int incrementInterval = 50;
    Handler animationHandler;
    Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if(shouldAnimate) {
                tick();
            }
        }
    };

    public void start(){
        currentTime = 0;
        animationHandler = new Handler();
        animationHandler.post(animationRunnable);
        shouldAnimate = true;
    }

    public void stop(){
        shouldAnimate =false;
        animationHandler.removeCallbacks(animationRunnable);
    }

    private void tick(){
        setTicker((currentTime+1.0f)/((maxDuration*1000)+1.0f));
        currentTime +=incrementInterval;
        animationHandler.postDelayed(animationRunnable,incrementInterval);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Rect r = new Rect(getLeft(),getTop(),getRight(),getBottom());
//        canvas.drawRect(canvas.getClipBounds(), fillPaint);


        Bitmap background = makeToneGrad(canvas.getClipBounds(), progressColor, backgroundColor, ticker);
        canvas.drawBitmap(background, canvas.getClipBounds(), canvas.getClipBounds(), fillPaint);

        if (showText)
            canvas.drawText(title, getWidth() / 2, getHeight() / 2, textPaint);
    }


    private static RectF rectF =null;
    private static Bitmap makeToneGrad(Rect rect, @ColorInt int startColour, @ColorInt int endColour, float point) {
        int[] colours = new int[]{startColour, startColour, endColour, endColour};
        float[] positions = new float[]{0f, point, point, 1f};
        int start = rect.left;
        int end = rect.right;

        if (GlobalUtils.isRTL()) { // todo add option in the menu system to switch this layout.
            start = rect.right;
            end = rect.left;
        }

        LinearGradient gradient = new LinearGradient(start, rect.bottom, end, rect.bottom, colours, positions, Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);
        p.setStyle(Paint.Style.FILL);
        if(rectF == null)
            rectF = new RectF(rect.left,rect.top,rect.right,rect.bottom);
        Bitmap bitmap = Bitmap.createBitmap((int)rectF.width(), (int)rectF.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawRect(rect, p);
        return bitmap;
    }



}
