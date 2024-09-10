package com.omotion.contentsx.android.licon.core.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.omotion.contentsx.android.licon.RBWApplication;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;

/**
 * SharedPreferences 클래스
 */
public class SharedPrefManager {
    private static String TAG = "GlobalPreference";

    public static SharedPreferences prefs = null;

    public static final String KEY_HAS_SEEN_TUTORIAL = "key_has_seen_tutorial";
    /**
     * @see MusicPlayer.PlayStatus
     */
    public static final String IS_SHOW_PRONUNCIATION = "is_show_pronunciation";
    public static final String LYRIC_LANGUAGE_INDEX = "lyric_language_index";
    public static final String REPEAT_MODE = "repeat_mode";
    public static final String IS_SHUFFLE = "is_shuffle";
    public static final String SERIAL_KEY = "serial_key";

    public static SharedPreferences getInstance() {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(RBWApplication.getContext());
        }
        return prefs;
    }

    public static String getData(String key, String defaultData) {
        String data = getInstance().getString(key, defaultData);
        LLog.d(TAG, "getData", "key : " + key + ", value : " + data);
        return data;
    }

    public static void setData(String key, String value) {
        LLog.d(TAG, "setData", "key : " + key + ", value : " + value);
        getInstance().edit().putString(key, value).commit();
    }

    public static boolean getData(String key, boolean defaultData) {
        return getInstance().getBoolean(key, defaultData);
    }

    public static void setData(String key, boolean value) {
        getInstance().edit().putBoolean(key, value).commit();
    }

    public static long getData(String key, long defaultData) {
        long data = getInstance().getLong(key, defaultData);
        LLog.d(TAG, "getData", "key : " + key + ", value : " + data);
        return data;
    }

    public static void setData(String key, long value) {
        LLog.d(TAG, "getData", "key : " + key + ", value : " + value);
        getInstance().edit().putLong(key, value).commit();
    }

    public static int getData(String key, int defaultData) {
        int data = getInstance().getInt(key, defaultData);
        LLog.d(TAG, "getData", "key : " + key + ", value : " + data);
        return data;
    }

    public static void setData(String key, int value) {
        LLog.d(TAG, "setData", "key : " + key + ", value : " + value);
        getInstance().edit().putInt(key, value).commit();
    }

}
