package com.lesbobets.smartmouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String TAG = "MainActivity";

    private VelocityTracker mVelocityTracker = null;

    private View v;

    private ServerContacter mServerContacter;
    private AsyncTask<Double[], Void, Void> mAsyncTask;
    private GestureDetectorCompat mDetector;

    private long lastPushDownTime = 0;


    TwoFingersDetector multiTouchListener = new TwoFingersDetector() {
        @Override
        public void onTwoFingerTap() {
            if (mServerContacter != null) {
                mServerContacter.sendRightClick();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        initClient();
    }

    private void initClient() {
        mServerContacter = new ServerContacter();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.multiTouchListener.onTouchEvent(event);
        this.mDetector.onTouchEvent(event);

        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                lastPushDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - lastPushDownTime < 10) {
                    return super.onTouchEvent(event);
                }
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                Log.d(TAG, " Velocities: " + xVelocity + "\t\t" + yVelocity);

                final Double[] velocities = new Double[]{Double.valueOf(xVelocity), Double.valueOf(yVelocity)};
//                    mAsyncTask.execute(velocities);
                if (mServerContacter != null) {
                    mServerContacter.sendVelocities(velocities);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }

        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mServerContacter != null) {
            mServerContacter.sendLeftClick();
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    public abstract class TwoFingersDetector {
        private final int TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;
        private long mFirstDownTime = 0;

        private void reset(long time) {
            mFirstDownTime = time;
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch(event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if(event.getEventTime() - mFirstDownTime > TIMEOUT)
                        reset(event.getDownTime());
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(event.getPointerCount() == 2)
                        onTwoFingerTap();
                    break;
            }

            return false;
        }

        public abstract void onTwoFingerTap();
    }

}