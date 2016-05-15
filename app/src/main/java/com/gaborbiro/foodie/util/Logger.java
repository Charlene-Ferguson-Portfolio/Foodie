package com.gaborbiro.foodie.util;

import android.util.Log;

public class Logger {

    private static final String TAG = "Foodie";

    public static void d(String tag, String message) {
        Log.d(TAG, "(" + tag + ") " + message);
    }

    public static void e(String tag, String message) {
        Log.e(TAG, "(" + tag + ") " + message);
    }

    public static void v(String tag, String message) {
        Log.v(TAG, "(" + tag + ") " + message);
    }
}
