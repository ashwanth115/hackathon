package com.pushparaj.googlemaps;

//import android.content.Intent;

//import android.content.Intent;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;

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


public class LoginScreen extends AppCompatActivity implements View.OnClickListener{
    EditText ed;
    Button b,b1;
    ProgressBar pb;
    public void dialog(String a)
    {
        new AlertDialog.Builder(this)
                .setTitle("alert")
                .setMessage(a)
                .create()
                .show();
    }
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ed=(EditText)findViewById(R.id.editText);
        b =(Button)findViewById(R.id.button);
        b.setOnClickListener(this);
        qrScan = new IntentIntegrator(this);
        b1=(Button)findViewById(R.id.login);
        pb = (ProgressBar)findViewById(R.id.prog);
        pb.setVisibility(View.INVISIBLE);
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s=ed.getText().toString();
                        if(s.equals(""))
                        {
                        dialog("Please Scan The Code");
                        }
                        else
                        {
                            background bk = new background(ed.getText().toString());
                            bk.execute("check");

                        }

                    }
                }
        );


    }
    public class background extends AsyncTask<String,Integer,UserTableRows[]> {
        String qr;
        background(String qr){
            this.qr=qr;
        }
        @Override
        protected UserTableRows[] doInBackground(String... params) {
            if(params[0].equals("check")){
                try {
                    publishProgress(0);
                    Log.i("push","inside check");
                    URL url = new URL("https://pushparajkvp.000webhostapp.com/loginCheck.php");
                    HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    String post = URLEncoder.encode("qr","UTF-8")+"="+ URLEncoder.encode(qr,"UTF-8");
                    br.write(post);
                    br.flush();
                    br.close();
                    os.close();
                    publishProgress(50);
                    int res = httpURLConnection.getResponseCode();
                    if(res == HttpURLConnection.HTTP_OK){
                        BufferedReader brs=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder ress = new StringBuilder();
                        String line;
                        while((line = brs.readLine()) != null){
                            ress.append(line);
                        }
                        br.close();
                        JSONObject ob = new JSONObject(ress.toString());
                        JSONArray ar = ob.getJSONArray("user");
                        int count = 0;
                        UserTableRows[] rw = new UserTableRows[ar.length()];
                        while(count < ar.length()) {
                            rw[count] = new UserTableRows();
                            JSONObject obs = ar.getJSONObject(count);
                            rw[count].setQr(obs.getString("qr"));
                            rw[count].setAge(obs.getInt("age"));
                            rw[count].setSex(obs.getInt("sex"));
                            rw[count].setName(obs.getString("name"));
                            rw[count].setBill(obs.getInt("bill"));
                            rw[count].setUsertype(obs.getInt("usertype"));
                            rw[count].setPass(obs.getString("pass"));
                            rw[count].setAirport(obs.getString("airport"));
                            count++;
                        }
                        Log.i("push","inside response");
                        publishProgress(100);
                        return rw;
                    }
                    httpURLConnection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserTableRows[] params) {
            if(params != null) {
                if (params.length != 0) {
                    openNext(params[0]);
                } else {
                    dialog("The Scanned Code Is Invalid");
                    pb.setVisibility(View.INVISIBLE);
                    b.setVisibility(View.VISIBLE);
                    b1.setVisibility(View.VISIBLE);
                }
            }else{
                dialog("Please Check Your Internet Connection");
                LoginScreen.this.pb.setVisibility(View.INVISIBLE);
                LoginScreen.this.b.setVisibility(View.VISIBLE);
                LoginScreen.this.b1.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            loginClicked(values[0]);
        }
    }


    void openNext(UserTableRows rw){
        b.setVisibility(View.VISIBLE);
        b1.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
        int user = rw.getUsertype();
        if(user==0) {
            Toast.makeText(this,"User",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, UserPage.class);
            i.putExtra("qr", rw.getQr());
            i.putExtra("type", user);
            startActivity(i);
        }else if(user ==1){
            Toast.makeText(this,"Super User",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, superUserPage.class);
            i.putExtra("qr", rw.getQr());
            i.putExtra("type", user);
            i.putExtra("airport",rw.getAirport());
            startActivity(i);
        }else if(user == 2){
            Toast.makeText(this,"Shop Owner",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, shopKeeperPage.class);
            i.putExtra("qr", rw.getQr());
            i.putExtra("name", rw.getName());
            startActivity(i);
        }
    }
    void loginClicked(int values){
        pb.setVisibility(View.VISIBLE);
        pb.setProgress(values);
        b.setVisibility(View.INVISIBLE);
        b1.setVisibility(View.INVISIBLE);

    }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
       if (result != null) {
           //if qrcode has nothing in it
           if (result.getContents() == null) {
               Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
           } else {
               //if qr contains data
               try {
                   //converting the data to json
                   JSONObject obj = new JSONObject(result.getContents());
                   //setting values to textviews
                  // ed.setText(obj.getString("name"));

               } catch (JSONException e) {
                   e.printStackTrace();
                   //if control comes here
                   //that means the encoded format not matches
                   //in this case you can display whatever data is available on the qrcode
                   //to a toast
                   //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    ed.setText(result.getContents());
               }
           }
       } else {
           super.onActivityResult(requestCode, resultCode, data);
       }
   }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
