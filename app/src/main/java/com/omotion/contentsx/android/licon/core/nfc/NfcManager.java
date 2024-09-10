package com.omotion.contentsx.android.licon.core.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.SharedPrefManager;
import com.omotion.contentsx.android.licon.core.util.Utils;
import com.omotion.contentsx.android.licon.data.remote.model.HanteoChart;
import com.omotion.contentsx.android.licon.ui.widget.NfcInformDialog;

import java.io.UnsupportedEncodingException;

public class NfcManager {

    private HanteoChart hanteoChart;

    private final String TAG = this.getClass().getName();
    private static NfcManager instance = null;
    private NfcManagerListener listener = null;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNfcTechLists;
    private FirebaseFirestore db;
    private Activity activity;
    private NfcInformDialog dialog;

    private QueryDocumentSnapshot document_;

    public enum RegistResult {
        SUCCESS, NO_REGISTERED, DUPLICATED, ERROR
    }

    public enum NfcStatus {
        DISABLE, ACTIVE, INACTIVE
    }

    public static NfcManager getInstance() {
        if (instance == null) {
            instance = new NfcManager();
        }
        return instance;
    }

    public void init(Activity context) {
        this.activity = context;
        db = FirebaseFirestore.getInstance();
        setEventReceiverActivity();
    }

    public void addListener(NfcManagerListener listener) {
        this.listener = listener;
    }


    public void setEventReceiverActivity() {
        LLog.i(TAG, "setEventReceiverActivity", "call");
        // nfc 이벤트 발생 시 실행, 현재 activty에서 실행
        Intent intent = new Intent(activity, activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE);

        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            // 모든 MIME 유형의 NDEF 데이터 수락
            ndefIntent.addDataType("*/*");
        } catch (Exception e) {
            LLog.e(TAG, "setEventReceiverActivity", "TagDispatch " + e.toString());
        }

