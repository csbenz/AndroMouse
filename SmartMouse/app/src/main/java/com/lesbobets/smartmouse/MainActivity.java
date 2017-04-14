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

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String TAG = "MainActivity";

    private VelocityTracker mVelocityTracker = null;

    private View v;

    private ServerContacter mServerContacter;
    private AsyncTask<Double[], Void, Void> mAsyncTask;
    private GestureDetectorCompat mDetector;


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

//        v = findViewById(R.id.touch_view);
//        mDetector.setOnTouchListener(mOnTouchListener);
//        mAsyncTask = new SendAsyncTask();
        initClient();
    }

    private void initClient() {
        mServerContacter = new ServerContacter();
//        Toast.makeText(this, "Initialized Client successfully", Toast.LENGTH_SHORT).show();
    }

//private class SendAsyncTask extends AsyncTask<Double[], Void, Void> {
//
//    private ServerContacter serverContacter;
//
//    public SendAsyncTask() {
//        try {
//            serverContacter = new ServerContacter();
//            Log.d(TAG, "Initialized Client successfully");
//            Toast.makeText(MainActivity.this, "Initialized Client successfully", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "initClient: Failed to initialize");
//        }
//    }
//
//    @Override
//    protected Void doInBackground(Double[]... doubles) {
//        Double[] dd = doubles[0];
//        serverContacter.sendVelocities(dd);
//        return null;
//    }
//
//}

//    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            int index = event.getActionIndex();
//            int action = event.getActionMasked();
//            int pointerId = event.getPointerId(index);
//
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    if (mVelocityTracker == null) {
//                        mVelocityTracker = VelocityTracker.obtain();
//                    } else {
//                        mVelocityTracker.clear();
//                    }
//                    mVelocityTracker.addMovement(event);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mVelocityTracker.addMovement(event);
//                    mVelocityTracker.computeCurrentVelocity(1000);
//                    float xVelocity = VelocityTrackerCompat.getXVelocity(mVelocityTracker,
//                            pointerId);
//                    float yVelocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker,
//                            pointerId);
//
//                    Log.d(TAG, " Velocities: " + xVelocity + "\t\t" + yVelocity);
//
//                    final Double[] velocities = new Double[]{Double.valueOf(xVelocity), Double.valueOf(yVelocity)};
////                    mAsyncTask.execute(velocities);
//                    if (mServerContacter != null) {
//                        mServerContacter.sendVelocities(velocities);
//                    }
//
//                    break;
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    mVelocityTracker.recycle();
//                    mVelocityTracker = null;
//                    break;
//            }
//            return true;
//        }
//    };

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.multiTouchListener.onTouchEvent(event);
        this.mDetector.onTouchEvent(event);

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
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                        pointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                        pointerId);

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
        return true;
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
}
