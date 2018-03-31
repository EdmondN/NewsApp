
package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;

/**
 * An {@link News} object contains information related to a single News.
 */
public class News {
    private String mTitle;
    private String mAuthor;
    private String mSectionName;
    private String mDate;
    private Bitmap thumbnail;
    private String mUrl;

    public News(String title, String sectionName, String author, String date, String url, Bitmap thumbnail) {
        mTitle = title;
        mAuthor = author;
        mSectionName = sectionName;
        this.thumbnail = thumbnail;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the Title of the news.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the Author name of the news.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the SectionName of the news.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the date of the news.
     */
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    /**
     * Returns the date of the news.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the URL of the news.
     */
    public String getUrl() {
        return mUrl;
    }
}