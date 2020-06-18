package com.mymeatshop.utils;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Vivek on 17/9/18.
 */

public class LoggerUtil {
    private static final String TAG = "OUTPUT";

    public static void logItem(Object src) {
        Gson gson = new Gson();
        Log.e(TAG, "====:> " + gson.toJson(src));
    }
}
