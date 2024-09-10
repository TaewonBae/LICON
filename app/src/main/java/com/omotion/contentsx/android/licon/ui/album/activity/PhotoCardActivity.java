package com.omotion.contentsx.android.licon.ui.album.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.ui.BaseActivity;

import java.util.List;

public class PhotoCardActivity extends BaseActivity {
    private ImageButton backBtn;
    private Button cardFrontBtn;
    private Button cardBackBtn;
    private ImageView cardImg;
    private CardView cardView;
    private ImageButton prevBtn;
    private ImageButton nextBtn;
    private TextView photoCountText;

    private GestureDetectorCompat gestureDetector;

    private float startX;

    List<Uri> myImageUriList = ContentsManager.getInstance().getUriListFromDirectory(ContentsManager.CARD_PHOTO_IMG);
    private boolean isFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photocard);

        backBtn = findViewById(R.id.backBtn);
        cardFrontBtn = findViewById(R.id.cardFontBtn);
        cardBackBtn = findViewById(R.id.cardBackBtn);
        cardImg = findViewById(R.id.cardImg);
        cardView = findViewById(R.id.photoCard);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        photoCountText = findViewById(R.id.photoCountText);

        photoCountText.setText(String.valueOf(RBW.album_photo + 1) + " / " + myImageUriList.size());
        cardFrontBtn.setTextColor(getApplication().getResources().getColor(R.color.white));
        cardBackBtn.setTextColor(getApplication().getResources().getColor(R.color.photocard_btn_gray));

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) cardView.getLayoutParams();
        int cardViewWidth = RBW.deviceWidth - (layoutParams.leftMargin + layoutParams.rightMargin);
        layoutParams.height = (int) Math.round(cardViewWidth * 1.55); //가로 = 세로
        cardView.setLayoutParams(layoutParams);
        Glide.with(this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);

        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureListener());

        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureListener());

        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = Math.abs(endX - startX);

                        // 일정 거리 이내의 스와이프만 클릭으로 간주
                        if (deltaX < 50) {
                            // 클릭 처리 코드 추가
                            handleCardClick();
                        }
                        break;
                }

                return true;
            }
        });

        // 뒤로가기 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down); // slide down 애니메이션
            }
        });


        //이전 버튼 클릭이벤트
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RBW.album_photo--;
                if (RBW.album_photo < 0) {
                    RBW.album_photo = myImageUriList.size() - 1;
                }

                photoCountText.setText(String.valueOf(RBW.album_photo + 1) + " / " + myImageUriList.size());

                // 포토카드 앞면일 경우 Check(Front일 경우) 뒷면일 경우 그대로 둔다.
                if (isFront) {
                    Glide.with(PhotoCardActivity.this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);
                }
                cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스케일 타입 CenterCrop
            }
        });

        //다음 버튼 클릭이벤트
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RBW.album_photo++;
                if (RBW.album_photo >= myImageUriList.size()) {
                    RBW.album_photo = 0;
                }

                photoCountText.setText(String.valueOf(RBW.album_photo + 1) + " / " + myImageUriList.size());

                // 포토카드 앞면일 경우 Check(Front일 경우) 뒷면일 경우 그대로 둔다.
                if (isFront) {
                    Glide.with(PhotoCardActivity.this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);
                }

                cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스케일 타입 CenterCrop
            }
        });

        // Front(카드) 버튼
        cardFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFront) {
                    showCardFront();
                }
            }
        });

        // Back(카드) 버튼
        cardBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFront) {
                    showCardBack();
                }
            }
        });

//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isFront) {
//                    showCardBack();
//                } else {
//                    showCardFront();
//                }
//            }
//        });
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                // 스와이프가 가로 방향으로 발생하면 이곳에서 처리
                if (diffX > 0) {
                    // 오른쪽 스와이프
                    RBW.album_photo--;
                    if (RBW.album_photo < 0) {
                        RBW.album_photo = myImageUriList.size() - 1;
                    }

                    photoCountText.setText(String.valueOf(RBW.album_photo + 1) + " / " + myImageUriList.size());

                    // 포토카드 앞면일 경우 Check(Front일 경우) 뒷면일 경우 그대로 둔다.
                    if (isFront) {
                        Glide.with(PhotoCardActivity.this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);
                    }
                    cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스케일 타입 CenterCrop
                } else {
                    // 왼쪽 스와이프
                    RBW.album_photo++;
                    if (RBW.album_photo >= myImageUriList.size()) {
                        RBW.album_photo = 0;
                    }

                    photoCountText.setText(String.valueOf(RBW.album_photo + 1) + " / " + myImageUriList.size());
                    // 포토카드 앞면일 경우 Check(Front일 경우) 뒷면일 경우 그대로 둔다.
                    if (isFront) {
                        Glide.with(PhotoCardActivity.this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);
                    }
                    cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스케일 타입 CenterCrop
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void handleCardClick() {
        if (isFront) {
            showCardBack();
        } else {
            showCardFront();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RBW.Activity_screenshot = "AlbumActivity";
    }

    private void showCardBack() {
        isFront = false;
        cardFrontBtn.setTextColor(getApplication().getResources().getColor(R.color.photocard_btn_gray));
        cardBackBtn.setTextColor(getApplication().getResources().getColor(R.color.white));


        cardImg.setRotationY(0f);
        cardImg.animate().rotationY(90f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Glide.with(PhotoCardActivity.this)
                        .load(ContentsManager.getInstance().getPhotoImgBack())
                        .into(cardImg);
                cardImg.setRotationY(270f);
                cardImg.animate().rotationY(360f).setListener(null);
                cardImg.setScaleType(ImageView.ScaleType.FIT_XY); // 스케일 타입 FitXY
            }
        });

    }

    private void showCardFront() {
        isFront = true;
        cardFrontBtn.setTextColor(getApplication().getResources().getColor(R.color.white));
        cardBackBtn.setTextColor(getApplication().getResources().getColor(R.color.photocard_btn_gray));


        cardImg.setRotationY(360f);
        cardImg.animate().rotationY(270f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Glide.with(PhotoCardActivity.this).load(myImageUriList.get(RBW.album_photo)).into(cardImg);
                cardImg.setRotationY(90f);
                cardImg.animate().rotationY(0f).setListener(null);
                cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스케일 타입 CenterCrop
            }
        });

    }

}