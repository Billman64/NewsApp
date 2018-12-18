package com.example.bill.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    public NewsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        // fill in individual textViews
        final NewsItem currentNewsItem = getItem(position);
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNewsItem.getTitle());
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        date.setText(currentNewsItem.getPubDate());     //TODO: make date user-friendly (maybe extract just the date from the timestamp).
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        author.setText(currentNewsItem.getAuthor());

        //TODO: implement onClickListener to make each newsItem clickable using newsItem's url

        return listItemView;
    }
}
