package com.omotion.contentsx.android.licon.ui.player.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;


public class LyricsRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView tvLyric;
//    TextView tvPronun;

    public LyricsRecyclerViewHolder(View itemview){
        super(itemview);
        tvLyric = itemview.findViewById(R.id.tv_lyric);
//        tvPronun = itemview.findViewById(R.id.tv_pronun);
    }
}
