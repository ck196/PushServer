package com.kju.push;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class PushSender {

        private static String GcmUrl = "https://android.googleapis.com/gcm/send";
        private static String GcmRegId="APA91bF6nE-3XLLVGAHtJlAAPQkH8w7U-gy4k7TzjhmJ5i4clr5TXtowaj7aOoX9vmfE7eRKRd-zu51wBpbAfZgVc-wm2BcGf8ChPUfMn-p-FMVNrkHUjy36kcX0gkVX_CgpP_P2my3z7EIBnmMwHcQanpHrgpJYFg";
        private static String APP_KEY = "AIzaSyBiYEl6CdiY0Vp26zAQ5yaoRufF-sPcL7s";        
        
        static String msg = "test";
        
        public static void main(String[] args) {
                try {
                        senders(GcmRegId, APP_KEY, msg);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void senders(String regid, String authToken, String msg) throws Exception {
                StringBuffer postDataBuilder = new StringBuffer();
                postDataBuilder.append("registration_id=" + regid);
                postDataBuilder.append("&collapse_key=1");
                postDataBuilder.append("&delay_white_idle=1");
                postDataBuilder.append("&data.msg=" + URLEncoder.encode(msg, "UTF-8"));

                byte[] postData = postDataBuilder.toString().getBytes("UTF-8");
                URL url = new URL(GcmUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                HttpsURLConnection.setDefaultHostnameVerifier(new FakeHoswnameVerifier());
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                conn.setRequestProperty("Authorization", "key=" + APP_KEY);
                OutputStream out = conn.getOutputStream();
                out.write(postData);
                out.close();
                conn.getInputStream();

                System.out.println("postData : " + postDataBuilder.toString());
                String reponseLine = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                                .readLine();

                System.out.println("reponseLine : " + reponseLine);

        }

        private static class FakeHoswnameVerifier implements HostnameVerifier {
                public boolean verify(String hostname, SSLSession session) {
                        return true;
                }
        }
}