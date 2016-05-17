package com.gaborbiro.foodie.ui.view;

import org.greenrobot.eventbus.EventBus;

/**
 * Send this event to control any visible
 * {@link AutoProgressIndicator}s
 */
public class ProgressEvent {
    public enum ProgressType {
        START, PROGRESS, END
    }

    private ProgressType mType;
    private int mProgress;
    private Object mSource;

    public ProgressEvent(Object source, ProgressType type, int progress) {
        mSource = source;
        mType = type;
        mProgress = progress;
    }

    public Object getSource() {
        return mSource;
    }

    public ProgressType getType() {
        return mType;
    }

    public int getProgress() {
        return mProgress;
    }

    public static void sendProgressStartEvent(Object source) {
        EventBus.getDefault()
                .post(new ProgressEvent(source, ProgressType.START, 0));
    }

    public static void sendProgressEvent(Object source, int progress) {
        EventBus.getDefault()
                .post(new ProgressEvent(source, ProgressType.PROGRESS, progress));
    }

    public static void sendProgressEndEvent(Object source) {
        EventBus.getDefault()
                .post(new ProgressEvent(source, ProgressType.END, 0));
    }
}
