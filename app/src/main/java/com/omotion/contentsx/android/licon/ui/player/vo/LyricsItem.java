package com.omotion.contentsx.android.licon.ui.player.vo;

public class LyricsItem {
    private String timeLine;
    private String lyric;
//    private String pronun;

    private boolean isFocused = false;

    public LyricsItem(String timeLine, String lyric/*, String pronun*/) {
        this.timeLine = timeLine;
        this.lyric = lyric;
//        this.pronun = pronun;
    }

    public String getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(String timeLine) {
        this.timeLine = timeLine;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

   /* public String getPronun() {
        return pronun;
    }

    public void setPronun(String pronun) {
        this.pronun = pronun;
    }*/

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    @Override
    public String toString() {
        return "LyricsItem{" +
                "timeLine='" + timeLine + '\'' +
                ", lyric='" + lyric + '\'' +
                '}';
    }
}
