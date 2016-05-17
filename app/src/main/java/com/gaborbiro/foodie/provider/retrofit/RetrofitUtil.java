package com.gaborbiro.foodie.provider.retrofit;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.gaborbiro.foodie.ui.view.ProgressEvent;
import com.gaborbiro.foodie.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RetrofitUtil {

    private static final String TAG = "RetrofitUtil";

    public static <T> CallbackWithRetry<T> executeWithRetry(Call<T> call,
            Callback<T> callback) {
        logCall(call);
        CallbackWithRetry<T> retry = new CallbackWithRetry<>(call, callback);
        call.enqueue(retry);
        return retry;
    }

    private static void logCall(Call call) {
        Logger.d(TAG, call.request()
                .url()
                .toString());
    }

    public static class CallbackWithRetry<T> implements retrofit2.Callback<T> {

        private static final int TOTAL_RETRIES = 3;
        private static final String TAG = CallbackWithRetry.class.getSimpleName();

        private Call<T> mCall;
        private final Callback<T> mCallback;
        private int mRetryCount = 0;
        private boolean mCanceled;

        public CallbackWithRetry(Call<T> call, Callback<T> callback) {
            this.mCall = call;
            this.mCallback = callback;
            EventBus.getDefault()
                    .register(this);
            ProgressEvent.sendProgressStartEvent(CallbackWithRetry.this);
        }

        @Override public void onResponse(Call<T> call, Response<T> response) {
            if (mCanceled) {
                return;
            }
            Logger.d(TAG, "Response: " + response.code());
            ProgressEvent.sendProgressEndEvent(CallbackWithRetry.this);

            if (response.body() != null) {
                try {
                    mCallback.onResponse(call.request()
                            .url()
                            .hashCode(), response.body());
                } catch (ClassCastException e) {
                    mCallback.onResponse(call.request()
                                    .url()
                                    .hashCode(),
                            ((PayloadWrapper<T>) response.body()).getPayload());
                }
            } else {
                String errorMessage = null;
                try {
                    errorMessage = response.errorBody()
                            .string();
                } catch (IOException e) {
                }
                if (TextUtils.isEmpty(errorMessage)) {
                    errorMessage = response.message();
                }
                mCallback.onFailure(call.request()
                        .url()
                        .hashCode(), errorMessage);
            }
        }

        @Override public void onFailure(Call call, Throwable t) {
            if (mCanceled) {
                return;
            }
            Logger.e(TAG, t.getLocalizedMessage());
            if (mRetryCount++ < TOTAL_RETRIES) {
                Logger.v(TAG,
                        "Retrying... (" + mRetryCount + " out of " + TOTAL_RETRIES + ")");
                retry();
            } else {
                ProgressEvent.sendProgressEndEvent(CallbackWithRetry.this);
                mCallback.onFailure(call.request()
                        .url()
                        .hashCode(), t);
            }
        }

        private void retry() {
            mCall = mCall.clone();
            mCall.enqueue(this);
        }

        @Subscribe public void onEvent(CancelAllRetrofitRequestsEvent event) {
            if (mCall != null) {
                mCall.cancel();
            }
            mCanceled = true;
            ProgressEvent.sendProgressEndEvent(CallbackWithRetry.this);
        }
    }

    /**
     * Creates a unique subdirectory of the designated app cache directory. Tries to use
     * external but if not mounted, falls back on internal storage.
     */
    public static File getDiskCacheDir(Context appContext, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable()
                ? appContext.getExternalCacheDir()
                        .getPath() : appContext.getCacheDir()
                        .getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
