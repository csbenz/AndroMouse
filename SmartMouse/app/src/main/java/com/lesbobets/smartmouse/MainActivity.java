package com.lesbobets.smartmouse;

import android.os.Bundle;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private VelocityTracker mVelocityTracker = null;

    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        v = findViewById(R.id.touch_view);

        v.setOnTouchListener(mOnTouchListener);

    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
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
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    break;
            }
            return true;
        }
    };

}
