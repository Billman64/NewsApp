package com.example.bill.newsapp;

/*
    News app
    Created by Bill Lugo for Udacity course. 11/21/18
    Revised 12/18
    News source is Guardian. Any news content displayed comes from and belongs to them.
 */

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {
    public static final int LOADER_ID = 1;

    public ArrayList newsItemList = new ArrayList<NewsItem>();

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int loaderId, Bundle args) {
        try{
            URL url = new URL(createURI().toString());
            return new NewsLoader(this,url);
        } catch (MalformedURLException e) {
            Log.e(getString(R.string.tag),"onCreateLoader(): MalformedURL " + e);
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItemList){
        Log.d(getString(R.string.tag), "AsyncTaskLoader onLoadFinished()");

        // clear output textView, indicating that the network call is done
        TextView output = (TextView) findViewById(R.id.output);
        output.setText("");

        // if no news items found, display an message, as a newsItem itself in the listView
        if(newsItemList.isEmpty()) {
           newsItemList.add(new NewsItem(getString(R.string.no_result),"--","--", "--"));
        }

        // update listView
        ListView lv = (ListView) findViewById(R.id.listView);
//        ArrayAdapter<NewsItem> adapter  = new ArrayAdapter<>(this, R.layout.news_item, newsItemList);
        NewsAdapter adapter = new NewsAdapter(this, 0, newsItemList);
        lv.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LoaderManager loaderManager = getLoaderManager();
//        loaderManager.initLoader(LOADER_ID, null, this);

        Button button = (Button) findViewById(R.id.button);
        final TextView output = (TextView) findViewById(R.id.output);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieve news via API call in a worker thread
                Log.d("NewsApp", "onClick() activated");
                output.setText(R.string.searching);
                getNews();
                Log.d("NewsApp", "onClick() done");


                // test listView output
//                sampleData();
            }
        });
    }


    private void sampleData(){
        ListView lv = (ListView) findViewById(R.id.listView);
//        ArrayAdapter<NewsItem> adapter  = new ArrayAdapter<>(this, R.layout.news_item, newsItemList);
        newsItemList.add(new NewsItem("title","publication data", "author", "url"));
        NewsAdapter adapter = new NewsAdapter(this, 0, newsItemList);
        lv.setAdapter(adapter);
    }


    private void getNews(){
        try {
            LoaderManager loaderManager = getLoaderManager();
            Log.d("NewsApp", "loaderManager created.");


            Loader<NewsItem> loader = loaderManager.getLoader(LOADER_ID);

            if(loader == null){
                loaderManager.initLoader(LOADER_ID, null, this);
                Log.d("NewsApp", "loaderManaager initialized. Loader_ID: " + LOADER_ID + ".");
            } else {
                loaderManager.restartLoader(LOADER_ID, null, this);
            }
            new NewsLoader(getApplicationContext(), new URL(createURI().toString()));   //TODO fix AsyncTaskLoader not executing

        } catch (MalformedURLException e) {
            Log.e(getString(R.string.tag),"onClick() Malformed URL in NewsLoader: " + e);
        }
    }

    private Uri.Builder createURI() {
        Uri.Builder uri = new Uri.Builder();
        try{
            uri.scheme("http");
            uri.authority("content.guardianapis.com");
            uri.appendPath("search");

            EditText et = (EditText) findViewById(R.id.et);
            uri.appendQueryParameter("q", et.getText().toString());     //TODO: error-trap for spaces and other special characters in input, ie: "%20" unicode
            uri.appendQueryParameter("api-key","ad802560-e4de-4aea-9286-8a462045964d");
            uri.appendQueryParameter("show-tags","contributor");
            //TODO: implement fallback query with test api key     uri.appendQueryParameter("api-key","test");
            //TODO: secure key by hiding it in lower level (C/C++)

            uri.build();
            Log.d(getString(R.string.tag), "Uri Builder: " + uri.toString());
        } catch(Exception e) {
            Log.e(getString(R.string.tag),getString(R.string.error_malformed_url));
            return null;
        }
        return uri;
    }

}
