package com.omotion.contentsx.android.licon.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.omotion.contentsx.android.licon.R;

public class NfcInformDialog extends BottomSheetDialog {
    public NfcInformDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_nfc_inform);
        setCancelable(true);
    }

    public void setOnCancelBtnListener(View.OnClickListener listener) {
        findViewById(R.id.btn_cancel).setOnClickListener(listener);
    }

    public void setErrorInformView() {
        findViewById(R.id.tv_title).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.iv_status)).setImageResource(R.drawable.nfc_check);
        ((TextView) findViewById(R.id.tv_content)).setText(R.string.nfc_fail);
        findViewById(R.id.btn_cancel).setVisibility(View.INVISIBLE);
    }

}
