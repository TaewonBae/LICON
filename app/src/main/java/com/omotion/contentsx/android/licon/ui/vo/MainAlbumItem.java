package com.omotion.contentsx.android.licon.ui.vo;

import android.net.Uri;

import java.io.Serializable;

public class MainAlbumItem implements Serializable {
    private int cardId;
    private int albumId;
    private String str_title;
    private String str_artist;
    private String str_musicNum;
    private String str_movieNum;
    private String str_cameraNum;
    private boolean hasRegisted;

    private boolean isPlaying;

    public MainAlbumItem(int cardId_, int albumId_, String str_title_, String str_artist_, String str_musicNum_, String str_movieNum_, String str_cameraNum_, boolean hasRegisted_, boolean isPlaying_) {
        cardId = cardId_;
        albumId = albumId_;
        str_title = str_title_;
        str_artist = str_artist_;
        str_musicNum = str_musicNum_;
        str_movieNum = str_movieNum_;
        str_cameraNum = str_cameraNum_;
        hasRegisted = hasRegisted_;
        isPlaying = isPlaying_;

    }

    public int getCardImage() {
        return cardId;
    }


    public String getTitle() {
        return str_title;
    }

    public String getArtist() {
        return str_artist;
    }

    public String getMusicNum() {
        return str_musicNum;
    }

    public String getMovieNum() {
        return str_movieNum;
    }

    public String getCameraNum() {
        return str_cameraNum;
    }

    public boolean hasRegisted() {
        return hasRegisted;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setHasRegisted(boolean hasRegisted) {
        this.hasRegisted = hasRegisted;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "MainAlbumItem{" +
                "cardId=" + cardId +
                ", albumId=" + albumId +
                ", str_title='" + str_title + '\'' +
                ", str_artist='" + str_artist + '\'' +
                ", str_musicNum='" + str_musicNum + '\'' +
                ", str_movieNum='" + str_movieNum + '\'' +
                ", str_cameraNum='" + str_cameraNum + '\'' +
                ", hasRegisted=" + hasRegisted +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
