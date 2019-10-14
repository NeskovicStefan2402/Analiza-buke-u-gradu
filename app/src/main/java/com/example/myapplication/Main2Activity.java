package com.example.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class Main2Activity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    TextView macText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        macText=findViewById(R.id.macTxt);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            JSONArray jsonArray=getElement(MainActivity.macTxt.getText().toString());
            LinkedList<pozicija> pozicije=vrati();
            for(int i=0;i<pozicije.size();i++){
                LatLng position = new LatLng(pozicije.get(i).lat, pozicije.get(i).lan);
                mMap.addMarker(new MarkerOptions().position(position).title(jsonArray.getJSONObject(i).getString("vreme")));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            }
            macText.setText("   "+jsonArray.getJSONObject(0).getString("mac")+"");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONArray getElement(String mac) throws IOException, JSONException {
        URL urlForGetRequest = new URL("http://192.168.0.15:5000/nesto/"+mac);
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            JSONArray jsonArray=new JSONArray(response.toString());
            return jsonArray;
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }
    public LinkedList<pozicija> vrati() throws IOException, JSONException {
        LinkedList<pozicija> pozicije=new LinkedList<pozicija>();
        JSONArray jsonArray=getElement(MainActivity.macTxt.getText().toString());
        for(int i=0;i<jsonArray.length();i++){
            double lat=jsonArray.getJSONObject(i).getDouble("lat");
            double lon=jsonArray.getJSONObject(i).getDouble("lon");
            pozicija p=new pozicija();
            p.lan=lon;
            p.lat=lat;
            pozicije.add(p);
        }
        return pozicije;
    }

    public void nazad(View view) {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();

    }
}
