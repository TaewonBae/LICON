package com.omotion.contentsx.android.licon.core.player;

import com.omotion.contentsx.android.licon.data.remote.model.Song;

public interface MusicPlayListener {
    void onPlayTimeMillis(long timeMillis);
    void onPlayTimeChanged(String playTime, int percent);
    void onPlayStatusChanged(MusicPlayer.PlayStatus status);
    void onSongInfoChanged(Song song);
}
