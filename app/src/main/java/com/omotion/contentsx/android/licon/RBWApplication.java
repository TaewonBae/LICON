package com.omotion.contentsx.android.licon;

import android.app.Application;
import android.content.Context;

import com.omotion.contentsx.android.licon.core.player.MusicPlayer;

public class RBWApplication extends Application {
    public static Context APP_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;
        MusicPlayer.getInstance().init(this);
    }

    public static Context getContext() {
        return APP_CONTEXT;
    }
}
