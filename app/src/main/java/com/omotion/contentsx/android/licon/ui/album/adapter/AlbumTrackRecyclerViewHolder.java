package com.omotion.contentsx.android.licon.ui.album.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.omotion.contentsx.android.licon.R;


public class AlbumTrackRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView track_num;
    TextView track_title;
    TextView track_artist;
    ImageView track_title_img;
    ImageButton track_moreBtn;
    ConstraintLayout track_parent;

    public AlbumTrackRecyclerViewHolder(View itemview){
        super(itemview);
        track_num = itemview.findViewById(R.id.track_num);
        track_title = itemview.findViewById(R.id.track_title);
        track_artist = itemview.findViewById(R.id.track_artist);
        track_title_img = itemview.findViewById(R.id.track_title_img);
        track_moreBtn = itemview.findViewById(R.id.track_moreBtn);
        track_parent = itemview.findViewById(R.id.cl_track);
    }
}
