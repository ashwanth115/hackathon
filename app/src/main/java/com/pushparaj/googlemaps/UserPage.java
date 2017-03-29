package com.pushparaj.googlemaps;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
@SuppressWarnings("deprecation")
public class UserPage extends TabActivity {

    TabHost host;
    TextView txtwelcome,txtname,txtflightno,txtfrom,txtto,txttime,txtticketno,txtclass,txtpurchase,txtcompany,txtbillno
            ,txtticketstatus,txtprice,txtgate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle i = getIntent().getExtras();
        String qr = i.getString("qr");
        int type = i.getInt("type");
        setContentView(R.layout.userpage);
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
        BackgroundPass bks = new BackgroundPass(qr);
        bks.execute();
        backGroundShop bk = new backGroundShop();
        bk.execute();

        TabHost.TabSpec spec1 = host.newTabSpec("Details");
        spec1.setIndicator("Details");
        spec1.setContent(R.id.Details);
        host.addTab(spec1);


        TabHost.TabSpec spec2 = host.newTabSpec("Shops");
        spec2.setIndicator("Shops");
        spec2.setContent(R.id.Shops);
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

    public void onClickRefresh(View view) {
        backGroundShop bk = new backGroundShop();
        bk.execute();
    }

    public class backGroundShop extends AsyncTask<String,Void,latlngrows[]> {
        String jString;
        @Override
        protected latlngrows[] doInBackground(String... params) {
            try {
                URL url = new URL("https://pushparajkvp.000webhostapp.com/showShops.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(2000);
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String jsonString;
                while((jsonString = br.readLine())!=null){
                    sb.append(jsonString).append("\n");
                }
                is.close();
                br.close();
                jString = sb.toString().trim();

                JSONObject ob = new JSONObject(jString);
                JSONArray ar = ob.getJSONArray("shop");
                int count = 0;
                latlngrows[] rw = new latlngrows[ar.length()];
                while(count < ar.length()) {
                    rw[count] = new latlngrows();
                    JSONObject obs = ar.getJSONObject(count);
                    rw[count].setIcon(obs.getInt("icon"));
                    rw[count].setLat(obs.getDouble("lat"));
                    rw[count].setLng(obs.getDouble("lng"));
                    rw[count].setName(obs.getString("name"));
                    rw[count].setOffer(obs.getString("offer"));
                    rw[count].setShop(obs.getInt("shop"));
                    count++;
                }
                httpURLConnection.disconnect();
                return rw;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(latlngrows[] passengerDetailses) {
        if(passengerDetailses!=null) {
            if (passengerDetailses.length > 0) {
                displayDetails(passengerDetailses);
            }
        }//else//Toast.makeText(UserPage.this,"No Such QR code",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void displayDetails(latlngrows[] param) {
        RecyclerView.Adapter adapter = new REcyclerAdapter(param);
        adapter.hasStableIds();
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        layout.canScrollVertically();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
    class BackgroundPass extends AsyncTask<String,Void,PassengerDetails[]> {
        String jString,qr;


        BackgroundPass() {
        }

        public BackgroundPass(String qr) {
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
                    if(passengerDetailses[0].getTicketstatus()==1)
                    txtticketstatus.setText("Confirmed");
                    else
                    txtticketstatus.setText("Not Confirmed");
                    txtprice.setText(String.valueOf(passengerDetailses[0].getPrice()));
                    txtgate.setText(passengerDetailses[0].getGate());
                }else{
                    Toast.makeText(UserPage.this,"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected PassengerDetails[] doInBackground(String... params) {
                try {
                    URL url = new URL("https://pushparajkvp.000webhostapp.com/select.php");
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
}
