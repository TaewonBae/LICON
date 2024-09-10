package com.omotion.contentsx.android.licon.ui.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.ui.album.activity.AlbumMediaPhoto;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumMediaItem;

import java.util.ArrayList;

public class AlbumMediaGridViewAdapter extends BaseAdapter {
    private final String TAG = this.getClass().getName();
    ArrayList<AlbumMediaItem> items = new ArrayList<AlbumMediaItem>();
    Context context;

    public void addItem(AlbumMediaItem item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext(); //context 객체는 activity 정보를 읽어올 수 있다.
        }

        AlbumMediaItem listItem = items.get(position); //position에 해당하는 listItem

        //gridview_item을 inflate하고 converView를 참조.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_item_media, parent, false);
        }

        // 부모 뷰를 3등분하기
        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        layoutParams.width = (int) Math.round(RBW.deviceWidth / 3);
        layoutParams.height = (int) Math.round(RBW.deviceWidth / 3);
        convertView.setLayoutParams(layoutParams);


        ImageView mediaImg = convertView.findViewById(R.id.media_img);
        ImageView mediaIcon = convertView.findViewById(R.id.media_icon);

        Glide.with(context).load(listItem.getImageUri()).into(mediaImg);

        if (!listItem.getMediaUrl().isEmpty()) {
            mediaIcon.setImageResource(R.drawable.ic_media_video);
        }

        mediaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlbumMediaPhoto.class);
                intent.putExtra("listItem", (Parcelable) listItem);
                ArrayList<AlbumMediaItem> mediaImageList = new ArrayList<>();
                ArrayList<AlbumMediaItem> mediaYoutubeList = new ArrayList<>();
                for (AlbumMediaItem item : items) {
                    if (listItem.getMediaUrl().isEmpty() == item.getMediaUrl().isEmpty()) {
                        mediaImageList.add(item);
                        mediaYoutubeList.add(item);
                    }
                }
                intent.putParcelableArrayListExtra("mediaImageList", mediaImageList);
                intent.putParcelableArrayListExtra("mediaYoutubeList", mediaYoutubeList);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });
        return convertView;
    }
}
