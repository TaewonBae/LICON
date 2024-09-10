package com.omotion.contentsx.android.licon.ui.album.vo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AlbumMediaItem implements Serializable, Parcelable {
    private Uri imageUri;
    private String mediaUrl;

    private String youtubeTitle;
    private int position;


    public AlbumMediaItem(Uri imageUri_, String mediaUrl_, String youtubeTitle_, int position_) {
        imageUri = imageUri_;
        mediaUrl = mediaUrl_;
        youtubeTitle = youtubeTitle_;
        position = position_;
    }


    protected AlbumMediaItem(Parcel in) {
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        mediaUrl = in.readString();
        youtubeTitle = in.readString();
        position = in.readInt();
    }

    public static final Creator<AlbumMediaItem> CREATOR = new Creator<AlbumMediaItem>() {
        @Override
        public AlbumMediaItem createFromParcel(Parcel in) {
            return new AlbumMediaItem(in);
        }

        @Override
        public AlbumMediaItem[] newArray(int size) {
            return new AlbumMediaItem[size];
        }
    };

    public Uri getImageUri() {
        return imageUri;
    }

    public int getPosition() {
        return position;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getYotubeTitle() {
        return youtubeTitle;
    }

    @Override
    public String toString() {
        return "AlbumMediaItem{" +
                "imageId='" + imageUri + '\'' +
                ", bool_media=" + mediaUrl +
                ", position=" + position +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(imageUri, i);
        parcel.writeString(mediaUrl);
        parcel.writeString(youtubeTitle);
        parcel.writeInt(position);
    }
}