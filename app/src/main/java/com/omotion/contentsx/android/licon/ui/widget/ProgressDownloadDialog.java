package com.omotion.contentsx.android.licon.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.util.LLog;

public class ProgressDownloadDialog extends Dialog {
    TextView progressText, downloadText;
    public ProgressDownloadDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);
        setCancelable(false);

        downloadText = findViewById(R.id.downloadText);
        progressText = findViewById(R.id.progressText);
    }

    public void setDownloadText(String text) {
        downloadText.setText(text);
    }

    public void setProgressText(String text) {
        LLog.d("setProgressText", "onComplete", "text: " + text);
        progressText.setText(text);
    }

}
