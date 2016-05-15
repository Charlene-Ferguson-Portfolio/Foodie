package com.gaborbiro.foodie.util;

import com.gaborbiro.foodie.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ArrayList implementation of weak-references. All operations do a cleanup of empty
 * references.
 */
public class WeakList<T> {

    private final List<WeakReference<T>> mList = new ArrayList<>();

    /**
     * Convenience method to just remove dead weak-references
     */
    public <T> void cleanup() {
        applyForAll(null);
    }

    /**
     * @return true if <code>o</code> has been found. False otherwise.
     */
    public boolean addWeak(final T o) {
        IteratorUpdater<T> opIfNotFound = new IteratorUpdater<T>(true) {
            @Override public void run(boolean isCallerThread) {
                mList.add(new WeakReference<>(o));
            }
        };
        return searchAndApply(o, null, opIfNotFound);
    }

    /**
     * @return true if <code>o</code> has been found. False otherwise.
     */
    public boolean removeWeak(T o) {
        IteratorUpdater<T> opIfFound = new IteratorUpdater<T>(true) {
            @Override public void run(boolean isCallerThread) {
                getIterator().remove();
            }
        };
        return searchAndApply(o, opIfFound, null);
    }

    /**
     * This method will also clean up any dead weak-references it finds. This might
     * affect the length of the list.
     *
     * @param opIfFound    operation to be applied if the <code>item</code> is found in
     *                     the list
     * @param opIfNotFound operation to be applie din case <code>item</code> was not
     *                     found in the list
     * @return true if <code>item</code> has been found, false otherwise
     */
    public boolean searchAndApply(T item, IteratorUpdater<T> opIfFound,
            IteratorUpdater<T> opIfNotFound) {
        synchronized (mList) {
            boolean found = false;

            for (Iterator<WeakReference<T>> i = mList.iterator(); i.hasNext(); ) {
                T currentItem = i.next()
                        .get();

                if (currentItem == null) {
                    i.remove();
                } else {
                    if (item != null && currentItem == item) {
                        if (opIfFound != null) {
                            opIfFound.apply(i, currentItem);
                        }
                        found = true;
                        break;
                    }
                }
            }
            if (!found && opIfNotFound != null) {
                opIfNotFound.apply(null, null);
            }
            return found;
        }
    }

    /**
     * This method will also clean up any dead weak-references it finds. This might
     * affect the length of the list.
     *
     * @param op operation to be applied to each element in the list
     */
    public void applyForAll(IteratorUpdater<T> op) {
        synchronized (mList) {
            for (Iterator<WeakReference<T>> i = mList.iterator(); i.hasNext(); ) {
                T currentItem = i.next()
                        .get();

                if (currentItem == null) {
                    i.remove();
                } else if (op != null) {
                    op.apply(i, currentItem);
                }
            }
        }
    }

    public int size() {
        synchronized (mList) {
            if (mList.size() < 100) {
                cleanup();
            }
            return mList.size();
        }
    }

    public boolean isEmpty() {
        synchronized (mList) {
            if (mList.size() < 100) {
                cleanup();
            }
            return mList.isEmpty();
        }
    }

    public static abstract class IteratorUpdater<T> {
        private Iterator<WeakReference<T>> mIterator;
        private T mCurrentItem;
        private boolean mRunOnCallerThread;

        public IteratorUpdater(boolean runOnCallerThread) {
            this.mRunOnCallerThread = runOnCallerThread;
        }

        public void apply(Iterator<WeakReference<T>> i, T currentItem) {
            this.mIterator = i;
            this.mCurrentItem = currentItem;
            Runnable r = new Runnable() {
                @Override public void run() {
                    if (BuildConfig.DEBUG) {
                        IteratorUpdater.this.run(mRunOnCallerThread);
                    } else {
                        try {
                            IteratorUpdater.this.run(mRunOnCallerThread);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            };
            if (mRunOnCallerThread) {
                r.run();
            } else {
                new Thread(r, "WeakList-Executor").run();
            }
        }

        public Iterator<WeakReference<T>> getIterator() {
            return mIterator;
        }

        public T getCurrentItem() {
            return mCurrentItem;
        }

        public abstract void run(boolean isCallerThread);
    }
}
