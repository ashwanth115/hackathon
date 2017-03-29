package com.pushparaj.googlemaps;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class checkNotification extends Service {
    String qr;
    public checkNotification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Intent i = new Intent(this,checkNotification.class);
        i.putExtra("qr",qr);
        startService(i);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        qr=intent.getExtras().getString("qr");
        Background bk = new Background(qr);
        bk.execute("postdata");
        return START_STICKY;
    }
    class Background extends AsyncTask<String,Void,NotificationRows[]> {
        String qr,txt;

        Background() {
        }

        Background(String qr) {
            this.qr = qr;
        }

        public Background(String qr, String txt) {
            this.qr = qr;
            this.txt = txt;
        }

        @Override
        protected NotificationRows[] doInBackground(String... params) {
            if (params[0].equals("postdata")) {
                URL url = null;
                try {
                    url = new URL("https://pushparajkvp.000webhostapp.com/checkNotification.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String post = URLEncoder.encode("qr", "UTF-8") + "=" + URLEncoder.encode(qr, "UTF-8");
                    bw.write(post);
                    bw.flush();
                    bw.close();
                    os.close();
                    int response = httpURLConnection.getResponseCode();
                    StringBuilder res = new StringBuilder();
                    if (response == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            res.append(line);
                        }
                        br.close();
                    }
                    JSONObject ob = new JSONObject(res.toString());
                    JSONArray ar = ob.getJSONArray("noti");
                    int count = 0;
                    NotificationRows[] rw = new NotificationRows[ar.length()];
                    while(count < ar.length()) {
                        rw[count] = new NotificationRows();
                        JSONObject obs = ar.getJSONObject(count);
                        rw[count].setText(obs.getString("texts"));
                        rw[count].setTitle(obs.getString("titles"));
                        count++;
                    }
                    httpURLConnection.disconnect();
                    return rw;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else if (params[0].equals("delete")) {
                URL url = null;
                try {
                    url = new URL("https://pushparajkvp.000webhostapp.com/deleteNoti.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String post = URLEncoder.encode("qr", "UTF-8") + "=" + URLEncoder.encode(qr, "UTF-8") + "&"
                            + URLEncoder.encode("txt", "UTF-8") + "=" + URLEncoder.encode(txt, "UTF-8");
                    Log.i("push", post);
                    bw.write(post);
                    bw.flush();
                    bw.close();
                    os.close();
                    int response = httpURLConnection.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder res = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            res.append(line);
                        }
                        br.close();
                    }
                    httpURLConnection.disconnect();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(NotificationRows[] notificationRowses) {
            if(notificationRowses != null) {
                if (notificationRowses.length > 0) {
                    for (int i = 0; i < notificationRowses.length; i++) {
                        showNoti(notificationRowses[i]);
                        Background bk = new Background(qr, notificationRowses[i].getText());
                        bk.execute("delete");
                    }
                }
            }
        }
    }

    void showNoti(NotificationRows notificationRows){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.gate);
        builder.setContentTitle(notificationRows.getTitle());
        builder.setContentText(notificationRows.getText());
        Intent i = new Intent(this,MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(i);
        PendingIntent in = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(in);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0,builder.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
