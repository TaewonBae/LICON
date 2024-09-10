package com.omotion.contentsx.android.licon.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.util.LLog;

import java.util.Map;

public class SplashActivity extends BaseActivity {
    private int mPermissionRequestCount;
    private String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 전체화면 >> 상태바 숨김
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check permission
            if (savedInstanceState == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mPermissions = new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.FOREGROUND_SERVICE
                    };
                } else {
                    mPermissions = new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                }
                ensurePermissions();
            }
        } else {
            startLoading();
        }
    }

    //region //startLoading함수 선언 및 스플레쉬스크린 실행 후 Intent로 메인으로 넘어가게하기
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);//2500이 적당
    }//endregion

    private void ensurePermissions() {
        boolean requestPermission = false;

        for (String permission : mPermissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                requestPermission = true;
                break;
            }
        }

        if (!requestPermission) {
            startLoading();
        } else {
            mPermissionResultLauncher.launch(mPermissions);
        }
    }

    private final ActivityResultLauncher<Intent> mSettingApplicationResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    LLog.d(TAG, "onActivityResult()", "application setting after : " + result.getResultCode());

                    mPermissionResultLauncher.launch(mPermissions);
                }
            });

    private final ActivityResultLauncher<String[]> mPermissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean isDenied = false;

                    for (Map.Entry<String, Boolean> elem : result.entrySet()) {
                        Boolean value = elem.getValue();

                        if (!value) {
                            isDenied = true;
                            break;
                        }
                    }

                    LLog.d(TAG, "onActivityResult()", "isDenied : " + isDenied);

                    if (isDenied) {
                        Dialog dlg = new Dialog(mCurrentActivity);
                        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dlg.setContentView(R.layout.dialog_register2);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        ((TextView) dlg.findViewById(R.id.text1)).setText("권한이 없으면 서비스를 이용할 수 없습니다.");
                        Button btnCancel = (Button) dlg.findViewById(R.id.close_btn);
                        btnCancel.setText("취소");
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dlg.cancel();
                                finishAndRemoveTask();
                            }
                        });
                        Button btnConfirm = (Button) dlg.findViewById(R.id.register_btn);
                        btnConfirm.setText("확인");
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dlg.cancel();
                                mPermissionRequestCount++;
                                if (mPermissionRequestCount > 1) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + mCurrentActivity.getPackageName()));

                                    mSettingApplicationResultLauncher.launch(intent);
                                } else {
                                    mPermissionResultLauncher.launch(mPermissions);
                                }
                            }
                        });
                        dlg.show();
                    } else {
                        startLoading();
                    }
                }
            });
}