package com.example.bill.newsapp;

/*
    News app
    Created by Bill Lugo for Udacity course. 11/21/18
    News source is Guardian. Any news content displayed comes from and belongs to them.
 */

import android.content.AsyncTaskLoader;
import android.net.Uri;
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
import java.net.URI;
import java.net.URISyntaxException;
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
//            EditText et = (EditText) findViewById(R.id.et);
//            String url = getString(R.string.API_endpoint) + et.getText().toString().trim() + getString(R.string.API_endpoint_ender);
//            //TODO: error trap input (limit size, character restrictions, etc.)
//            Log.d(getString(R.string.tag),"url: " + url);

            // HTTP handling by HTTPHandler class
            HTTPHandler hh = new HTTPHandler();

            String json="";
            try{
                // make request to the endpoint's URL
                json = hh.startHttpRequest(new URL(createURI().toString()));
//                json = hh.startHttpRequest(new URL("http://content.guardianapis.com/search?q=nintendo&api-key=test"));
                Log.d(getString(R.string.tag),"(asyncTask) json: " + json.substring(0,100) + "..." );
        }   catch (IOException e) {
                return null;
            }

            if(json != null) {
                try {
                    // create JSON object
                    JSONObject jsonObject = new JSONObject(json);

                    Log.d(getString(R.string.tag),"(asyncTask) json object created with length: " + jsonObject.length() );

                    // get JSON array node
                    JSONArray news = jsonObject.getJSONArray("response");   //TODO: fix runtime exception here
                    Log.d(getString(R.string.tag), "news length: " + Integer.toString(news.length()));

                    // loop through records
                    for(int i=0; i < news.length(); i++){
                        JSONObject j = news.getJSONObject(i);
                        String id = j.getString("id");
                        String pubDate = j.getString("webPublicationDate");
                        String webTitle = j.getString("webTitle");
                        String articleUrl = j.getString("webUrl");
                        Log.d(getString(R.string.tag), "news: " + webTitle);    // it could be fun or funny to see the news in a logcat log

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
                            Log.d(getString(R.string.tag), "Parse exception: " + e.getMessage() );
                            Toast.makeText(getApplicationContext(), getString(R.string.parse_error) + e.getMessage(),
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

            if(!newsList.isEmpty()) output.setText(newsList.get(0).toString());
                else output.setText(getString(R.string.no_result));
        }
    }

    private Uri.Builder createURI() {
        Uri.Builder uri = new Uri.Builder();
        try{
            uri.scheme("http");
            uri.authority("content.guardianapis.com");
            uri.appendPath("search");

            EditText et = (EditText) findViewById(R.id.et);
            uri.appendQueryParameter("q", et.getText().toString());
            uri.appendQueryParameter("api-key","test");
            uri.build();
            Log.d(getString(R.string.tag), "Uri Builder: " + uri.toString());
        } catch(Exception e) {
            Log.d(getString(R.string.tag),getString(R.string.error_malformed_url));
            return null;
        }
        return uri;
    }
}
