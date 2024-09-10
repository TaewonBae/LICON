package com.omotion.contentsx.android.licon.ui.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.ui.album.activity.PhotoCardActivity;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumPhotoItem;

import java.util.ArrayList;

public class AlbumPhotoGridViewAdapter extends BaseAdapter {
    ArrayList<AlbumPhotoItem> items = new ArrayList<AlbumPhotoItem>();
    Context context;

    public void addItem(AlbumPhotoItem item) {
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
    public View getView(int position, View converView, ViewGroup parent) {
        context = parent.getContext(); //context 객체는 activity 정보를 읽어올 수 있다.
        AlbumPhotoItem listItem = items.get(position); //position에 해당하는 listItem

        //gridview_item을 inflate하고 converView를 참조.
        if (converView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = inflater.inflate(R.layout.gridview_item_photo, parent, false);
        }

        // 부모 뷰를 3등분하기
        ViewGroup.LayoutParams clsParam = converView.getLayoutParams();
        clsParam.width = (int) Math.round(RBW.deviceWidth / 3);
        clsParam.height = (int) Math.round(clsParam.width * 1.55);
        converView.setLayoutParams(clsParam);

        ImageView photo_img = converView.findViewById(R.id.photo_img);
        Glide.with(context).load(listItem.getImageUri()).into(photo_img);

        photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RBW.album_photo = listItem.getPosition();
                Intent intent = new Intent(context, PhotoCardActivity.class);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });
        return converView;
    }
}

