package com.example.bill.newsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHandler {
    public HTTPHandler(){
    }

    // method for initiatinng an http request
    public String startHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection uc = null;
        InputStream is = null;

        // connection settings with handling
        try{
            uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("GET");
            uc.setReadTimeout(5000);
            uc.setConnectTimeout(10000);
            uc.connect();
            is = uc.getInputStream();
            jsonResponse = streamToString(is);
        } catch (Exception e) {

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
