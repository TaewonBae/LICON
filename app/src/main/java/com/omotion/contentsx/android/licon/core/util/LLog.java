package com.omotion.contentsx.android.licon.core.util;


import com.omotion.contentsx.android.licon.BuildConfig;

/**
 *  @author letzdev81
 *  Clean Log
 */
public class LLog {

    public static boolean hasLog = BuildConfig.DEBUG;

    public static void d(String tag, String func, String msg) {
        if (hasLog) {
            android.util.Log.d(tag, func + "::" + msg);
        }
    }

    public static void e(String tag, String func, String msg) {
        if (hasLog) {
            android.util.Log.e(tag, func + "::" + msg);
        }
    }

    public static void i(String tag, String func, String msg) {
        if (hasLog) {
            android.util.Log.i(tag, func + "::" + msg);
        }
    }

    public static void w(String tag, String func, String msg) {
        if (hasLog) {
            android.util.Log.w(tag, func + "::" + msg);
        }
    }

    public static void v(String tag, String func, String msg) {
        if (hasLog) {
            android.util.Log.v(tag, func + "::" + msg);
        }
    }

    public static void l(String tag, String func, String message ) {
        if (hasLog) {
            int maxLogSize = 1000;
            for(int i = 0; i <= message.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = end > message.length() ? message.length() : end;
                android.util.Log.d(tag, func + "::" + message.substring(start, end));
            }
        }
    }
}
