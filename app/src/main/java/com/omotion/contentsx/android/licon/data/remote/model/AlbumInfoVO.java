package com.omotion.contentsx.android.licon.data.remote.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;


@Keep
public class AlbumInfoVO implements Serializable {
    private String cardAlbumText;
    private String cardPhotoImgBack;
    private String albumImgTitle;
    private String albumImgCover;
    private String albumTitleImg;
    private ArrayList<String> albumURL;
    private ArrayList<String> albumURLMp3;
    private String trackInfo;
    private ArrayList<String> trackTitle;
    private ArrayList<String> cardMediaImgCheck;
    private ArrayList<String> cardMediaYoutube;
    private String cardArtistText;
    private ArrayList<String> trackSinger;
    private ArrayList<String> cardPhotoImg;
    private ArrayList<String> cardMediaImg;
    private String albumInfo;
    private ArrayList<String> albumMusicInfo;


    @PropertyName("Card_album_text")
    public String getCardAlbumText() {
        return cardAlbumText;
    }

    @PropertyName("Card_album_text")
    public void setCardAlbumText(String cardAlbumText) {
        this.cardAlbumText = cardAlbumText;
    }

    @PropertyName("Card_photo_img_back")
    public String getCardPhotoImgBack() {
        return cardPhotoImgBack;
    }

    @PropertyName("Card_photo_img_back")
    public void setCardPhotoImgBack(String cardPhotoImgBack) {
        this.cardPhotoImgBack = cardPhotoImgBack;
    }

    @PropertyName("Album_URL_png")
    public String getAlbumImgTitle() {
        return albumImgTitle;
    }

    @PropertyName("Album_URL_png")
    public void setAlbumImgTitle(String albumImgTitle) {
        this.albumImgTitle = albumImgTitle;
    }

    @PropertyName("Album_URL_png_cover")
    public String getAlbumImgCover() {
        return albumImgCover;
    }

    @PropertyName("Album_URL_png_cover")
    public void setAlbumImgCover(String albumImgCover) {
        this.albumImgCover = albumImgCover;
    }

    @PropertyName("Album_title_img")
    public String getAlbumTitleImg() {
        return albumTitleImg;
    }

    @PropertyName("Album_title_img")
    public void setAlbumTitleImg(String albumTitleImg) {
        this.albumTitleImg = albumTitleImg;
    }

    @PropertyName("Album_URL")
    public ArrayList<String> getAlbumURL() {
        return albumURL;
    }

    @PropertyName("Album_URL")
    public void setAlbumURL(ArrayList<String> albumURL) {
        this.albumURL = albumURL;
    }

    @PropertyName("Album_URL_mp3")
    public ArrayList<String> getAlbumURLMp3() {
        return albumURLMp3;
    }

    @PropertyName("Album_URL_mp3")
    public void setAlbumURLMp3(ArrayList<String> albumURLMp3) {
        this.albumURLMp3 = albumURLMp3;
    }

    @PropertyName("Track_info")
    public String getTrackInfo() {
        return trackInfo;
    }

    @PropertyName("Track_info")
    public void setTrackInfo(String trackInfo) {
        this.trackInfo = trackInfo;
    }

    @PropertyName("Track_title")
    public ArrayList<String> getTrackTitle() {
        return trackTitle;
    }

    @PropertyName("Track_title")
    public void setTrackTitle(ArrayList<String> trackTitle) {
        this.trackTitle = trackTitle;
    }

    @PropertyName("Card_media_youtube")
    public ArrayList<String> getCardMediaYoutube() {
        return cardMediaYoutube;
    }

    @PropertyName("Card_media_youtube")
    public void setCardMediaYoutube(ArrayList<String> cardMediaYoutube) {
        this.cardMediaYoutube = cardMediaYoutube;
    }

    @PropertyName("Card_media_img_check")
    public ArrayList<String> getCardMediaImgCheck() {
        return cardMediaImgCheck;
    }

    @PropertyName("Card_media_img_check")
    public void setCardMediaImgCheck(ArrayList<String> cardMediaImgCheck) {
        this.cardMediaImgCheck = cardMediaImgCheck;
    }

    @PropertyName("Card_artist_text")
    public String getCardArtistText() {
        return cardArtistText;
    }

    @PropertyName("Card_artist_text")
    public void setCardArtistText(String cardArtistText) {
        this.cardArtistText = cardArtistText;
    }

    @PropertyName("Track_singer")
    public ArrayList<String> getTrackSinger() {
        return trackSinger;
    }

    @PropertyName("Track_singer")
    public void setTrackSinger(ArrayList<String> trackSinger) {
        this.trackSinger = trackSinger;
    }

    @PropertyName("Card_photo_img")
    public ArrayList<String> getCardPhotoImg() {
        return cardPhotoImg;
    }

    @PropertyName("Card_photo_img")
    public void setCardPhotoImg(ArrayList<String> cardPhotoImg) {
        this.cardPhotoImg = cardPhotoImg;
    }

    @PropertyName("Card_media_img")
    public ArrayList<String> getCardMediaImg() {
        return cardMediaImg;
    }

    @PropertyName("Card_media_img")
    public void setCardMediaImg(ArrayList<String> cardMediaImg) {
        this.cardMediaImg = cardMediaImg;
    }

    @PropertyName("Album_info")
    public String getAlbumInfo() {
        return albumInfo;
    }

    @PropertyName("Album_info")
    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    @PropertyName("Album_Music_info")
    public ArrayList<String> getAlbumMusicInfo() {
        return albumMusicInfo;
    }

    @PropertyName("Album_Music_info")
    public void setAlbumMusicInfo(ArrayList<String> albumMusicInfo) {
        this.albumMusicInfo = albumMusicInfo;
    }

    @Override
    public String toString() {
        return "AlbumInfoVO{" +
                "cardAlbumText='" + cardAlbumText + '\'' +
                ", cardPhotoImgBack='" + cardPhotoImgBack + '\'' +
                ", Album_URL_png='" + albumImgTitle + '\'' +
                ", Album_URL_png_cover='" + albumImgCover + '\'' +
                ", albumTitleImg='" + albumTitleImg + '\'' +
                ", albumURL=" + albumURL +
                ", albumURLMp3=" + albumURLMp3 +
                ", trackInfo='" + trackInfo + '\'' +
                ", trackTitle=" + trackTitle +
                ", cardMediaImgCheck=" + cardMediaImgCheck +
                ", cardMediaYoutube=" + cardMediaYoutube +
                ", cardArtistText='" + cardArtistText + '\'' +
                ", trackSinger=" + trackSinger +
                ", cardPhotoImg=" + cardPhotoImg +
                ", cardMediaImg=" + cardMediaImg +
                ", albumInfo='" + albumInfo + '\'' +
                ", albumMusicInfo=" + albumMusicInfo +
                '}';
    }
}
