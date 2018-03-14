
package com.example.android.newsapp;

/**
 * An {@link News} object contains information related to a single News.
 */
public class News {
    private String mTitle;
    private String mAuthor;
    private String mSectionName;
    private String mDate;
    private String mThumbnail;
    private String mUrl;

    public News(String title, String sectionName, String author, String date, String url, String thumbnail ) {
        mTitle = title;
        mAuthor = author;
        mSectionName = sectionName;
        mThumbnail = thumbnail;
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
    public String getAuthor() {return mAuthor;}

    /**
     * Returns the SectionName of the news.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the date of the news.
     */
    public String getThumbnail() {
        return mThumbnail;
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