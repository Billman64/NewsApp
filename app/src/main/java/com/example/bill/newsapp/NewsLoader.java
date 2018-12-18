package com.example.bill.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {
    String mUrl;

    public NewsLoader(Context context, URL url) {
        super(context);
        mUrl = url.toString();
    }

    protected void onStartLoading(){
        Log.d(getContext().getString(R.string.tag), "AsyncTaskLoader onStartLoading().");
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        Log.d(getContext().getString(R.string.tag), "AsyncTaskLoader loadInBackground().");

        if(mUrl==null) return null;

        HTTPHandler hh = new HTTPHandler();

        try{
            // make request to the endpoint's URL
            String rawJson = hh.startHttpRequest(new URL(mUrl));
            if(rawJson == null) {
                Log.d("NewsApp ---", "HTTPHandler done. rawJson is null");
                return null;
            } else {
                Log.d("NewsApp ---", "HTTPHandler done. rawJson: " + rawJson.substring(0,50) + "...");


                JSONObject jsonRoot = new JSONObject(rawJson);
                Log.d(getContext().getString(R.string.tag),"jsonRoot: " + jsonRoot.toString().substring(0,100) + "..." );

                // create JSON objects to traverse to general data area
                JSONObject jsonObjectResponse = jsonRoot.optJSONObject("response");
                Log.d("NewsApp ----", "jsonObjectResponse: " + jsonObjectResponse.toString().substring(0,100));
                JSONArray jsonArrayResults = jsonObjectResponse.getJSONArray("results");
                Log.d(getContext().getString(R.string.tag),"jsonObjectResponse created with length: " + jsonObjectResponse.length() );

                // create a temporary newsItem list to return
                List<NewsItem> newsItemList = new ArrayList<NewsItem>();

                // loop through records
                for(int i=0; i < jsonArrayResults.length(); i++) {
                    // create individual JSON objects and arrays to traverse to specific data points
                    JSONObject j = jsonArrayResults.getJSONObject(i);
                    String pubDate = j.optString("webPublicationDate");
                    String webTitle = j.optString("webTitle");
                    String section = j.optString("sectionName");
                    String articleUrl = j.optString("webUrl");

                    JSONArray jsonArrayTags = j.optJSONArray("tags");
                    JSONObject jsonObjectTag = jsonArrayTags.optJSONObject(0);

                    String author = jsonObjectTag.optString("webTitle");
                    Log.d(getContext().getString(R.string.tag), "news: " + webTitle);    // it could be fun or funny to see a news headline in a logcat log

                    // put each JSON record into the newsItem list
                    newsItemList.add(new NewsItem(webTitle, pubDate, section, author, articleUrl));
                }
                return newsItemList;
            }

            } catch (final Exception e) {
                Log.e(getContext().getString(R.string.tag), "Parse, or other, exception: " + e.getMessage().substring(0,100) );

                //TODO: differentiate between different exceptions, such as malformedURL or ParseException

                return null;
            }

    }
}
