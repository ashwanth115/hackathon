package com.pushparaj.googlemaps;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;


public class MainActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    GoogleMap mGoogleMap;
    EditText searchText,iconName;
    Button btn,btnadd,btndelete;
    Spinner spinner;
    boolean issuper;
    boolean adds,delets;
    float zoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googlePlaySevicesAvailable()) {
            setContentView(R.layout.activity_main);
            searchText = (EditText) findViewById(R.id.txtsearch);
            Background bk = new Background();
            bk.execute("getdata");
            adds = false;
            delets=false;
            iconName = (EditText)findViewById(R.id.iconname);
            spinner=(Spinner)findViewById(R.id.spinner);
            btn = (Button)findViewById(R.id.btnsearch);
            btnadd=(Button)findViewById(R.id.btnadd);
            btndelete=(Button)findViewById(R.id.btndelete);
            int type = getIntent().getExtras().getInt("user");
            if(type==1){
                btnadd.setVisibility(View.VISIBLE);
                btndelete.setVisibility(View.VISIBLE);
                issuper = true;
            }else{
                issuper=false;
            }
            ArrayAdapter<String> adap = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.iconList));
            adap.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
            spinner.setAdapter(adap);
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
            zoom = 18;
        } else {
            setContentView(R.layout.noplayservices);
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
    }

    public void gotolatlng(double lat,double lng,float zoom){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public boolean googlePlaySevicesAvailable(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int availability = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(availability == ConnectionResult.SUCCESS){
            return  true;
        }else if(googleApiAvailability.isUserResolvableError(availability)){
            Dialog dialog = googleApiAvailability.getErrorDialog(this,availability,0);
            dialog.show();
        }else{
            Toast.makeText(this,"Cannot Connect To Google Play Services",Toast.LENGTH_LONG).show();
        }
        return false;
    }


    public void searchString(View view) throws IOException {
        if(!searchText.getText().toString().equals("")){
            Geocoder geo = new Geocoder(this);
            List<Address> list = geo.getFromLocationName(searchText.getText().toString(),1);
            if(list.isEmpty()){
                return;
            }
            Address address = list.get(0);
            gotolatlng(address.getLatitude(),address.getLongitude(),zoom);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(!adds){
            return;
        }
        else if(!spinner.getSelectedItem().toString().equals("Select A Icon")  &&!spinner.getSelectedItem().toString().equals("") && !iconName.getText().toString().equals("")) {
            int id = findIdValue(spinner.getSelectedItem().toString());
            Background bk = new Background(String.valueOf(id), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),iconName.getText().toString());
            bk.execute("postdata");
        }else{
            Toast.makeText(this,"Please Write Fill Up The Name and Icon",Toast.LENGTH_LONG).show();
        }
    }
    public int findIdValue(String icon){
        switch(icon){
            case "Shop" : return R.drawable.sale;
            case "Book Shop" : return R.drawable.bookshop;
            case "Cloth Shop" : return R.drawable.clothshop;
            case "Coffee Shop" : return R.drawable.coffee;
            case "Gate" : return R.drawable.gate;
            case "Money Exchange" : return R.drawable.download;
            case "Free Wifi" : return R.drawable.freewifi;
            case "Gift Shop" : return R.drawable.giftshop;
            case "Grocery" : return R.drawable.grocery;
            case "Food Stall" : return R.drawable.sfoodstall;
            case "Washroom" : return R.drawable.washroom;
            default:return 0;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(delets){
            Background bk = new Background(String.valueOf(marker.getPosition().latitude),String.valueOf(marker.getPosition().longitude));
            bk.execute("delete");
            marker.remove();
            delets=false;
            Toast.makeText(this,"Marker Deleted",Toast.LENGTH_LONG).show();
            btndelete.setVisibility(View.VISIBLE);
            btnadd.setVisibility(View.VISIBLE);
            return true;
        }else if(issuper){
            btnadd.setVisibility(View.VISIBLE);
            btndelete.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public void addClick(View view) {
        adds = true;
        searchText.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.INVISIBLE);
        iconName.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        btndelete.setVisibility(View.INVISIBLE);
        btnadd.setVisibility(View.INVISIBLE);
    }

    public void deleteClick(View view) {
        delets=true;
        btndelete.setVisibility(View.INVISIBLE);
        btnadd.setVisibility(View.INVISIBLE);
    }

    //BACKGROUND TASK
    class Background extends AsyncTask<String,Void,latlngrows[]> {
        String jString;
        String lat,lng,name,icon,shop;

        Background() {
        }

        Background(String icon, String lat, String lng, String name) {
            this.icon = icon;
            int icons = Integer.parseInt(icon);
            if((icons == R.drawable.bookshop) || (icons == R.drawable.sale) || (icons == R.drawable.clothshop)
            ||(icons==R.drawable.coffee)||(icons==R.drawable.giftshop)||(icons == R.drawable.grocery)
            ||(icons==R.drawable.sfoodstall)){
                shop=String.valueOf(1);
            }else{
                shop=String.valueOf(0);
            }
            this.lat = lat;
            this.lng = lng;
            this.name = name;
        }

        public Background(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected latlngrows[] doInBackground(String... params) {
            if(params[0].equals("getdata")){
                try {
                    URL url = new URL("https://pushparajkvp.000webhostapp.com/login.php");
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
                    JSONArray ar = ob.getJSONArray("latlng");
                    int count = 0;
                    latlngrows[] rw = new latlngrows[ar.length()];
                    while(count < ar.length()) {
                        rw[count] = new latlngrows();
                        JSONObject obs = ar.getJSONObject(count);
                        rw[count].setIcon(obs.getInt("icon"));
                        rw[count].setLat(obs.getDouble("lat"));
                        rw[count].setLng(obs.getDouble("lng"));
                        rw[count].setName(obs.getString("name"));
                        count++;
                    }
                    httpURLConnection.disconnect();
                    return rw;

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }else if(params[0].equals("postdata")){
                URL url = null;
                try {
                    url = new URL("https://pushparajkvp.000webhostapp.com/insert.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    String post = URLEncoder.encode("lat","UTF-8")+"="+URLEncoder.encode(lat,"UTF-8")+"&"
                                  +URLEncoder.encode("lng","UTF-8")+"="+URLEncoder.encode(lng,"UTF-8")+"&"
                                  +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                  +URLEncoder.encode("shop","UTF-8")+"="+URLEncoder.encode(shop,"UTF-8")+"&"
                                 +URLEncoder.encode("icon","UTF-8")+"="+URLEncoder.encode(icon,"UTF-8");
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
                    latlngrows[] rw = new latlngrows[1];
                    rw[0] = new latlngrows(Double.parseDouble(lat),Double.parseDouble(lng),name,Integer.parseInt(icon));
                return rw;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(params[0].equals("delete")){
                URL url = null;
                try {
                    url = new URL("https://pushparajkvp.000webhostapp.com/deleteLat.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    String post = URLEncoder.encode("lat","UTF-8")+"="+URLEncoder.encode(lat,"UTF-8")+"&"
                            +URLEncoder.encode("lng","UTF-8")+"="+URLEncoder.encode(lng,"UTF-8");
                    Log.i("push",post);
                    bw.write(post);
                    bw.flush();
                    bw.close();
                    os.close();
                    int response = httpURLConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuffer res = new StringBuffer();
                        String line;
                        while((line = br.readLine()) != null){
                            res.append(line);
                        }
                        br.close();
                        Log.i("push",res.toString());
                    }
                    httpURLConnection.disconnect();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(latlngrows[] params) {
            if(params != null) {
                for (int i = 0; i < params.length; i++) {
                    addmarker(params[i].getLat(), params[i].getLng(), params[i].name, params[i].getIcon());
                }
                adds = false;
                spinner.setVisibility(View.INVISIBLE);
                iconName.setVisibility(View.INVISIBLE);
                searchText.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
                if(issuper) {
                    btnadd.setVisibility(View.VISIBLE);
                    btndelete.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }
    public void addmarker(double lat,double lng,String name,int icon){
        loading ld = new loading(icon,lat,lng,name);
        ld.execute();
    }
    class loading extends AsyncTask<Void,Void,Bitmap>{
        double lat,lng;
        String name;
        int icon;

        public loading(int icon, double lat, double lng, String name) {
            this.icon = icon;
            this.lat = lat;
            this.lng = lng;
            this.name = name;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bits = BitmapFactory.decodeResource(getResources(),icon);
            bits = Bitmap.createScaledBitmap(bits,45,45,false);
            return bits;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            MarkerOptions mo = new MarkerOptions().title(name)
                    .position(new LatLng(lat,lng))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            mGoogleMap.addMarker(mo);
        }
    }


}
