package com.omotion.contentsx.android.licon.ui.alert.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.data.remote.model.AlertVO;
import com.omotion.contentsx.android.licon.ui.alert.adapter.AlertExpListAdapter2;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertChildItem2;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertParentItem2;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertNoticeFragment} factory method to
 * create an instance of this fragment.
 */
public class AlertNoticeFragment extends Fragment {
    private final String TAG = this.getClass().getName();
    ExpandableListView explistView;
    ArrayList<ArrayList<AlertChildItem2>> childList = new ArrayList<>(); //자식 리스트
    ArrayList<AlertVO> noticeList = new ArrayList<>();
    int lastClickedPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alert2, container, false);

        explistView = v.findViewById(R.id.exp_list_view2);
        getNoticeList();

        return v;
    }

    public void getNoticeList() {
        LLog.i(TAG, "getNoticeList", "getNoticeList call");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("database/v1/notice")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LLog.d(TAG, "onComplete", "toObject ::: => " + document.toObject(AlertVO.class));
                                noticeList.add(document.toObject(AlertVO.class));
                            }
                        } else {
                            LLog.w(TAG, "onComplete", "Error => " + task.getException());
                        }
                        bindList();
                    }
                });
    }

    private void bindList() {
        AlertExpListAdapter2 adapter = new AlertExpListAdapter2();

        for (int i = 0; i < noticeList.size(); i++) {
            childList.add(new ArrayList<AlertChildItem2>());
        }
        adapter.childItems = childList;

        for (int i = 0; i < childList.size(); i++) {
            adapter.addItem(new AlertParentItem2("알림", noticeList.get(i).getTitle()));
            adapter.addItem(i, new AlertChildItem2(noticeList.get(i).getSubject()));
        }

        explistView.setAdapter(adapter);
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

