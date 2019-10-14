package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    TextView txt,minTxt,maxTxt,BMtxt,prosekTxt;
    private GoogleMap mMap;
    public static int brojac;
    public static EditText macTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt=findViewById(R.id.polje);
        txt.setMovementMethod(new ScrollingMovementMethod());
        minTxt=findViewById(R.id.minAmpl);
        maxTxt=findViewById(R.id.maxAmpl);
        BMtxt=findViewById(R.id.brMer);
        macTxt=findViewById(R.id.textViewMAC);
        prosekTxt=findViewById(R.id.prosekAmpl);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public Float minVrati(JSONArray json,String sort) throws JSONException {
        LinkedList<String> brojevi=new LinkedList<>();
            for(int i=0;i<json.length();i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                brojevi.addAll(vratiMinObject(jsonObject));
            }
            LinkedList<Float> lista=prevediListuUFloat(brojevi);
            if(sort.equals("max")){
                return odrediMax(lista);
            }else if(sort.equals("prosek")){
                return prosecnoDecibela(lista);
            }else if(sort.equals("min")){
            return odrediMin(lista);}
            return 0.0f;
    }
    public Float odrediMin(LinkedList<Float> lista) {
        Float min=Float.MAX_VALUE;
        for (int i=0;i<lista.size();i++){
            if(min>lista.get(i)){
                min=lista.get(i);
            }
        }
        return min;
    }

    public Float odrediMax(LinkedList<Float> lista) {
        Float max=Float.MIN_VALUE;
        for (int i=0;i<lista.size();i++){
            if(max<lista.get(i)){
                max=lista.get(i);
            }
        }
        return max;
    }
    public Snimak pronadji(LinkedList<Snimak> snimci,String ele,Float vr){
        if(ele.equals("max")){
            float max=0;
            Snimak maxSnimak=new Snimak();
            for(int i=0;i<snimci.size();i++){
                for(int j=0;j<snimci.size();j++) {
                    if (max<snimci.get(i).vrednosti.get(j).vrednost) {
                        max=snimci.get(i).vrednosti.get(j).vrednost;
                        maxSnimak=snimci.get(i);
                    }
                }
            }
            return maxSnimak;
        }else{
            float min=Float.MAX_VALUE;
            Snimak minSnimak=new Snimak();
            for(int i=0;i<snimci.size();i++){
                for(int j=0;j<snimci.size();j++) {
                    if (min>snimci.get(i).vrednosti.get(j).vrednost) {
                        min=snimci.get(i).vrednosti.get(j).vrednost;
                        minSnimak=snimci.get(i);
                    }
                }
            }
            return minSnimak;
        }
    }
    public LinkedList<Float> prevediListuUFloat(LinkedList<String> lista){
        LinkedList<Float> floatLinkedList=new LinkedList<>();
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).charAt(lista.get(i).length()-1)=='}'){
                String neki=lista.get(i).substring(0, lista.get(i).length() - 1);
                floatLinkedList.add(Float.parseFloat(neki));
            }else{

            floatLinkedList.add(Float.parseFloat(lista.get(i)));}
        }
        return floatLinkedList;
    }
    /*
    public LinkedList<Snimak> prebaciUClass(JSONArray jsonArray) throws JSONException {
        LinkedList<Snimak> snimci=new LinkedList<>();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
                Snimak snimak=new Snimak();
                snimak.lat=jsonObject.getDouble("lat");
                snimak.lon=jsonObject.getDouble("lon");
                snimak.naziv=jsonObject.getString("naziv");
                snimak.vreme=jsonObject.getString("vreme");
                LinkedList<String> listaString=vratiMinObject(jsonObject);
                LinkedList<Float> listaFloat=prevediListuUFloat(listaString);
                for(int j=0;j<listaFloat.size();j++){
                    snimak.vrednosti.get(j).podeok+=5;
                    snimak.vrednosti.get(j).vrednost=listaFloat.get(j);
                }
                snimci.add(snimak);


        }
        return snimci;
    }*/
    public LinkedList<String> vratiMinObject(JSONObject jObj) throws JSONException {
        String analiza=jObj.getString("analiza");
        String[] podaci=analiza.split(",");
        LinkedList<String> brojevi=new LinkedList<>();
        for(int i=0;i<podaci.length;i++){
            String[] elementi=podaci[i].split(":");
            String brSaZagradom=elementi[1].substring(0, elementi[1].length() - 1);
            String broj= brSaZagradom;
            brojevi.add(broj);
        }
        return brojevi;
    }
    public String ispisi(LinkedList<Snimak> snimci){
        String rec="";
        for(int i=0;i<snimci.size();i++){
            rec+=snimci.get(i).naziv+" ";
        }
        return rec;
    }
    public Float prosecnoDecibela(LinkedList<Float> vrednosti){
        Float suma=0.0f;
        for(int i=0;i<vrednosti.size();i++){
            suma+=vrednosti.get(i);
        }

    return suma/vrednosti.size();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void klikni1(View view) throws IOException, JSONException {
    if(macTxt.getText().toString().equals("64:89:F1:74:84:99")){
        JSONArray jsonArray = getElement(macTxt.getText().toString());
        String poruka = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            poruka += "               " + jsonArray.getJSONObject(i).getString("naziv") + "                 " + jsonArray.getJSONObject(i).getString("vreme") + "\n";
        }
        BMtxt.setText(jsonArray.length()+"");
        prosekTxt.setText(Math.round(minVrati(jsonArray, "prosek") * 100.0) / 100.0+" dB");
        minTxt.setText(Math.round(minVrati(jsonArray, "min") * 100.0) / 100.0+" dB ");
        maxTxt.setText(Math.round(minVrati(jsonArray, "max") * 100.0) / 100.0 + " dB");
        analMac();
        txt.setText(poruka);
    }else{
            Toast.makeText(this, "Unesite korektnu MAC adresu!", Toast.LENGTH_SHORT).show();
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
    public void analMac() throws IOException, JSONException {
        JSONArray jarray=getElement(macTxt.getText().toString());

    }

    public void prebaci(View view) {
        if(macTxt.getText().toString().equals("64:89:F1:74:84:99")){
            Intent i=new Intent(this,Main2Activity.class);
            startActivity(i);
            finish();}
        else{
            Toast.makeText(this, "Unesite MAC adresu!", Toast.LENGTH_SHORT).show();
        }
    }
}
