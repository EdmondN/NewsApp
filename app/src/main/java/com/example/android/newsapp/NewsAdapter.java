package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, List<News> News) {
        super(context, 0, News);
    }

    // Create class ViewHolder for efficient memory usage
    static class ViewHolder {
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDataTextView;
        private ImageView mThumbnailImageView;
        private TextView mSectionTextView;
    }

    /**
     * Returns a list item view that displays information about the news at the given position
     * in the list of news.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
            holder = new ViewHolder();
            holder.mTitleTextView = convertView.findViewById(R.id.title);
            holder.mAuthorTextView = convertView.findViewById(R.id.author);
            holder.mDataTextView = convertView.findViewById(R.id.date);
            holder.mSectionTextView = convertView.findViewById(R.id.section);
            holder.mThumbnailImageView = convertView.findViewById(R.id.thumbnail_image_card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        // Find the news at the given position in the list of news
        News currentNews = getItem(position);
        if (currentNews.getThumbnail() != null) {
            holder.mThumbnailImageView.setImageBitmap(currentNews.getThumbnail());
        }
        // Display the INFO of the current news in that all TextView
        holder.mTitleTextView.setText(currentNews.getTitle());
        holder.mAuthorTextView.setText(currentNews.getAuthor());
        holder.mDataTextView.setText(currentNews.getDate());
        holder.mSectionTextView.setText(currentNews.getSectionName());
        // Return the list item view that is now showing the appropriate data
        return convertView;
    }
}