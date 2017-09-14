package com.java.no16.ui.newslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.CacheService;
import com.java.no16.service.GetNewsDetailService;
import com.java.no16.service.GetNewsListService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private Context mContext;
    private List<SimpleNews> mNewsList;
    private Category mCategory;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView newsTitleTV;
        public TextView newsDescriptionTV;
        public ImageView newsIV;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            newsTitleTV = (TextView) v.findViewById(R.id.news_title);
            newsDescriptionTV = (TextView) v.findViewById(R.id.news_description);
            newsIV = (ImageView) v.findViewById(R.id.news_image);
        }
    }

    public NewsListAdapter(Context context, List<SimpleNews> newsList, Category category) {
        this.mContext = context;
        this.mNewsList = newsList;
        this.mCategory = category;
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
        final SimpleNews simpleNews = mNewsList.get(position);
        holder.newsTitleTV.setText(mNewsList.get(position).getTitle());
        holder.newsDescriptionTV.setText(mNewsList.get(position).getDescription());
        if (CacheService.isNight()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
            holder.newsTitleTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.newsDescriptionTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            holder.newsTitleTV.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            holder.newsDescriptionTV.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        if (simpleNews.isMark()) {
            int colorGrey = ContextCompat.getColor(mContext, R.color.grey);
            holder.newsTitleTV.setTextColor(colorGrey);
        }

        // Image stuffs
        Glide.clear(holder.newsIV);
        if (CacheService.isShowPicture()) {
                Glide.with(holder.newsIV.getContext())
                        .load(simpleNews.getImageUrl())
                        .centerCrop()
                        .into(holder.newsIV);
            if (simpleNews.getImageUrl() == null) {
                Glide.clear(holder.newsIV);
                final ViewHolder vh = holder;
                final Handler handler = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        super.handleMessage(msg);
                        vh.newsIV.setImageBitmap((Bitmap) msg.obj);

                        //Log.e("handleMessage", "msg");
                    };
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = GetNewsDetailService.getImage(
                                    GetNewsListService.getMissedImage(simpleNews.getTitle()));

                            Message message = Message.obtain();
                            message.obj = bitmap;

                            handler.sendMessage(message);
                        } catch (Exception e) {
                            Log.e("failed", "failed");
                        }
                    }
                }).start();
            }
//                if (simpleNews.getImageUrl() == null) {
//                Future<String> future = Executors.newSingleThreadExecutor().submit(new Callable<String>() {
//                    @Override
//                    public String call() throws Exception {
//                        String queryStr = simpleNews.getTitle().length() < 5 ?
//                                simpleNews.getTitle() : simpleNews.getTitle().substring(0, 5);
//                        return GetNewsListService.getMissedImage(queryStr);
//                    }
//                });
//                try {
//                    String imageUrl = future.get();
//                    Glide.with(holder.newsIV.getContext())
//                            .load(imageUrl)
//                            .centerCrop()
//                            .into(holder.newsIV);
//                } catch (Exception e) {
//                    Log.e("FAILED", "failed to get missing image.");
//                }
//            }
        }
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
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "update!!!!!");
    }

    public void addData(List<SimpleNews> newsList) {
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mNewsList.clear();
        notifyDataSetChanged();
    }
}
