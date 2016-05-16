package com.gaborbiro.foodie.ui;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    public interface MapTouchListener {
        void onMapTouched();
    }

    private long lastTouched = 0;
    private static final long SCROLL_TIME = 200L;
    private MapTouchListener mMapTouchListener;

    public TouchableWrapper(Context context, MapTouchListener listener) {
        super(context);
        mMapTouchListener = listener;
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouched = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                final long now = SystemClock.uptimeMillis();
                if (now - lastTouched > SCROLL_TIME) {
                    mMapTouchListener.onMapTouched();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}