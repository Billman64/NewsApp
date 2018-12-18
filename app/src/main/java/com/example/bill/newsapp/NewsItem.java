package com.example.bill.newsapp;

public class NewsItem {
    String mTitle;
    String mPubDate;
    String mAuthor;
    String mUrl;

    public NewsItem(String mTitle, String mPubDate, String mAuthor, String mUrl) {
        this.mTitle = mTitle;
        this.mPubDate = mPubDate;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
    }

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

    public void setPubDate(String mPubDate) {
        this.mPubDate = mPubDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
