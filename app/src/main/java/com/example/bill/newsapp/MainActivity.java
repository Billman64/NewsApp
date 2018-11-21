package com.example.bill.newsapp;

/*
    News app
    Created by Bill Lugo for Udacity course. 11/21/18
    News source is Guardian. Any news content displayed comes from and belongs to them.
 */

import android.content.AsyncTaskLoader;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String>> newsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = new ArrayList<>();


        Button button = (Button) findViewById(R.id.button);
        final TextView output = (TextView) findViewById(R.id.output);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieve news via API call in a worker thread
                output.setText(R.string.searching);
                new GetNews().execute();
            }
        });
    }

    // AsyncTaskLoader for network call for news data
    private class GetNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            // start with API endpoing
            EditText et = (EditText) findViewById(R.id.et);
            String url = getString(R.string.API_endpoint) + et.getText().toString() + getString(R.string.API_endpoint_ender);
            //TODO: error trap input (limit size, character restrictions, etc.)

            // HTTP handling by HTTPHandler class
            HTTPHandler hh = new HTTPHandler();

            String json="";
            try{
                // make request to the endpoint's URL
                json = hh.startHttpRequest(createURL(url));
        }   catch (IOException e) {
                return null;
            }

            if(json != null) {
                try {
                    // create JSON object
                    JSONObject jsonObject = new JSONObject(json);

                    // get JSON array node
                    JSONArray news = jsonObject.getJSONArray("news");
                    Log.d(getString(R.string.tag), "news length: " + Integer.toString(news.length()));

                    // loop through records
                    for(int i=0; i < news.length(); i++){
                        JSONObject j = news.getJSONObject(i);
                        String id = j.getString("id");
                        String pubDate = j.getString("webPublicationDate");
                        String webTitle = j.getString("webTitle");
                        String articleUrl = j.getString("webUrl");

                        // put each record into hashmap
                        HashMap<String,String> newsRecord = new HashMap<>();
                        newsRecord.put("webTitle", webTitle);
                        newsRecord.put("pubDate", pubDate);
                        newsRecord.put("webUrl", articleUrl);

                        // add news article record to list
                        newsList.add(newsRecord);
                    }

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.parse_error) + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        //TODO: use a Handler to update output TextView to let user know the result of the request


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            TextView output = (TextView) findViewById(R.id.output);
            if(result != null) output.setText(newsList.toString());
            else output.setText("");
        }
    }



    private URL createURL(String inputUrl) {
        URL url = null;
        try {
            url = new URL(inputUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }



}
