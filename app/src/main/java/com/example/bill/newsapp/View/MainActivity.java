package com.example.bill.newsapp.View;

/*
    News app
    Created by Bill Lugo for Udacity course. 11/21/18
    Revised: 12/18
    News source is Guardian. Any news content displayed comes from and belongs to them.

    API documentation: https://open-platform.theguardian.com/documentation/
    TODO: implement preference to change default search term
 */

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bill.newsapp.Model.KeyRetriever;
import com.example.bill.newsapp.Model.NewsItem;
import com.example.bill.newsapp.Presenter.NewsLoader;
import com.example.bill.newsapp.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {
    public static final int LOADER_ID = 1;

    public volatile ArrayList newsItemList = new ArrayList<NewsItem>();
//    public volatile List newsItemList = Collections.synchronizedList(new ArrayList<NewsItem>()); // atomic arrayList
    public String TAG = "MainAct";

    //TODO: refactor non-View architectural functions to proper MVP placement

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

//            Log.d(TAG, "onLoadFinished() newsItemList size: " + newsItemList.size());
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader){
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: to support on-screen data persistance through orientation change and other events,
        // retrieve data from savedInstanceState or onSaveInstanceState()
        if(savedInstanceState != null) Log.d(TAG, "onCreate() - savedInstanceState exists. size: " + savedInstanceState.size());

//        if(!savedInstanceState.isEmpty()){
//            ListView lv = (ListView) findViewById(R.id.listView);
//
////            ArrayList newsItemList = new ArrayList<NewsItem>();
//
//                //TODO: fill newsItemList with data from instanceState
//
//            NewsAdapter adapter = new NewsAdapter(this, 0, newsItemList);
//            lv.setAdapter(adapter);
//
//        }

        // clear focus from editText
        EditText et = (EditText) findViewById(R.id.et);
        et.clearFocus();

        Button button = (Button) findViewById(R.id.button);
        final TextView output = (TextView) findViewById(R.id.output);   //TODO: retain output text on orientation change

        // Default data pull to show news on startup

        Log.d(TAG, "checking savedInstanceState...");
        if(savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState exists");
            if(!savedInstanceState.isEmpty()) Log.d(TAG, " savedInstanceState has data");
        } else Log.d(TAG, "savedInstanceState does not exist");


        // if there's an Internet connection, pull news, otherwise display a connection message
        Log.d(TAG, "onCreate() - newsItemList size (before sync block): " + newsItemList.size());
            if(isInternetAvailable()) {
                getNews();
            }
            else {
                output.setText(R.string.no_connection);
                output.setVisibility(View.VISIBLE);
            }

        Log.d(TAG, "newsItemList size after sync block: " + newsItemList.size());


        Log.d(TAG, "onCreate() - newsItemList size: " + newsItemList.size());
        if(newsItemList.size()>0) {
            savedInstanceState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) newsItemList);
            Log.d(TAG, "savedInstanceState updated");
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
//                        savedInstanceState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) newsItemList);    //TODO: fix error here
                        Log.d(TAG, "savedInstanceState updated. newsItemList size: " + newsItemList.size());

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

    private synchronized void getNews(){

        // in response to onClick(), sets up and calls LoaderManager to pull the news from the web via worker thread
        try {
            LoaderManager loaderManager = getLoaderManager();
            Log.d(TAG, "loaderManager created.");

            Loader<NewsItem> loader = loaderManager.getLoader(LOADER_ID);

            // init or restart loader
            if(loader == null){
                loaderManager.initLoader(LOADER_ID, null, this);
                Log.d(TAG, "loaderManager initialized. Loader_ID: " + LOADER_ID + ".");
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

            // implement region detection
            String edition = "us";
            switch(Locale.getDefault().getCountry()){
                case "US": edition = "us";
                    break;
                case "AU": edition = "au";
                    break;
                case "GB": edition = "uk";
                    break;
                default: edition = "international";
                    String[] Eu = {"BE", "EL", "LT", "PT", "BG", "ES", "LU", "RO", "CZ", "FR", "HU", "SI", "DK", "HR",
                            "MT", "SK", "DE", "IT", "NL", "FI", "EE", "CY", "AT", "SE", "IE", "LV", "PL", "UK",
                            "CH", "NO", "IS", "LI"};
                    if(Arrays.asList(Eu).contains(Locale.getDefault().getCountry())) edition = "europe";    //refactor
                    break;
            }
            uri.appendQueryParameter("edition", edition);
            Log.i(TAG, "REGION: " + Locale.getDefault().getCountry());

            uri.appendQueryParameter("q", searchInput);

            KeyRetriever keyRetriever = new KeyRetriever();
            String key = keyRetriever.getKey(); // refactor?
            uri.appendQueryParameter("api-key",key);
            uri.appendQueryParameter("show-tags","contributor");
            //TODO: implement fallback query with test api key     uri.appendQueryParameter("api-key","test");
            //TODO: secure key by hiding it in lower level (C/C++ layer), store encrypted, and decrypt at run-time

            uri.appendQueryParameter("show-blocks","body:latest");

            uri.appendQueryParameter("order-by", "relevance");
            uri.appendQueryParameter("order-by", "newest");


            uri.build();
            Log.d(TAG, "Uri Builder: " + uri.toString());
        } catch(Exception e) {
            Log.e(TAG,getString(R.string.error_malformed_url));
            return null;
        }
        return uri;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //TODO: get this to run (newsItemList gets updated on onLoadFinished() as a thread. Need to update it in main thread too! Con)
        Log.d(TAG, "onSaveInstanceState()");

        ListView lv = findViewById(R.id.listView);


        outState.putParcelableArrayList("list", newsItemList);
        Log.d(TAG, " parcelableArrayList has been put into outState. list isEmpty(): " + newsItemList.isEmpty());

//        lv.getChildAt(2)


//        Log.d(TAG, " onSaveInstanceState() - newsItemList size: " + newsItemList.size());

//        ArrayList al = new ArrayList(newsItemList);
//        al.addAll(newsItemList);

//        outState.putParcelableArrayList("list", al);    //TODO: fix crash here - java.util.Collections$SynchronizedRandomAccessList cannot be cast to java.util.ArrayList
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "onRestoreInstanceState()");

//        Log.d(TAG, "onRestoreInstanceState() has data?: " + !savedInstanceState.isEmpty());
//
//            if(!savedInstanceState.isEmpty()) {
//
//
//
//                newsItemList = savedInstanceState.getParcelableArrayList("list");
//
//                if(newsItemList != null) {
//                    Log.d(TAG, " newsItemList first item: " + newsItemList.get(0).toString());
//
//                    Log.d(TAG, "onRestoreInstanceState() - list data obtained! List size: " + newsItemList.size());
//
//                    ListView lv = (ListView) findViewById(R.id.listView);
//                    Log.d(TAG, "onRestoreInstanceState() - listView identified");
//                    NewsAdapter adapter = new NewsAdapter(this, R.id.listView, newsItemList);
//                    Log.d(TAG, "onRestoreInstanceState() - NewsAdapter populated");
//                    lv.setAdapter(adapter); //TODO: fix crash here - 'int java.util.List.size()' on a null object reference
//                    Log.d(TAG, "onRestoreInstanceState() - NewsAdapter set into listView!");
//                }
//            }
    }
}
