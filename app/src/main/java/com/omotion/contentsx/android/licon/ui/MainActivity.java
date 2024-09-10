package com.omotion.contentsx.android.licon.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.contents.ContentsManagerListener;
import com.omotion.contentsx.android.licon.core.nfc.NfcManager;
import com.omotion.contentsx.android.licon.core.nfc.NfcManagerListener;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.CommonLogic;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.SharedPrefManager;
import com.omotion.contentsx.android.licon.data.remote.model.HanteoChart;
import com.omotion.contentsx.android.licon.ui.adapter.MainAlbumAdapter;
import com.omotion.contentsx.android.licon.ui.album.activity.MyAlbumActivity;
import com.omotion.contentsx.android.licon.ui.alert.activity.AlertActivity;
import com.omotion.contentsx.android.licon.ui.alert.fragment.AlertFAQFragment;
import com.omotion.contentsx.android.licon.ui.vo.MainAlbumItem;
import com.omotion.contentsx.android.licon.ui.widget.NfcInformDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ContentsManagerListener, NfcManagerListener {
    private RecyclerView rvMain; // 메인 리사이클러 뷰
    private ImageButton shop_btn; // shop 버튼
    private ImageButton myalbum_btn; // 앨범 보관함 버튼
    private ImageButton bell_btn; // 알림 버튼

    private static MainAlbumAdapter mainAlbumAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NfcManager.getInstance().init(this);
        NfcManager.getInstance().addListener(this);
        ContentsManager.getInstance().init(this);
        ContentsManager.getInstance().addListener(this);
        MusicPlayer.getInstance().addMusicPlayListener(this);
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
        //레이아웃을 위에 겹쳐서 올리는 부분
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //레이아웃 객체생성
        if (!SharedPrefManager.getData(SharedPrefManager.KEY_HAS_SEEN_TUTORIAL, false)) { // 요기 // 튜토리얼 한번만 나오게하는 코드 >> 만약 계속 나오게 하고싶으면 뒤에 //요기 라고 되있는곳들 //쳐주면됨
            ConstraintLayout ll = (ConstraintLayout) inflater.inflate(R.layout.tutorial_layout, null);

            //레이아웃 위에 겹치기
            ConstraintLayout.LayoutParams paramll = new ConstraintLayout.LayoutParams
                    (LinearLayout.LayoutParams.FILL_PARENT, ConstraintLayout.LayoutParams.FILL_PARENT);
            addContentView(ll, paramll);
            //위에겹친 레이아웃에 온클릭 이벤트주기
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewManager) ll.getParent()).removeView(ll);
                    SharedPrefManager.setData(SharedPrefManager.KEY_HAS_SEEN_TUTORIAL, true); // 요기 // 튜토리얼 한번만 나오게하는 코드 >> 만약 계속 나오게 하고싶으면 뒤에 //요기 라고 되있는곳들 //쳐주면됨
                }
            });
        }// 요기 // 튜토리얼 한번만 나오게하는 코드 >> 만약 계속 나오게 하고싶으면 뒤에 //요기 라고 되있는곳들 //쳐주면됨

        shop_btn = findViewById(R.id.shop_btn); // 샵 버튼
        myalbum_btn = findViewById(R.id.myalbum_btn); // 앨범 보관함 버튼
        bell_btn = findViewById(R.id.bell_btn); // 알림 버튼


        //디바이스 가로 세로 구하기
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int devicewidth = displaymetrics.widthPixels;
        int deviceheight = displaymetrics.heightPixels;
        LLog.d(TAG, "init", "width : " + devicewidth + "height : " + deviceheight);
        RBW.deviceWidth = devicewidth;

        // 샵 버튼 클릭 이벤트
        shop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bizent.co.kr/"));
                startActivity(urlintent);
            }
        });

        // 앨범 보관함 버튼 클릭 이벤트
        myalbum_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MyAlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
            }
        });

        //bell 아이콘 클릭 이벤트
        bell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
            }
        });
        List<MainAlbumItem> itemList = new ArrayList<>();
        boolean isPreparedSongs = MusicPlayer.getInstance().isSongsPrepared();

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.album_cover);

        itemList.add(new MainAlbumItem(R.drawable.album_img, R.drawable.album_cover,
                "GIUK 2ND MINI ALBUM [現像 : 소년의 파란]", "GIUK", "8", "7", "5", isPreparedSongs, false));


        // Adapter 추가
        mainAlbumAdapter = new MainAlbumAdapter();
        mainAlbumAdapter.setList(itemList);
        mainAlbumAdapter.notifyDataSetChanged();
        rvMain = findViewById(R.id.rv_main); // Middle Recycler view
        rvMain.setAdapter(mainAlbumAdapter);
        // Layout manager 추가
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rvMain.setLayoutManager(layoutManager);
    }

    public static void setAlbumItemViewRegist(boolean isRegist) {
        if (mainAlbumAdapter != null) {
            MainAlbumItem item = mainAlbumAdapter.getItemList().get(0);
            item.setHasRegisted(isRegist);
            mainAlbumAdapter.notifyDataSetChanged();
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

            tvTitle.setText(getResources().getString(R.string.dialog_success));
            tvContent.setText(getResources().getString(R.string.dialog_success2));
            btnLeft.setText(getResources().getString(R.string.dialog_success_btn));
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonLogic.goToAlbumActivity(MainActivity.this, true);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }else {
            Toast.makeText(this, getResources().getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayStatusChanged(MusicPlayer.PlayStatus status) {
        super.onPlayStatusChanged(status);
        if (mainAlbumAdapter != null) {
            MainAlbumItem item = mainAlbumAdapter.getItemList().get(0);
            item.setPlaying(status == MusicPlayer.PlayStatus.PLAY);
            item.setAlbumId(R.drawable.album_img);
            mainAlbumAdapter.notifyDataSetChanged();
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
            case ERROR: // 시리얼 코드가 다르거나 등록 실패 했을 경우
                tvTitle.setText(getResources().getString(R.string.dialog_fail)); //등록 실패
                tvContent.setText(getResources().getString(R.string.dialog_fail2)); //잠시 후 다시 시도해주세요.
                dialog.findViewById(R.id.btn_left).setVisibility(View.GONE);
                dialog.findViewById(R.id.btn_right).setBackgroundResource(R.drawable.dialog_bottom_corner_blue);
                break;
            case DUPLICATED: // 중복 등록 했을 경우
                tvTitle.setText(getResources().getString(R.string.dialog_fail)); //등록 실패
                tvContent.setText(getResources().getString(R.string.dialog_dup_text));  // 이미 등록된 앨범입니다. 재등록은 고객센터를 통해 문의해주세요.
                btnLeft.setText(getResources().getString(R.string.dialog_btn_customer));
                btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                        // AlertActivity로 이동 후 FAQ 탭으로 설정
                        intent.putExtra("selected_tab", 1); // 1은 FAQ 탭의 인덱스
                        overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
                        startActivity(intent);

                        dialog.dismiss(); // 중복 다이얼로그 닫기
                    }
                });
                break;
        }
        dialog.show();
    }


}