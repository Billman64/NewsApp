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

    final String TAG = "HTTPHandler";

    // method for initiating an http request
    public String startHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        InputStream is = null;

        Log.d(TAG,"url (in HTTPHandler, before setting connection): " + url);

        // connection settings with handling
        try{
            uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("GET");
            uc.setReadTimeout(7000);
            uc.setConnectTimeout(10000);
            Log.d(TAG,"HTTPHandler about to connect. uc.toString():  " + uc.toString());
            uc.connect();   // potential cause of connection error here (also need Internet permission added in manifest for this)
            //TODO: implement handling for connection issues here, ie: firewall blockage
            Log.d(TAG,"HTTPHandler - urlConnection connected!! url:  " + url);
            try {
                is = uc.getInputStream();   //TODO: API error handling needed here
            } catch(Exception e){
                Log.e(TAG, "API error! Response: " + uc.getResponseCode() + " " + uc.getResponseMessage());
            }
            Log.d(TAG,"HTTPHandler input stream:  " + is.toString());
            jsonResponse = streamToString(is);

            Log.d(TAG,"url (in HTTPHandler, url connection is good): " + url);
        } catch (Exception e) {
            Log.e(TAG,"Error possibly in urlConnection. uc.toString(): " + uc.toString() + "\n error message: " + e);
            Log.e(TAG, " API response: " + is.toString());
            return null;

        } finally {
            if(uc != null) {
                uc.disconnect();
                Log.d(TAG, "Handler disconnected");
            }
            if(is != null) {
                is.close();
                Log.d(TAG, "Handler closed connection");
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
