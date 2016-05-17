package com.gaborbiro.foodie.util.gate;

import com.gaborbiro.foodie.BuildConfig;

/**
 * A gate with n "legs". If any of the "legs" are activated ({@link #set(Object)}), the
 * gate is opened, otherwise it's closed. Use {@link #reset(Object)} to deactivate a "leg"
 */
public class Gate {
    interface Listener {

        /**
         * Called from a background thread
         */
        void onGateOpen();

        /**
         * Called from a background thread
         */
        void onGateClosed();
    }

    private final WeakList<Object> mGate;
    private final WeakList<Listener> mListeners;

    public Gate() {
        mGate = new WeakList<>();
        mListeners = new WeakList<>();
    }

    public void registerListener(Listener listener) {
        synchronized (mListeners) {
            mListeners.addWeak(listener);
        }
    }

    public boolean unregisterListener(Listener listener) {
        synchronized (mListeners) {
            return mListeners.removeWeak(listener);
        }
    }

    /**
     * @return true if the gate has been opened as a result of calling this method
     */
    public boolean set(Object o) {
        synchronized (mGate) {
            boolean stateBeforeOperation = isGateClosed();
            mGate.addWeak(o);
            return notifyListeners(stateBeforeOperation);
        }
    }

    /**
     * @return true if the gate has been closed as a result of calling this method
     */
    public boolean reset(Object o) {
        synchronized (mGate) {
            boolean stateBeforeOperation = isGateClosed();
            mGate.removeWeak(o);
            return notifyListeners(stateBeforeOperation);
        }
    }

    /**
     * @return true if the state of the gate changed since the last notify
     */
    private boolean notifyListeners(boolean stateBeforeOperation) {
        final boolean stateAfterOperation = isGateClosed();

        if (stateAfterOperation != stateBeforeOperation) {
            WeakList.IteratorUpdater<Listener> op =
                    new WeakList.IteratorUpdater<Listener>(false) {
                        @Override public void run(boolean isCallerThread) {
                            if (BuildConfig.DEBUG && isCallerThread) {
                                throw new AssertionError(
                                        "Gate updates should not be done on the caller " +
                                                "thread");
                            }
                            if (stateAfterOperation) {
                                getCurrentItem().onGateClosed();
                            } else {
                                getCurrentItem().onGateOpen();
                            }
                        }
                    };
            mListeners.applyForAll(op);
            return true;
        }
        return false;
    }

    private boolean isGateClosed() {
        return mGate.isEmpty();
    }
}
