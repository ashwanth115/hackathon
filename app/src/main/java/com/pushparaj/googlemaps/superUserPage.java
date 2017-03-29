package com.pushparaj.googlemaps;

import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
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

@SuppressWarnings("deprecation")
public class superUserPage extends TabActivity {
    TabHost host;
    String airport,qr;
    EditText editsearch,editmess;
    TextView txtwelcome,txtname,txtflightno,txtfrom,txtto,txttime,txtticketno,txtclass,txtpurchase,txtcompany,txtbillno
            ,txtticketstatus,txtprice,txtgate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user_page);
        Bundle i = getIntent().getExtras();
        qr = i.getString("qr");
        int type = i.getInt("type");
        airport = i.getString("airport");
        Log.i("push",airport+"-----");
        setContentView(R.layout.activity_super_user_page);
        editmess=(EditText)findViewById(R.id.editmess);
        editsearch =(EditText)findViewById(R.id.txtbookid);
        txtwelcome = (TextView)findViewById(R.id.txtwelcome);
        txtgate = (TextView)findViewById(R.id.txtgates);
        txtname = (TextView)findViewById(R.id.txtnames);
        txtflightno = (TextView)findViewById(R.id.txtflightnos);
        txtfrom = (TextView)findViewById(R.id.txtfroms);
        txtto = (TextView)findViewById(R.id.txttos);
        txttime = (TextView)findViewById(R.id.txttimes);
        txtticketno = (TextView)findViewById(R.id.txtticketnos);
        txtclass = (TextView)findViewById(R.id.txtclasss);
        txtpurchase = (TextView)findViewById(R.id.txtpurchases);
        txtcompany = (TextView)findViewById(R.id.txtcompanys);
        txtbillno = (TextView)findViewById(R.id.txtbillnos);
        txtticketstatus = (TextView)findViewById(R.id.txtstatuss);
        txtprice = (TextView)findViewById(R.id.txtprices);
        host= getTabHost();

        backgroundchats bk = new backgroundchats(qr);
        bk.execute();

        TabHost.TabSpec spec1 = host.newTabSpec("Details");
        spec1.setIndicator("Details");
        spec1.setContent(R.id.Details);
        host.addTab(spec1);


        TabHost.TabSpec spec2 = host.newTabSpec("Chats");
        spec2.setIndicator("Chats");
        spec2.setContent(R.id.Chats);
        host.addTab(spec2);

        TabHost.TabSpec spec3 = host.newTabSpec("Map");
        spec3.setIndicator("Map");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",type);
        spec3.setContent(intent);
        host.addTab(spec3);

        Intent is = new Intent(this,checkNotification.class);
        is.putExtra("qr",qr);
        startService(is);

    }

    public void onSearch(View view) {
        if(!editsearch.getText().toString().equals("")) {
            BackgroundPassenger bk = new BackgroundPassenger(editsearch.getText().toString());
            bk.execute();
        }else {
            Toast.makeText(this,"Enter The booking ID",Toast.LENGTH_LONG).show();
        }
        
    }

    public void sendClicked(View view) {
        if(editmess.getText().toString().equals("")){
            return;
        }else {
            backinsert bk = new backinsert(airport,editmess.getText().toString(),qr);
            bk.execute();
            editmess.setText("");
        }
    }

    class backinsert extends AsyncTask<Void,Void,Integer>{
        String qr,mess,airport;

        public backinsert(String airport, String mess, String qr) {
            this.airport = airport;
            this.mess = mess;
            this.qr = qr;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://pushparajkvp.000webhostapp.com/insertChat.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    String post = URLEncoder.encode("fromqr","UTF-8")+"="+URLEncoder.encode(qr,"UTF-8")+"&"+
                                URLEncoder.encode("mess","UTF-8")+"="+URLEncoder.encode(mess,"UTF-8")+"&"+
                                URLEncoder.encode("airport","UTF-8")+"="+URLEncoder.encode(airport,"UTF-8");
                    Log.i("push",post);
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
                        Log.i("push",res.toString());
                    }
                    httpURLConnection.disconnect();
                    return 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            backgroundchats bk = new backgroundchats(qr);
            bk.execute();
        }
    }

    class BackgroundPassenger extends AsyncTask<String,Void,PassengerDetails[]> {
        String jString,qr;


        BackgroundPassenger() {
        }

        public BackgroundPassenger(String qr) {
            this.qr = qr;
        }


        @Override
        protected void onPostExecute(PassengerDetails[] passengerDetailses) {
            if(passengerDetailses!=null){
                if(passengerDetailses.length>0) {
                    String welcomeString = "Welcome to " + passengerDetailses[0].getFromplace() + " airport";
                    txtwelcome.setText(welcomeString);
                    txtname.setText(passengerDetailses[0].getName());
                    txtflightno.setText(passengerDetailses[0].getAirno());
                    txtfrom.setText(passengerDetailses[0].getFromplace());
                    txtto.setText(passengerDetailses[0].getToplace());
                    txttime.setText(passengerDetailses[0].getDeparture());
                    txtticketno.setText(passengerDetailses[0].getTicketno());
                    txtclass.setText(passengerDetailses[0].getClasses());
                    txtpurchase.setText(passengerDetailses[0].getPurchase());
                    txtcompany.setText(passengerDetailses[0].getAirname());
                    txtbillno.setText(String.valueOf(passengerDetailses[0].getBillnumber()));
                    txtticketstatus.setText(String.valueOf(passengerDetailses[0].getTicketstatus()));
                    txtprice.setText(String.valueOf(passengerDetailses[0].getPrice()));
                    txtgate.setText(passengerDetailses[0].getGate());
                }else{
                    Toast.makeText(superUserPage.this,"The booking id is invalid or check internet",Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected PassengerDetails[] doInBackground(String... params) {
            try {
                URL url = new URL("https://pushparajkvp.000webhostapp.com/searchBill.php");
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
                if(response == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder res = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        res.append(line);
                    }
                    br.close();
                    jString = res.toString().trim();

                    JSONObject ob = new JSONObject(jString);
                    Log.i("push",jString);
                    JSONArray ar = ob.getJSONArray("bills");
                    int count = 0;
                    PassengerDetails[] rw = new PassengerDetails[ar.length()];
                    while (count < ar.length()) {
                        rw[count] = new PassengerDetails();
                        JSONObject obs = ar.getJSONObject(count);
                        rw[count].setAirno(obs.getString("airno"));
                        rw[count].setGate(obs.getString("gate"));
                        rw[count].setFromplace(obs.getString("fromplace"));
                        rw[count].setToplace(obs.getString("toplace"));
                        rw[count].setClasses(obs.getString("class"));
                        rw[count].setPurchase(obs.getString("purchase"));
                        rw[count].setName(obs.getString("name"));
                        rw[count].setTicketno(obs.getString("ticketno"));
                        rw[count].setAirname(obs.getString("airname"));
                        rw[count].setDeparture(obs.getString("departure"));
                        rw[count].setBillnumber(obs.getInt("billnumber"));
                        rw[count].setTicketstatus(obs.getInt("ticketstatus"));
                        rw[count].setPrice(obs.getDouble("price"));
                        count++;
                    }
                    httpURLConnection.disconnect();
                    return rw;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
    }
    public class backgroundchats extends AsyncTask<String,Void,chat_row[]> {
        String qr;

        backgroundchats(String qr) {
            this.qr = qr;
        }

        @Override
        protected chat_row[] doInBackground(String... params) {
            try {
                URL url = new URL("https://pushparajkvp.000webhostapp.com/getChats.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(2000);
                httpURLConnection.setRequestMethod("POST");

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String post = URLEncoder.encode("airport", "UTF-8") + "=" + URLEncoder.encode(airport, "UTF-8");
                br.write(post);
                br.flush();
                br.close();
                os.close();
                int res = httpURLConnection.getResponseCode();
                if (res == HttpURLConnection.HTTP_OK) {
                    BufferedReader brs = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder ress = new StringBuilder();
                    String line;
                    while ((line = brs.readLine()) != null) {
                        ress.append(line);
                    }
                    br.close();
                    JSONObject ob = new JSONObject(ress.toString());
                    Log.i("push",ress.toString());
                    JSONArray ar = ob.getJSONArray("chats");
                    int count = 0;
                    chat_row[] rw = new chat_row[ar.length()];
                    while (count < ar.length()) {
                        rw[count] = new chat_row();
                        JSONObject obs = ar.getJSONObject(count);
                        if (obs.getString("qr").equals(qr))
                            rw[count].setLor(1);
                        else
                            rw[count].setLor(0);
                        rw[count].setMess(obs.getString("mess"));
                        rw[count].setName(obs.getString("name"));
                        count++;
                    }
                    return rw;
                }
                httpURLConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(chat_row[] params) {
            if (params != null) {
                if (params.length != 0) {
                    openNext(params);
                }
            }else {
                Toast.makeText(superUserPage.this,"Please check your connection",Toast.LENGTH_LONG).show();
            }
        }
    }
    void openNext(chat_row[] param){

        RecyclerView.Adapter adapter = new chatAdapter(param);
        adapter.hasStableIds();
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.canScrollVertically();
        layout.setReverseLayout(true);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycle);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
}
