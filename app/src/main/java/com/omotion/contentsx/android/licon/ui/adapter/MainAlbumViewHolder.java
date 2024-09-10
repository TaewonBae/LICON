package com.omotion.contentsx.android.licon.ui.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAlbumViewHolder extends RecyclerView.ViewHolder {
    ImageView card_img, card_blur;
    CircleImageView album_img;
    TextView album_title;
    TextView album_singer;
    TextView music;
    TextView movie;
    TextView camera;
    ConstraintLayout clParent;
    ImageButton playBtn;
    ConstraintLayout clNoRegisted;
    Button btnRegist;

    public MainAlbumViewHolder(View itemview) {
        super(itemview);

        card_img = itemview.findViewById(R.id.album_card);
        card_blur = itemview.findViewById(R.id.album_blur);
        album_img = itemview.findViewById(R.id.album_img);
        album_title = itemview.findViewById(R.id.album_title);
        album_singer = itemview.findViewById(R.id.album_singer);
        music = itemview.findViewById(R.id.album_music);
        movie = itemview.findViewById(R.id.album_movie);
        camera = itemview.findViewById(R.id.album_camera);
        clParent = itemview.findViewById(R.id.cl_parent);
        playBtn = itemview.findViewById(R.id.playBtn);
        clNoRegisted = itemview.findViewById(R.id.cl_no_registed);
        btnRegist = itemview.findViewById(R.id.btn_regist);
    }
}
