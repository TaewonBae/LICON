package com.omotion.contentsx.android.licon.ui.alert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.ui.BaseActivity;
import com.omotion.contentsx.android.licon.ui.alert.adapter.AlertAdapter;
import com.omotion.contentsx.android.licon.ui.alert.fragment.AlertFAQFragment;
import com.omotion.contentsx.android.licon.ui.alert.fragment.AlertNoticeFragment;
import com.omotion.contentsx.android.licon.ui.alert.fragment.AlertO3Fragment;

public class AlertActivity extends BaseActivity {

    private ViewPager2 viewPager2;
    private AlertAdapter alertAdapter;
    private AlertNoticeFragment fragment1;
    private AlertFAQFragment fragment2;
    private AlertO3Fragment fragment3;
    private TabLayout tabLayout;

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        backBtn = findViewById(R.id.backBtn);

        createFragment();
        createViewpager();
        settingTabLayout();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        // 추가된 부분 중복등록시 FAQ로 이동하도록
        Intent intent = getIntent();
        int selectedTab = intent.getIntExtra("selected_tab", -1);
        if (selectedTab != -1) {
            RBW.customerService = true;
            viewPager2.setCurrentItem(selectedTab);
        }
    }

    private void exitActivity() {
        RBW.customerService = false;
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    private void createFragment() {
        fragment1 = new AlertNoticeFragment();
        fragment2 = new AlertFAQFragment();
        fragment3 = new AlertO3Fragment();
    }

    private void createViewpager() {
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager2_container);
        alertAdapter = new AlertAdapter(getSupportFragmentManager(), getLifecycle());
        alertAdapter.addFragment(fragment1);
        alertAdapter.addFragment(fragment2);
//        alertAdapter.addFragment(fragment3);

        viewPager2.setAdapter(alertAdapter);
//        viewPager2.setUserInputEnabled(false); //이 줄을 주석처리 하면 슬라이딩으로 탭을 바꿀 수 있다

    }

    private void settingTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText(getResources().getString(R.string.notice)); // 공지사항
                        break;
                    }
                    case 1: {
                        tab.setText("FAQ"); // FAQ
                        break;
                    }
                    case 2: {
                        tab.setText("1:1문의");
                        break;
                    }
                }
            }
        }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        viewPager2.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager2.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager2.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
}