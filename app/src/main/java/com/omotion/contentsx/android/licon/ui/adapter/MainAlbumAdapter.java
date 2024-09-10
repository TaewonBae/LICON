package com.omotion.contentsx.android.licon.ui.adapter;


import static com.google.common.reflect.Reflection.getPackageName;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.NetworkUtils;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.nfc.NfcManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.CommonLogic;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.SharedPrefManager;
import com.omotion.contentsx.android.licon.core.util.Utils;
import com.omotion.contentsx.android.licon.data.remote.model.AlbumInfoVO;
import com.omotion.contentsx.android.licon.ui.player.activity.QRActivity;
import com.omotion.contentsx.android.licon.ui.vo.MainAlbumItem;

import java.util.List;

public class MainAlbumAdapter extends RecyclerView.Adapter<MainAlbumViewHolder> {
    private final String TAG = this.getClass().getName();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_FOOTER = 1;

    private List<MainAlbumItem> itemList;
    private AlbumInfoVO albumItem = new AlbumInfoVO();
    Context context;
    Dialog dialog;

    public MainAlbumAdapter() {
    }

    public void setList(List<MainAlbumItem> itemList_) {
        itemList = itemList_;
    }

    public List<MainAlbumItem> getItemList() {
        return itemList;
    }

    // ViewHolder 생성 시에 호출된다.
    //처음 화면에 보이는 view 의 ViewHolder 객체를 생성하며, 화면에 보이는 만큼의 ViewHolder 객체가 생성되면 이후 호출되지 않는다.
    //ViewType 형태에 따라 다른 type 의 ViewHolder 객체를 생성할 수 있다.
    @Override
    public MainAlbumViewHolder onCreateViewHolder(ViewGroup a_viewGroup, int a_viewType) {
        View view;
        //if (a_viewType == VIEW_TYPE_FOOTER) { // with LICON | Platform Album 지우기
        //    view = LayoutInflater.from(a_viewGroup.getContext()).inflate(R.layout.main_album_adapter_footer, a_viewGroup, false);// with LICON | Platform Album 지우기
        //} else {// with LICON | Platform Album 지우기
            view = LayoutInflater.from(a_viewGroup.getContext()).inflate(R.layout.main_album_adapter, a_viewGroup, false);
            context = view.getContext();
        //}// with LICON | Platform Album 지우기
        return new MainAlbumViewHolder(view);
    }

