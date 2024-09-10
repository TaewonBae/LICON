package com.omotion.contentsx.android.licon.data.remote.model;

import android.util.Log;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;


@Keep
public class AlertVO {
    @PropertyName("Title")
    private String title;

    @PropertyName("Subject")
    private String subject;

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        // Java 특성상 텍스트뷰에 \n으로 출력이 될경우 실제 데이터는 \\\\n 이므로 이것들을 \n으로 변경해주는 작업
        String formatted = subject.replaceAll("\\\\n", "\n");
        Log.d("faq", "Formatted subject: " + formatted);
        return formatted;
    }

    @Override
    public String toString() {
//        return "AlertVO{" +
//                "title='" + title + '\'' +
//                ", subject='" + subject + '\'' +
//                '}';
        return "AlertVO{" +
                "title='" + title + '\'' +
                ", subject='" + getSubject() + '\'' +
                '}';
    }


}