        mIntentFilters = new IntentFilter[]{ndefIntent,};
        mNfcTechLists = new String[][]{new String[]{NfcF.class.getName()}};
    }

    public void onNewIntent(Intent intent) {
        LLog.d(TAG, "onNewIntent", "called!");
        if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            readNdefMessage(intent);
        }
    }

    public void onPause() {
        disableNfcTag();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void readNdefMessage(Intent passedIntent) {
        Parcelable[] rawMessages = passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages == null) {
            LLog.d(TAG, "readNdefMessage", "NDEF is null.");
            return;
        }
        LLog.d(TAG, "readNdefMessage", "rawMsgs.length" + rawMessages.length);

        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }

            for (NdefMessage message : messages) {
                NdefRecord[] records = message.getRecords();
                for (NdefRecord record : records) {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;
                    String nfcResultString;
                    try {
                        nfcResultString = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                        String hexStringKey = Utils.stringToHex(nfcResultString);
                        String hexStringCode = Utils.stringToHex("5_motion_platform_A");
                        String targetString = Utils.hexXOR(hexStringKey, hexStringCode);
                        callRegistrationApi(targetString);
                        break;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        listener.onNfcPassResult(RegistResult.ERROR);
                    }
                }
            }
        }
    }

    private void disableNfcTag() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(activity);
            mNfcAdapter = null;
        }
    }

    public void checkRegistration() {
        String targetString = SharedPrefManager.getData(SharedPrefManager.SERIAL_KEY, "");

        if (!targetString.isEmpty() && targetString != null) {
            callRegistrationApi(targetString);
        } else {
            // NFC 상태 확인
            switch (getNfcState()) {
                case DISABLE: // NFC 지원하지 않는 기기인 경우
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.nfc_no_device), Toast.LENGTH_SHORT).show();
                    break;
                case INACTIVE: // NFC 비활성 상태인 경우
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    } else {
                        intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    }
                    activity.startActivity(intent);
                    break;
                case ACTIVE: // NFC 활성 상태인 경우
                    // NFC 활성화
                    mNfcAdapter = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
                    if (mNfcAdapter != null) {
                        mNfcAdapter.enableForegroundDispatch((Activity) activity, mPendingIntent, mIntentFilters, mNfcTechLists);
                    }
                    dialog = new NfcInformDialog(activity);
                    dialog.setOnCancelBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            disableNfcTag();
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            disableNfcTag();
                        }
                    });
                    dialog.show();
                    break;
            }
        }
    }

    public void callRegistrationApi(String targetString) {
        LLog.i(TAG, "callRegistrationApi", "targetString ::: " + targetString);
        if (!targetString.isEmpty() && targetString != null) {
            RBW.SerialKey = targetString;

            CollectionReference collectionReference = db.collection("database/v1/Registration");

            collectionReference.whereEqualTo("Encoding_Code", targetString) // targetString, EncodingCode 비교
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        LLog.i(TAG, "callRegistrationApi", "onSuccess :: " + queryDocumentSnapshots.isEmpty());
                        //일치하는 targetString이 없음
                        if (queryDocumentSnapshots.isEmpty()) {
                            listener.onNfcPassResult(RegistResult.NO_REGISTERED);
                        }
                    })
                    .addOnCompleteListener(task -> handleRegistrationResult(task, targetString))
                    .addOnFailureListener(e -> LLog.w(TAG, "callRegistrationApi", "Error => " + e));
        }
    }

    public void RegistrationTrue()
    {
        LLog.i(TAG, "handleRegistrationResult", "document ::: 값 " + document_.getData());
        Boolean registrationValue = document_.getBoolean("Registration");
        if (registrationValue != null && !registrationValue) {
            ContentsManager.getInstance().processCheckDownload();
            document_.getReference().update("Registration", true)
                    .addOnSuccessListener(aVoid -> {
                        hanteoChart = new HanteoChart();
                        hanteoChart.requestToken();
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

    private void handleRegistrationResult(Task<QuerySnapshot> task, String targetString) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                LLog.i(TAG, "handleRegistrationResult", "document ::: 값 " + document.getData());
                Boolean registrationValue = document.getBoolean("Registration");
                if (registrationValue != null && !registrationValue) {
                    document_ = document;
                    ContentsManager.getInstance().processCheckDownload();
//                    document.getReference().update("Registration", true)
//                            .addOnSuccessListener(aVoid -> {
//                                LLog.i(TAG, "handleRegistrationResult", "Registration 값 true 업데이트 성공");
//                                SharedPrefManager.setData(SharedPrefManager.SERIAL_KEY, targetString);
//                                listener.onNfcPassResult(RegistResult.SUCCESS);
////                                ContentsManager.getInstance().processCheckDownload();
//                            })
//                            .addOnFailureListener(e -> {
//                                LLog.i(TAG, "handleRegistrationResult", "Registration 값 true 업데이트 실패");
//                                Toast.makeText(activity.getApplicationContext(),  activity.getResources().getString(R.string.dialog_fail), Toast.LENGTH_SHORT).show();
//                                listener.onNfcPassResult(RegistResult.ERROR);
//                                //실패 시, 저장된 serial key 제거
//                                SharedPrefManager.setData(SharedPrefManager.SERIAL_KEY, "");
//                            });
                } else {
                    LLog.i(TAG, "handleRegistrationResult", "이미 등록됨");
                    SharedPrefManager.setData(SharedPrefManager.SERIAL_KEY, "");
                    listener.onNfcPassResult(RegistResult.DUPLICATED);
                }
            }
        } else {
            LLog.w(TAG, "handleRegistrationResult", "Error => " + task.getException());
            listener.onNfcPassResult(RegistResult.ERROR);
            //실패 시, 저장된 serial key 제거
            SharedPrefManager.setData(SharedPrefManager.SERIAL_KEY, "");
        }
    }

    // 단말기 Nfc 상태 점검
    private NfcStatus getNfcState() {
        try {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
            if (adapter == null) { // NFC를 지원하지 않는 기기인지 확인
                return NfcStatus.DISABLE;
            } else { // NFC가 켜져있는지 확인
                if (adapter.isEnabled() == true) {
                    return NfcStatus.ACTIVE;
                } else {
                    return NfcStatus.INACTIVE;
                }
            }
        } catch (Exception e) {
            return NfcStatus.DISABLE;
        }
    }
}