    //스크롤 등으로 특정 position 의 data 를 새롭게 표시해야 할 때마다 호출.
    //Position 에 해당하는 데이터를 ViewHolder 에 연결하여 item view 에 표시.
    @Override
    public void onBindViewHolder(MainAlbumViewHolder viewHolder, int position) {
        if (position < itemList.size()) {
            final MainAlbumItem item = itemList.get(position);
            LLog.d(TAG, "onBindViewHolder", "item" + position + "::: => " + item);

            //viewHolder.album_img.setImageResource(item.getCardImage());
            viewHolder.card_img.setClipToOutline(true);


            //Glide.with(context).load(item.getAlbumId()).into(viewHolder.album_img); //여기 입니다 쉔쉐이
            if (ContentsManager.getInstance().getAlbumImgCover() != null)
            {
                Glide.with(context).load(ContentsManager.getInstance().getAlbumImgTitle()).into(viewHolder.card_img); //여기 입니다 쉔쉐이
                Glide.with(context).load(ContentsManager.getInstance().getAlbumImgCover()).into(viewHolder.album_img); //여기 입니다 쉔쉐이

                viewHolder.album_title.setText(ContentsManager.getInstance().getAlbumInfoVO().getCardAlbumText());
                viewHolder.album_title.setSelected(true);

                viewHolder.album_singer.setText(ContentsManager.getInstance().getAlbumInfoVO().getCardArtistText());
                viewHolder.music.setText(Integer.toString(ContentsManager.getInstance().getAlbumInfoVO().getAlbumURLMp3().size()));
                viewHolder.movie.setText(Integer.toString(ContentsManager.getInstance().getAlbumInfoVO().getCardMediaImg().size()));
                viewHolder.camera.setText(Integer.toString(ContentsManager.getInstance().getAlbumInfoVO().getCardPhotoImg().size()));
            }
            else
            {
                viewHolder.album_title.setText(item.getTitle());
                viewHolder.album_title.setSelected(true);

                viewHolder.album_singer.setText(item.getArtist());
                viewHolder.music.setText(item.getMusicNum());
                viewHolder.movie.setText(item.getMovieNum());
                viewHolder.camera.setText(item.getCameraNum());

            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.clParent.getLayoutParams();
            layoutParams.height = (int) Math.round((RBW.deviceWidth - (2 * Utils.getDpToPixel(context, 24))) * 1.545);
            viewHolder.clParent.setLayoutParams(layoutParams);
            viewHolder.playBtn.setImageResource(item.isPlaying() ? R.drawable.ic_main_pause : R.drawable.ic_main_play);



            if (item.hasRegisted()) {
                viewHolder.clNoRegisted.setVisibility(View.GONE); // 아직 등록되지 않은 앨범 + 등록하기 버튼 레이아웃 OFF
                viewHolder.playBtn.setVisibility(View.VISIBLE);
                viewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MusicPlayer.getInstance().onPlayEvent();

                    }

                });
                viewHolder.card_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonLogic.goToAlbumActivity((Activity) context, false);
                    }
                });
                viewHolder.album_img.setVisibility(View.VISIBLE);
            } else {
                viewHolder.clNoRegisted.setVisibility(View.VISIBLE); // 아직 등록되지 않은 앨범 + 등록하기 버튼 레이아웃 ON
                viewHolder.playBtn.setVisibility(View.GONE);
                viewHolder.card_blur.setClipToOutline(true); // 이거 추가함
                viewHolder.btnRegist.setOnClickListener(new View.OnClickListener() { // 등록하기 버튼
                    @Override
                    public void onClick(View view) {
                        if (NetworkUtils.isNetworkAvailable(context)) { // 네트워크 연결된 상태일 경우
                            // 네트워크 사용 가능
                            Log.d("NetworkStatus", "네트워크 사용 가능");
                            String targetString = SharedPrefManager.getData(SharedPrefManager.SERIAL_KEY, "");
                            if (!targetString.isEmpty() && targetString != null) {
                                NfcManager.getInstance().callRegistrationApi(targetString);
                            } else {
                                register_showDialog();
                            }
                        } else { // 네트워크 연결되지 않은 상태일 경우
                            // 네트워크 사용 불가능
                            Log.d("NetworkStatus", "네트워크 사용 불가능");
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("네트워크 설정")
                                    .setMessage("Wi-Fi 또는 모바일 데이터 설정이 필요합니다.")
                                    .setCancelable(false)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent= new Intent(Settings.ACTION_WIRELESS_SETTINGS); // 네트워크 설정창으로 이동
                                            context.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog dialog  = builder.create();
                            dialog.show();

                        }
//                        String targetString = SharedPrefManager.getData(SharedPrefManager.SERIAL_KEY, "");
//                        if (!targetString.isEmpty() && targetString != null) {
//                            NfcManager.getInstance().callRegistrationApi(targetString);
//                        } else {
//                            register_showDialog();
//                        }
                    }
                });
                //viewHolder.album_img.setVisibility(View.INVISIBLE);
            }
        }
    }

    //전체 item 개수 반환. 반환 되는 개수에 따라 생성되는 Item 의 개수가 정해진다.
    @Override
    public int getItemCount() {
        if (itemList == null) {
            return 0;
        }
        //return itemList.size() + 1; // with LICON | Platform Album 지우기
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //if (position == itemList.size()) { // with LICON | Platform Album 지우기
        //    return VIEW_TYPE_FOOTER; // with LICON | Platform Album 지우기
        //} else { // with LICON | Platform Album 지우기
            return VIEW_TYPE_ITEM;
        //} // with LICON | Platform Album 지우기
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
