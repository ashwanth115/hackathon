package com.pushparaj.googlemaps;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class shopKeeperPage extends AppCompatActivity {
    EditText editText;
    int id;
    TextView welcome,current;
    String qr,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_keeper_page);
        editText = (EditText)findViewById(R.id.editnewoffer);
        welcome = (TextView) findViewById(R.id.txtshopkeeper);
        current = (TextView)findViewById(R.id.txtcuroffer);
        Bundle i = getIntent().getExtras();
        qr = i.getString("qr");
        name = i.getString("name");
        welcome.setText("Welcome "+name);
        backshop bk = new backshop();
        bk.execute();
    }

    public void submit(View view) {
        if(editText.getText().toString().equals("")){
            return;
        }else {
            backshopsubmit bk = new backshopsubmit(String.valueOf(id),editText.getText().toString());
            bk.execute();
        }
    }
    class backshop extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
                String offer="";
                try {
                    URL url = new URL("https://pushparajkvp.000webhostapp.com/getShop.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    String post = URLEncoder.encode("qr","UTF-8")+"="+URLEncoder.encode(qr,"UTF-8");
                    bw.write(post);
                    bw.flush();
                    bw.close();
                    os.close();
                    int response = httpURLConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder res = new StringBuilder();
                        String line;
                        while((line = br.readLine()) != null){
                            res.append(line);
                        }
                        br.close();
                        JSONObject ob = new JSONObject(res.toString());
                        Log.i("push",res.toString());
                        JSONArray ar = ob.getJSONArray("shop");
                        int count = 0;
                        while(count < ar.length()) {
                            JSONObject obs = ar.getJSONObject(count);
                            offer = obs.getString("offer");
                            id = obs.getInt("id");
                            count++;
                        }
                    }
                    httpURLConnection.disconnect();
                    return offer;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            return  "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if(aVoid.equals("")){
                current.setText("No Offer Update");
            }else
            current.setText(aVoid);
        }
    }
    class backshopsubmit extends AsyncTask<Void,Void,String>{
        String id,offer;

        public backshopsubmit(String id, String offer) {
            this.id = id;
            this.offer = offer;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://pushparajkvp.000webhostapp.com/updateShop.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(2000);
                httpURLConnection.setRequestMethod("POST");

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String post = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"+
                        URLEncoder.encode("offer","UTF-8")+"="+URLEncoder.encode(offer,"UTF-8");
                bw.write(post);
                bw.flush();
                bw.close();
                os.close();
                int response = httpURLConnection.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder res = new StringBuilder();
                    String line;
                    while((line = br.readLine()) != null){
                        res.append(line);
                    }
                    br.close();
                }
                httpURLConnection.disconnect();
                return offer;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  offer;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if(aVoid.equals("")){
                current.setText("No Offer Update");
            }else
                current.setText(aVoid);
        }
    }
}
