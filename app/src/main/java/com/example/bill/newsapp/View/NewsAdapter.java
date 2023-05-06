package com.example.bill.newsapp.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bill.newsapp.Model.NewsItem;
import com.example.bill.newsapp.R;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    public String TAG = "NewsAdapter";

    public NewsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        // fill in individual textViews
        final NewsItem currentNewsItem = getItem(position);
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNewsItem.getTitle());

        // display date and time, making it human-readable
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        String [] dateAndTime = currentNewsItem.getPubDate().split("T");
        date.setText(dateAndTime[0] + " " + dateAndTime[1]);

        TextView section = (TextView) listItemView.findViewById(R.id.section);
        section.setText(currentNewsItem.getSection());

        TextView author = (TextView) listItemView.findViewById(R.id.author);
        author.setText(currentNewsItem.getAuthor());

        TextView preview = (TextView) listItemView.findViewById(R.id.preview);
        preview.setText(currentNewsItem.getPreview());

        TextView urlTv = (TextView) listItemView.findViewById(R.id.url);
        urlTv.setText(currentNewsItem.getUrl());

        urlTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNewsItem.getUrl()));
                getContext().startActivity(i);
            }
        });

        // implement onClickListener to make each newsItem clickable using newsItem's url
        listItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNewsItem.getUrl()));

                Log.d(TAG, " this: " + this.toString());
                Log.d(TAG, " parent.getContext(): " + parent.getContext().getClass().getName());
                Log.d(TAG, " ArticleView.class: " + ArticleView.class.getName());

                try {
                Intent i = new Intent(view.getContext(), ArticleView.class);  //???
                    //TODO: intent for article view screen (activity?)
//                Intent i = new Intent(this, ArticleView.class);
                    i.putExtra("title", currentNewsItem.getTitle());
                    i.putExtra("date", currentNewsItem.getPubDate());
                    i.putExtra("section", currentNewsItem.getSection());
                    i.putExtra("preview", currentNewsItem.getPreview());
                    i.putExtra("author", currentNewsItem.getAuthor());
                    i.putExtra("url", currentNewsItem.getUrl());
                    Log.d(TAG, " intent testing: " + i.getExtras().getString("url"));
                    view.getContext().startActivity(i);    //TODO: fix bug triggered here
                } catch(Exception e){
                    Log.e(TAG," intent error cause: " + e.getCause());
                    Log.e(TAG, " intent error msg: " + e.getMessage());
                }


            }
        });

        return listItemView;
    }
}
