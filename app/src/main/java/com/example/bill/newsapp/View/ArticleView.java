package com.example.bill.newsapp.View;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.PersistableBundle;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;

        import com.example.bill.newsapp.R;

public class ArticleView extends AppCompatActivity {
    public String TAG = "ArticleView";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    // persistentState prevents activity from running
        setContentView(R.layout.activity_article_view);

        //TODO: load whole article (separate network request)

        Log.d(TAG, " starting onCreate() of ArticleView");

        String title = getIntent().getExtras().getString("title");
        String date = getIntent().getExtras().getString("date");
        String section = getIntent().getExtras().getString("section");
        String preview = getIntent().getExtras().getString("preview");
        String author = getIntent().getExtras().getString("author");
        final String url = getIntent().getExtras().getString("url");

        Log.d(TAG, "title: " + title);

        TextView titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(title);
        TextView tagTv = (TextView) findViewById(R.id.tag);
        tagTv.setText(section); //TODO: fix inconsistency between tag and section
        TextView dateTv = (TextView) findViewById(R.id.date);
        dateTv.setText(date);
        TextView articleTv = (TextView) findViewById(R.id.article_text);
        articleTv.setText(preview);
        TextView authorTv = (TextView) findViewById(R.id.author);
        authorTv.setText(author);
        TextView urlTv = (TextView) findViewById(R.id.url);
        urlTv.setText(url);


        urlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });


        Log.d(TAG, "intent info. Title: " + title + date + section + preview.substring(0,10) + author + url);
    }
}
