package com.example.bill.newsapp.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bill.newsapp.Model.NewsItem;
import com.example.bill.newsapp.R;

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

        // implement onClickListener to make each newsItem clickable using newsItem's url
        listItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNewsItem.getUrl()));
                getContext().startActivity(i);
            }
        });

        return listItemView;
    }
}
