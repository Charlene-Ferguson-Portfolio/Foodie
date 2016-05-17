package com.gaborbiro.foodie.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.gaborbiro.foodie.util.gate.Gate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Place a AutoProgressIndicator in your layout and it will automatically show progress
 * from any component that sends out {@link ProgressEvent}'s
 */
public class AutoProgressIndicator extends ProgressBar {

    private Gate mGate;

    public AutoProgressIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGate = new Gate();
        EventBus.getDefault()
                .register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onProgressEvent(final ProgressEvent event) {
        switch (event.getType()) {
            case START:
                post(new Runnable() {
                    @Override public void run() {
                        if (mGate.set(event.getSource())) {
                            setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
            case PROGRESS:
                post(new Runnable() {
                    @Override public void run() {
                        if (mGate.set(event.getSource())) {
                            setVisibility(View.VISIBLE);
                        }
                        setProgress(event.getProgress());
                    }
                });
                break;
            case END:
                post(new Runnable() {
                    @Override public void run() {
                        if (mGate.reset(event.getSource())) {
                            setVisibility(View.GONE);
                        }
                    }
                });
                break;
        }
    }
}
