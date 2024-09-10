package com.omotion.contentsx.android.licon.ui.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.ui.album.activity.AlbumInfo;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumTrackItem;
import com.omotion.contentsx.android.licon.ui.player.activity.PlayActivity;

import java.util.List;

public class AlbumTrackRecyclerViewAdapter extends RecyclerView.Adapter<AlbumTrackRecyclerViewHolder> {
    private List<AlbumTrackItem> itemList;
    Context context;

    public AlbumTrackRecyclerViewAdapter(List<AlbumTrackItem> itemList_) {
        itemList = itemList_;
    }

    // ViewHolder 생성 시에 호출된다.
    //처음 화면에 보이는 view 의 ViewHolder 객체를 생성하며, 화면에 보이는 만큼의 ViewHolder 객체가 생성되면 이후 호출되지 않는다.
    //ViewType 형태에 따라 다른 type 의 ViewHolder 객체를 생성할 수 있다.
    @Override
    public AlbumTrackRecyclerViewHolder onCreateViewHolder(ViewGroup a_viewGroup, int a_viewType) {
        View view = LayoutInflater.from(a_viewGroup.getContext()).inflate(R.layout.recyclerview_item_track, a_viewGroup, false);
        context = view.getContext();
        return new AlbumTrackRecyclerViewHolder(view);
    }

    //스크롤 등으로 특정 position 의 data 를 새롭게 표시해야 할 때마다 호출.
    //Position 에 해당하는 데이터를 ViewHolder 에 연결하여 item view 에 표시.
    @Override
    public void onBindViewHolder(AlbumTrackRecyclerViewHolder viewHolder, int position) {
        final AlbumTrackItem item = itemList.get(position);

        if (item.getNumber() < 10) {
            viewHolder.track_num.setText("0" + String.valueOf(item.getNumber()));
        } else {
            viewHolder.track_num.setText(String.valueOf(item.getNumber()));
        }
        viewHolder.track_title.setText(item.getTitle());
        viewHolder.track_artist.setText(item.getArtist());

        if (item.getTitleCheck() == true) {
            viewHolder.track_title_img.setVisibility(View.VISIBLE);
        } else {
            viewHolder.track_title_img.setVisibility(View.INVISIBLE);
        }
        viewHolder.track_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 곡 확인 (현재 재생 중인지)
                MusicPlayer.getInstance().checkCurrentMediaItem(item.getTitle());
                context.startActivity(new Intent(context.getApplicationContext(), PlayActivity.class));
                ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.no_animation);

            }
        });

        viewHolder.track_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), AlbumInfo.class);
                intent.putExtra("TITLE", item.getTitle());
                intent.putExtra("CONTENT", item.getContent());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });

    }

    //전체 item 개수 반환. 반환 되는 개수에 따라 생성되는 Item 의 개수가 정해진다.
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}