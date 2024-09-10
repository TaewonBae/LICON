package com.omotion.contentsx.android.licon.ui.album.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.nfc.NfcManager;
import com.omotion.contentsx.android.licon.core.util.CommonLogic;
import com.omotion.contentsx.android.licon.core.util.SharedPrefManager;
import com.omotion.contentsx.android.licon.ui.album.vo.MyAlbumItem;
import com.omotion.contentsx.android.licon.ui.player.activity.QRActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAlbumGridViewAdapter extends BaseAdapter {
    List<MyAlbumItem> itemList = new ArrayList<MyAlbumItem>();
    Context context;
    Dialog dialog;

    public MyAlbumGridViewAdapter() {
    }

    public List<MyAlbumItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<MyAlbumItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        context = parent.getContext(); //context 객체는 activity 정보를 읽어올 수 있다.
        MyAlbumItem listItem = itemList.get(position); //position에 해당하는 listItem

        //gridview_item을 inflate하고 converView를 참조.
        if (converView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = inflater.inflate(R.layout.gridview_item_myalbum, parent, false);
        }


        ImageView myalbum_blur = converView.findViewById(R.id.album_blur); // shadow할당
        ImageView myalbum_img = converView.findViewById(R.id.myalbum_img);
        RelativeLayout rlNoRegist = converView.findViewById(R.id.rl_no_registed);
        TextView myalbum_title = converView.findViewById(R.id.myalbum_title);
        TextView myalbum_artist = converView.findViewById(R.id.myalbum_artist);

        myalbum_blur.setClipToOutline(true); // shadow 코너 지우기
        myalbum_title.setText(listItem.getTitle());
        myalbum_title.setSelected(true);
        myalbum_artist.setText(listItem.getArtist());
        myalbum_img.setImageResource(listItem.getImage());
        myalbum_img.setClipToOutline(true);

        if (listItem.hasRegisted()) {
            rlNoRegist.setVisibility(View.GONE);
            myalbum_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonLogic.goToAlbumActivity((Activity) context, false);
                }
            });
        } else {
            rlNoRegist.setVisibility(View.VISIBLE);
            converView.findViewById(R.id.btn_regist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String targetString = SharedPrefManager.getData(SharedPrefManager.SERIAL_KEY, "");
                    if (!targetString.isEmpty() && targetString != null) {
                        NfcManager.getInstance().callRegistrationApi(targetString);
                    } else {
                        register_showDialog();
                    }
                }
            });
        }
        return converView;
    }

    private void register_showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageButton nfc_Btn = (ImageButton) dialog.findViewById(R.id.nfc_button); // 닫기 버튼
        //확인
        nfc_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NfcManager.getInstance().checkRegistration();
                dialog.dismiss();
            }
        });

        ImageButton qr_Btn = (ImageButton) dialog.findViewById(R.id.qr_button); // 닫기 버튼
        //확인
        qr_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), QRActivity.class);
                context.startActivity(intent);
            }
        });

        Button closeBtn = (Button) dialog.findViewById(R.id.close_btn); // 닫기 버튼
        //확인
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}