package com.example.bill.newsapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable {
    String mTitle;
    String mPubDate;
    String mSection;
    String mAuthor;
    String mUrl;
    String mPreview;

    public NewsItem(String Title, String PubDate, String section, String Author, String Url, String Preview) {
        mTitle = Title;
        mPubDate = PubDate;
        mSection = section;
        mAuthor = Author;
        mUrl = Url;
        mPreview = Preview;
    }

    protected NewsItem(Parcel in) {
        mTitle = in.readString();
        mPubDate = in.readString();
        mSection = in.readString();
        mAuthor = in.readString();
        mUrl = in.readString();
        mPreview = in.readString();
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

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

    public String getPreview(){ return mPreview; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mPubDate);
        parcel.writeString(mSection);
        parcel.writeString(mAuthor);
        parcel.writeString(mUrl);
        parcel.writeString(mPreview);
    }
}