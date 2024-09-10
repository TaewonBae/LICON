package com.omotion.contentsx.android.licon.data.remote.model;

import android.os.AsyncTask;
import android.util.Log;

import com.omotion.contentsx.android.licon.core.RBW;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class HanteoChart {

   private static final String authorizationCode = "NjQ4MDAzMzE0OmFdVGJQc2BjfjNDSw==";
   private static final String tokenURL = "https://api.hanteo.io/oauth/token?grant_type=client_credentials";
   private static final String apiURL = "https://api.hanteo.io/v4/collect/realtimedata/ALBUM";

   private static final String TAG = "HanteoChart";

   private static String AccessToken = "";

   public void requestToken() {
      new TokenRequestTask().execute(tokenURL);
   }

   private class TokenRequestTask extends AsyncTask<String, Void, String> {

      @Override
      protected String doInBackground(String... urls) {
         try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Basic " + authorizationCode);
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf8");

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               // InputStream을 사용하여 응답을 읽어오는 코드를 여기에 추가
               InputStream inputStream = urlConnection.getInputStream();
               BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
               StringBuilder response = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) {
                  response.append(line);
               }
               reader.close();
               inputStream.close();

               // 전체 응답을 JSON으로 파싱
               JSONObject jsonResponse = new JSONObject(response.toString());
               // resultData 객체에서 access_token 추출
               String accessToken = jsonResponse.getJSONObject("resultData").getString("access_token");
               Log.d(TAG, "Access Token: " + accessToken);
               AccessToken = accessToken;

            } else {
               // 에러 처리 코드를 여기에 추가
            }
         } catch (Exception e) {
            Log.e(TAG, "Error during token request", e);
         }
         return null;
      }

      @Override
      protected void onPostExecute(String result) {
         makeAPICall(AccessToken);
      }
   }

   private void makeAPICall(String accessToken) {
      new APICallTask().execute(apiURL, accessToken);
   }

   private class APICallTask extends AsyncTask<String, Void, String> {

      @Override
      protected String doInBackground(String... params) {
         try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + params[1]);
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf8");

            JSONArray requestDataArray = createRequestDataArray();
            byte[] postDataBytes = requestDataArray.toString().getBytes("UTF-8");
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               InputStream inputStream = urlConnection.getInputStream();
               BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
               StringBuilder response = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) {
                  response.append(line);
               }
               reader.close();
               inputStream.close();

               // 응답 데이터는 response.toString()에 저장됨
               Log.d(TAG, "Response: " + response.toString());

            } else {
               // 에러 처리 코드를 여기에 추가
            }
         } catch (Exception e) {
            Log.e(TAG, "Error during API call", e);
         }
         return null;
      }

      private JSONArray createRequestDataArray() throws JSONException {
         JSONArray dataArray = new JSONArray();

         // 여러 개의 앨범 데이터를 추가하려면 반복문 등을 사용하여 JSONArray에 추가
         JSONObject albumData1 = createRequestData();
         dataArray.put(albumData1);

         return dataArray;
      }


      private JSONObject createRequestData() throws JSONException {
         JSONObject albumData = new JSONObject();
         albumData.put("familyCode", "HF0082COX001");
         albumData.put("branchCode", "001");
         albumData.put("barcode", "8809325061848");
         albumData.put("albumName", "Album_GIUK");
         albumData.put("realTime", getCurrentKSTUnixTimestamp());
         albumData.put("opVal", RBW.SerialKey);
         //albumData.put("lng", "111");
         //albumData.put("lat", "222");
         albumData.put("ip", getDeviceIPAddress());

         return albumData;
      }

      @Override
      protected void onPostExecute(String result) {
         // API 호출 이후의 작업을 수행하는 코드를 여기에 추가
      }
   }

   private int getCurrentKSTUnixTimestamp() {
      return (int) (System.currentTimeMillis() / 1000);
   }

   private String getDeviceIPAddress() {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
         while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
               InetAddress address = addresses.nextElement();
               if (!address.isLoopbackAddress()) {
                  // IPv4 또는 IPv6 주소 모두 처리
                  String ipAddress = address.getHostAddress();
                  int percentIndex = ipAddress.indexOf('%');
                  if (percentIndex != -1) {
                     // % 이후의 부분을 제외
                     ipAddress = ipAddress.substring(0, percentIndex);
                  }

                  ipAddress = normalizeIPv6Address(ipAddress);

                  return ipAddress;
               }
            }
         }
      } catch (Exception e) {
         Log.e("MainActivity", "Error getting IP address: " + e.getMessage());
      }
      return null;
   }

   private static String normalizeIPv6Address(String compressedIPv6Address) {
      String[] blocks = compressedIPv6Address.split(":");
      StringBuilder normalizedAddress = new StringBuilder();

      for (int i = 0; i < blocks.length; i++) {
         // 빈 블록은 생략
         if (!blocks[i].isEmpty()) {
            // 각 블록에서 앞에 있는 0을 채우기
            String block = String.format("%04x", Integer.parseInt(blocks[i], 16));

            if (normalizedAddress.length() > 0) {
               normalizedAddress.append(":");
            }
            normalizedAddress.append(block);
         }
      }

      return normalizedAddress.toString();
   }
}