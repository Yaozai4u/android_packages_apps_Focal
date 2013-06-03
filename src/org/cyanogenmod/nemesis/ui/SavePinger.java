package org.cyanogenmod.nemesis.ui;

import org.cyanogenmod.nemesis.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class SavePinger extends View {
    public final static String TAG = "SavePinger";
    
    private ValueAnimator mFadeAnimator;
    private ValueAnimator mConstantAnimator;
    private float mFadeProgress = 1.0f;
    private Paint mPaint;
    private long mRingTime[] = new long[CIRCLES_COUNT];
    private long mLastTime;
    private Bitmap mSaveIcon;
    
    private final static int CIRCLES_COUNT = 3;
    private float mRingRadius;
    
    public SavePinger(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }
    
    public SavePinger(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public SavePinger(Context context) {
        super(context);
        initialize();
    }
    
    private void initialize() {
        mPaint = new Paint();
        
        mSaveIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_save)).getBitmap();
        
        mFadeAnimator = new ValueAnimator();
        mFadeAnimator.setDuration(1500);
        mFadeAnimator.setStartDelay(500);
        mFadeAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                mFadeProgress = (Float) arg0.getAnimatedValue();
                invalidate();
            }
        });
        
        mConstantAnimator = new ValueAnimator();
        mConstantAnimator.setDuration(1000);
        mConstantAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mConstantAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                invalidate();
            }
        });
        mConstantAnimator.setFloatValues(0, 1);
        mConstantAnimator.start();
        
        mLastTime = System.currentTimeMillis();
        
        for (int i = 0; i < CIRCLES_COUNT; i++) {
            mRingTime[i] = i * -500; 
        }
    }

    public void startSaving() {
        mFadeAnimator.setFloatValues(0, 1);
    }
    
    public void stopSaving() {
        mFadeAnimator.setFloatValues(1, 0);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        mRingRadius = getWidth() * 0.5f;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        
        long systemTime = System.currentTimeMillis();
        long deltaMs = systemTime - mLastTime;
        
        for (int i = 0; i < CIRCLES_COUNT; i++) {
            mRingTime[i] += deltaMs * 0.2f;
            
            if (mRingTime[i] < 0) continue;
            
            float circleValue = mRingTime[i] / 255.0f;
            float ringProgress = circleValue * mRingRadius;
            
            if (circleValue > 1) circleValue = 1;
            
            mPaint.setARGB((int) (255.0f - 255.0f * circleValue * mFadeProgress), 255, 255, 255);
            canvas.drawCircle(getWidth()/2, getHeight()/2, ringProgress, mPaint);
            
            if (circleValue == 1) {
                mRingTime[i] = 0;
            }
        }
        
        mPaint.setARGB(255,255,255,255);
        canvas.save();
        canvas.scale((float) Math.sin(systemTime * 0.2f), (float) Math.sin(systemTime * 0.2f));
        canvas.drawBitmap(mSaveIcon, getWidth()/2-mSaveIcon.getWidth()/2, getHeight()/2-mSaveIcon.getHeight()/2, mPaint);
        
        mLastTime = systemTime;
        
        invalidate();
    }
}