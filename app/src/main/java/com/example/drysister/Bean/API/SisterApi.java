package com.example.drysister.Bean.API;

import android.util.Log;

import com.example.drysister.Bean.Sister;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SisterApi {
    private static final String TAG ="Network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    public ArrayList<Sister>fetchSister(int count,int page){
        String fetchUrl =BASE_URL + count+"/"+ page;
        ArrayList<Sister> sisters =new ArrayList<>();
        try {
           URL url = new URL(fetchUrl);
        HttpURLConnection conn =(HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        int code =conn.getResponseCode();
            Log.v(TAG,"Server response"+code);
        if (code==200) {
            InputStream in = conn.getInputStream();
            byte[]data =readFtomStream(in);
            String result = new String(data,"UTF-8");
            sisters =parseSister(result);
        }else {
            Log.e(TAG,"请求失败:"+code);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sisters;
}

    private ArrayList<Sister> parseSister(String content) throws JSONException {
        ArrayList<Sister>sisters=new ArrayList<>();
        JSONObject object=new JSONObject(content);
        JSONArray array=object.getJSONArray("results");
        for (int i =0;i < array.length();i++){
          JSONObject results  = (JSONObject) array.get(i);
          Sister sister =new Sister();
          sister.set_id(results.getString("_id"));
            sister.setCreateAt(results.getString("createdAt"));
            sister.setDesc(results.getString("desc"));
            sister.setPublishedAt(results.getString("publishedAt"));
            sister.setSource(results.getString("source"));
            sister.setType(results.getString("type"));
            sister.setUrl(results.getString("url"));
            sister.setUsed(results.getBoolean("used"));
            sister.setWho(results.getString("who"));
            sisters.add(sister);
        }
        return sisters;

    }

    public byte[] readFtomStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) !=-1){
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }
    }
