package com.example.bill.newsapp.Model;

public class NewsItem {
    String mTitle;
    String mPubDate;
    String mSection;
    String mAuthor;
    String mUrl;

    public NewsItem(String Title, String PubDate, String section, String Author, String Url) {
        mTitle = Title;
        mPubDate = PubDate;
        mSection = section;
        mAuthor = Author;
        mUrl = Url;
    }

    public String getSection() { return mSection; }

    public void setSection(String section) { mSection = section; }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getUrl() {
        return mUrl;
    }
}