package com.java.no16.ui.newslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.no16.R;
import com.java.no16.protos.SimpleNews;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private List<SimpleNews> mNewsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleTV;
        public ImageView newsIV;

        public ViewHolder(View v) {
            super(v);
            newsTitleTV = (TextView) v.findViewById(R.id.news_title);
            newsIV = (ImageView) v.findViewById(R.id.news_image);
        }
    }

    public NewsListAdapter(Context context, List<SimpleNews> newsList) {
        this.mNewsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newslist_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleNews simpleNews = mNewsList.get(position);
        holder.newsTitleTV.setText(mNewsList.get(position).getTitle());
        Glide.clear(holder.newsIV);
        Log.e("ErrorImg", simpleNews.getImageUrl());
        Glide.with(holder.newsIV.getContext())
                .load(simpleNews.getImageUrl())
                .centerCrop()
                .into(holder.newsIV);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public SimpleNews getItem(int position) {
        return mNewsList.get(position);
    }

    public void updateData(List<SimpleNews> newsList) {
        mNewsList.clear();
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }

    public void addData(List<SimpleNews> newsList) {
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }
}
