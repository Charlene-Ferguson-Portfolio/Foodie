package com.gaborbiro.foodie.ui.view;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;

/**
 * Used with {@link NonProgrammaticTouchDetectorMapFragment}
 */
public class TouchableWrapper extends FrameLayout {

    public interface MapTouchListener {
        void onMapPinched();
        void onMapZoomed(float zoom);
        void onMapReleased();
    }

    private ScaleGestureDetector gestureDetector;
    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private long lastTouched = 0;
    private static final long SCROLL_TIME = 200L;
    private MapTouchListener mMapTouchListener;

    public TouchableWrapper(Context context, MapTouchListener listener) {
        super(context);
        mMapTouchListener = listener;

        gestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= 50) {
                    lastZoomTime = detector.getEventTime();
                    mMapTouchListener.onMapZoomed(getZoomValue(detector.getCurrentSpan(), lastSpan));
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                lastSpan = -1;
            }
        });
    }

    private static float getZoomValue(float currentSpan, float lastSpan) {
        double value = (Math.log(currentSpan / lastSpan) / Math.log(1.55d));
        return (float) value;
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                if (ev.getPointerCount() > 1) {
                    mMapTouchListener.onMapPinched();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                lastTouched = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                fingers = 0;
                final long now = SystemClock.uptimeMillis();
                if (now - lastTouched > SCROLL_TIME) {
                    mMapTouchListener.onMapReleased();
                }
                break;
        }
        if (fingers > 1) {
            return gestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}