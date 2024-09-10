package com.omotion.contentsx.android.licon.core.util;

import android.content.Context;
import android.util.TypedValue;

import java.io.File;

public class Utils {
    public static String TAG = "Utils";

    public static int getDpToPixel(Context context, int DP) {
        float px = 0;

        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP,
                context.getResources().getDisplayMetrics());
        // 취약성 검사 : IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION 걸림
//        try {
//            px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP,
//                    context.getResources().getDisplayMetrics());
//        } catch (Exception e) {
//
//        }
        return (int) px;
    }

    // 문자열을 16진수 문자열로 변환
    public static String stringToHex(String string) {
        byte[] bytes = string.getBytes();
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }

    // 16진수 문자열 XOR 연산
    public static String hexXOR(String hexString1, String hexString2) {
        if (hexString1.length() != hexString2.length()) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexString1.length(); i += 2) {
            int value1 = Integer.parseInt(hexString1.substring(i, i + 2), 16);
            int value2 = Integer.parseInt(hexString2.substring(i, i + 2), 16);
            int xorResult = value1 ^ value2;
            result.append(String.format("%02X", xorResult));
        }

        return result.toString();
    }

    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles())
                deleteRecursive(child);
        } else {
            file.delete();
        }
    }

    public static long timeFormatToLong(String time) {
        if (time == null || time.isEmpty()) {
            return 0;
        }
        String timeDigits = time.replaceAll("[^0-9]", "");
        // 시, 분, 밀리초를 추출
        int minutes = Integer.parseInt(timeDigits.substring(0, 2));
        int seconds = Integer.parseInt(timeDigits.substring(2, 4));
        int milliseconds = Integer.parseInt(timeDigits.substring(4, 7));
        long value = milliseconds + (seconds * 1000) + (minutes * 1000 * 60);
        return value;
    }
}
