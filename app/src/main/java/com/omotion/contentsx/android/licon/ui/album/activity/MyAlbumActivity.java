package com.omotion.contentsx.android.licon.ui.album.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.contents.ContentsManagerListener;
import com.omotion.contentsx.android.licon.core.nfc.NfcManager;
import com.omotion.contentsx.android.licon.core.nfc.NfcManagerListener;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.CommonLogic;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.ui.BaseActivity;
import com.omotion.contentsx.android.licon.ui.album.adapter.MyAlbumGridViewAdapter;
import com.omotion.contentsx.android.licon.ui.album.vo.MyAlbumItem;
import com.omotion.contentsx.android.licon.ui.widget.NfcInformDialog;

import java.util.ArrayList;
import java.util.List;

public class MyAlbumActivity extends BaseActivity implements ContentsManagerListener, NfcManagerListener {
    private GridView gridView;
    private ImageButton backBtn;
    private TextView albumLibraryText;
    private static MyAlbumGridViewAdapter myAlbumGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myalbum);

        NfcManager.getInstance().init(this);
        NfcManager.getInstance().addListener(this);
        ContentsManager.getInstance().addListener(this);
        ContentsManager.getInstance().setProgressContext(this);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NfcManager.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcManager.getInstance().onPause();
    }

    private void init() {
        gridView = findViewById(R.id.myalbum_gridView);
        backBtn = findViewById(R.id.backBtn);

        albumLibraryText = findViewById(R.id.albumLibraryText);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        List<MyAlbumItem> itemList = new ArrayList<>();
        boolean isRegist = MusicPlayer.getInstance().isSongsPrepared();
        itemList.add(new MyAlbumItem(R.drawable.album_img, "GIUK 2ND MINI ALBUM [現像 : 소년의 파란]", "GIUK", isRegist));
        myAlbumGridViewAdapter = new MyAlbumGridViewAdapter();
        myAlbumGridViewAdapter.setItemList(itemList);
        myAlbumGridViewAdapter.notifyDataSetChanged();
        gridView.setAdapter(myAlbumGridViewAdapter);
        albumLibraryText.setText(getResources().getString(R.string.myalbum) + " (" + myAlbumGridViewAdapter.getCount() + ")"); // 앨범 보관함 + (cnt)
    }

    private void exitActivity() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    public static void setAlbumItemViewRegist(boolean isRegist) {
        if (myAlbumGridViewAdapter != null) {
            MyAlbumItem item = myAlbumGridViewAdapter.getItemList().get(0);
            item.setHasRegisted(isRegist);
            myAlbumGridViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onContentsPassResult(boolean isPass) {
        LLog.d(TAG, "onContentsPassResult", "isPass: " + isPass);
        if (isPass) {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_register4);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView tvTitle = dialog.findViewById(R.id.tv_title);
            TextView tvContent = dialog.findViewById(R.id.tv_content);
            Button btnLeft = dialog.findViewById(R.id.btn_left);
            dialog.findViewById(R.id.btn_right).setOnClickListener(view -> dialog.dismiss());

            tvTitle.setText("등록 성공");
            tvContent.setText("앨범이 등록되었습니다.");
            btnLeft.setText("포토카드 확인");
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonLogic.goToAlbumActivity(MyAlbumActivity.this, true);
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNfcPassResult(NfcManager.RegistResult result) {
        if (result == NfcManager.RegistResult.NO_REGISTERED) {
            NfcInformDialog dialog = new NfcInformDialog(this);
            dialog.setErrorInformView();
            dialog.show();
            return;
        }
        boolean isResultOk = result == NfcManager.RegistResult.SUCCESS;
        setAlbumItemViewRegist(isResultOk);
        if (isResultOk) {
            ContentsManager.getInstance().processCheckDownload();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register3);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvContent = dialog.findViewById(R.id.tv_content);
        Button btnLeft = dialog.findViewById(R.id.btn_left);
        dialog.findViewById(R.id.btn_right).setOnClickListener(view -> dialog.dismiss());
        switch (result) {
            case ERROR:
                tvTitle.setText(getResources().getString(R.string.dialog_fail));
                tvContent.setText(getResources().getString(R.string.dialog_fail2));
                dialog.findViewById(R.id.btn_left).setVisibility(View.GONE);
                dialog.findViewById(R.id.btn_right).setBackgroundResource(R.drawable.dialog_bottom_corner_blue);
                break;
            case DUPLICATED:
                tvTitle.setText(getResources().getString(R.string.dialog_fail));
                tvContent.setText(getResources().getString(R.string.dialog_dup_text));
                btnLeft.setText(getResources().getString(R.string.dialog_btn_customer));
                btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.show();
    }
}