package com.example.bill.newsapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHandler {
    public HTTPHandler(){
    }

    // method for initiating an http request
    public String startHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        InputStream is = null;

        Log.d("NewsApp ----","url (in HTTPHandler, before setting connection): " + url);

        // connection settings with handling
        try{
            uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("GET");
            uc.setReadTimeout(7000);
            uc.setConnectTimeout(10000);
            Log.d("NewsApp ----","HTTPHandler about to connect. uc.toString():  " + uc.toString());
            uc.connect();   // potential cause of connection error here (also need Internet permission added in manifest for this)
            Log.d("NewsApp ----","HTTPHandler - urlConnection connected!! url:  " + url);
            is = uc.getInputStream();
            Log.d("NewsApp ----","HTTPHandler input stream:  " + is.toString());
            jsonResponse = streamToString(is);

            Log.d("NewsApp ----","url (in HTTPHandler, url connection is good): " + url);
        } catch (Exception e) {
            Log.e("NewsApp ----","Error possibly in urlConnection. uc.toString(): " + uc.toString() + "\n error message: " + e);
            return null;

        } finally {
            if(uc != null) {
                uc.disconnect();
            }
            if(is != null) {
                is.close();
            }
        }
        return jsonResponse;
    }

    // captures a stream into a string
    private String streamToString(InputStream is) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String inputLine;
        try{
            while((inputLine =  bufferedReader.readLine()) != null) {
                sb.append(inputLine).append('\n');
            }
        }   catch (IOException e) {
            e.printStackTrace();
        }   finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }



}
