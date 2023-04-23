package com.example.bill.newsapp;

/*
    News app
    Created by Bill Lugo for Udacity course. 11/21/18
    Revised: 12/18
    News source is Guardian. Any news content displayed comes from and belongs to them.
    TODO: implement preference to change default search term
 */

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {
    public static final int LOADER_ID = 1;

    public ArrayList newsItemList = new ArrayList<NewsItem>();
    public String TAG = "MainAct";

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int loaderId, Bundle args) {
        try{
            URL url = new URL(createURI().toString());
            return new NewsLoader(this,url);
        } catch (MalformedURLException e) {
            Log.e(TAG,"onCreateLoader(): MalformedURL " + e);
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItemList){
        Log.d(TAG, "AsyncTaskLoader onLoadFinished()");

        // clear output textView, indicating that the network call is done
        TextView output = (TextView) findViewById(R.id.output);
        output.setText("");
        output.setVisibility(View.INVISIBLE);

        // if no news items found, display an message, as a newsItem itself in the listView
        if(null == newsItemList ) {
           output.setText(R.string.no_result);
           output.setVisibility(View.VISIBLE);
        } else {
            // update listView otherwise
            ListView lv = (ListView) findViewById(R.id.listView);
            NewsAdapter adapter = new NewsAdapter(this, 0, newsItemList);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clear focus from editText
        EditText et = (EditText) findViewById(R.id.et);
        et.clearFocus();

        Button button = (Button) findViewById(R.id.button);
        final TextView output = (TextView) findViewById(R.id.output);   //TODO: retain output text on orientation change

        // Default data pull to show news on startup
        // if there's an Internet connection, pull news, otherwise display a connection message
        if(isInternetAvailable()) getNews();
        else {
            output.setText(R.string.no_connection);
            output.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick() activated");

                // guard against empty input
                EditText et = (EditText) findViewById(R.id.et);
                if(!et.getText().toString().isEmpty()){

                    // Retrieve news via API call in a worker thread
                    output.setText(R.string.searching);
                    output.setVisibility(View.VISIBLE);

                    // if there's an Internet connection, pull news, otherwise display a connection message
                    if(isInternetAvailable()) getNews();
                            else {
                                output.setText(R.string.no_connection);
                                output.setVisibility(View.VISIBLE);
                    }

                    Log.d(TAG, "onClick() done");
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isInternetAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // check if network info exists, return true if existing and connected, false otherwise
        if(netInfo != null) {
            Log.d(TAG, "checking internet connection state: " + cm.getActiveNetworkInfo().isConnected());
            return cm.getActiveNetworkInfo().isConnected();
        } else {
            Log.d(TAG, "checking internet connection state: not connected!");
            return false;
        }
    }

    private void getNews(){

        // in response to onClick(), sets up and calls LoaderManager to pull the news from the web via worker thread
        try {
            LoaderManager loaderManager = getLoaderManager();
            Log.d(TAG, "loaderManager created.");

            Loader<NewsItem> loader = loaderManager.getLoader(LOADER_ID);

            // init or restart loader
            if(loader == null){
                loaderManager.initLoader(LOADER_ID, null, this);
                Log.d(TAG, "loaderManaager initialized. Loader_ID: " + LOADER_ID + ".");
            } else {
                loaderManager.restartLoader(LOADER_ID, null, this);
            }

            // run NewsLoader
            new NewsLoader(getApplicationContext(), new URL(createURI().toString()));

        } catch (MalformedURLException e) {
            Log.e(TAG,"onClick() Malformed URL in NewsLoader: " + e);
        }
    }

    private Uri.Builder createURI() {
        Uri.Builder uri = new Uri.Builder();
        try{
            // build URI part by part
            uri.scheme("http");
            uri.authority("content.guardianapis.com");
            uri.appendPath("search");

            //error-trap for spaces in input, by replacing with "%20" to conform to web unicode
            EditText et = (EditText) findViewById(R.id.et);
            String searchInput = et.getText().toString();
            if(searchInput.contains(" ")){
                searchInput.replace(" ", "%20");
            }

            // append parameters
            uri.appendQueryParameter("q", searchInput);
            uri.appendQueryParameter("api-key","ad802560-e4de-4aea-9286-8a462045964d");
//            uri.appendQueryParameter("api-key","test");
            uri.appendQueryParameter("show-tags","contributor");
            //TODO: implement fallback query with test api key     uri.appendQueryParameter("api-key","test");
            //TODO: secure key by hiding it in lower level (C/C++ layer), store encrypted, and decrypt at run-time

            uri.build();
            Log.d(TAG, "Uri Builder: " + uri.toString());
        } catch(Exception e) {
            Log.e(TAG,getString(R.string.error_malformed_url));
            return null;
        }
        return uri;
    }

}
