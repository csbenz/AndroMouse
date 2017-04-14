package com.lesbobets.smartmouse;

import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by joachim on 4/14/17.
 */

public abstract class TwoFingersDetector {
        private static final int TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;
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
