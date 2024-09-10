package com.omotion.contentsx.android.licon.ui.alert.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.data.remote.model.AlertVO;
import com.omotion.contentsx.android.licon.ui.alert.adapter.AlertExpListAdapter;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertChildItem;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertParentItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFAQFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFAQFragment extends Fragment {
    private final String TAG = this.getClass().getName();
    ExpandableListView explistView;
    ArrayList<ArrayList<AlertChildItem>> childList = new ArrayList<>(); //자식 리스트
    ArrayList<AlertVO> faqList = new ArrayList<>();
    int lastClickedPosition = 0;

    public static AlertFAQFragment newInstance(String param1, String param2) {
        AlertFAQFragment fragment = new AlertFAQFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alert1, container, false);

        explistView = v.findViewById(R.id.exp_list_view);
        getFaqList();

        return v;
    }

    public void getFaqList() {
        LLog.i(TAG, "getFaqList", "getFaqList call");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("database/v1/FAQ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LLog.d(TAG, "onComplete", document.getId() + " => " + document.getData());
                                faqList.add(document.toObject(AlertVO.class));
                            }
                        } else {
                            LLog.w(TAG, "onComplete", "Error => " + task.getException());
                        }
                        bindList();
                    }
                });
    }

    private void bindList() {
        //monthArray에 1월~12월 배열을 모두 추가
        for (int i = 0; i < faqList.size(); i++) {
            childList.add(new ArrayList<AlertChildItem>());
        }
        AlertExpListAdapter adapter = new AlertExpListAdapter();

        adapter.childItems = childList;

        for (int i = 0; i < childList.size(); i++) {
            adapter.addItem(new AlertParentItem("FAQ", faqList.get(i).getTitle()));
            adapter.addItem(i, new AlertChildItem(faqList.get(i).getSubject()));
            Log.d(faqList.get(i).getSubject(),"faq");
        }

        explistView.setAdapter(adapter);

        if (RBW.customerService) {
            // 이전에 열려있던 그룹 닫기
            explistView.collapseGroup(lastClickedPosition);

            // 첫 번째 그룹(셀)을 엽니다.
            int firstGroupPosition = 1; // 첫 번째 그룹의 위치 (1번째 셀)
            explistView.expandGroup(firstGroupPosition);

            lastClickedPosition = firstGroupPosition; // 마지막으로 열린 셀 할당
        }


        //부모 view 클릭이벤트
        explistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                // 선택 한 groupPosition의 펼침/닫힘 상태 체크
                Boolean isExpand = (!explistView.isGroupExpanded(groupPosition));

                // 이 전에 열려있던 group 닫기
                explistView.collapseGroup(lastClickedPosition);

                if (isExpand) {
                    explistView.expandGroup(groupPosition);
                }
                lastClickedPosition = groupPosition;


                return true;
            }
        });



        explistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //child list 클릭이벤트
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });
    }
}
