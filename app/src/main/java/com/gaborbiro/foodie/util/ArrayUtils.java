package com.gaborbiro.foodie.util;

import android.text.TextUtils;

import java.util.List;

public class ArrayUtils {

    public static String join(List array, String separator) {
        if (array == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            builder.append(array.get(i));

            if (!TextUtils.isEmpty(separator) && i < array.size() - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
