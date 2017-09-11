package com.java.no16.protos;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure providing simple news information.
 */
public class SimpleNews implements Serializable {
    @SerializedName("news_ID")
    @Expose
    private String newsId;

    @SerializedName("news_Title")
    @Expose
    private String title;

    @SerializedName("news_Author")
    @Expose
    private String author;

    @SerializedName("news_Time")
    @Expose
    private String time;

    @SerializedName("news_Intro")
    @Expose
    private String description;

    /** A single string providing all image urls, separating by ';'. */
    @SerializedName("news_Pictures")
    @Expose
    private String imageUrls;

    private @Nullable String imageUrl;

    private boolean mark;

    private boolean showImage;

    public SimpleNews(String newsId, String title, String author, String date, String description, String imageUrls, boolean mark) {
        this.newsId = newsId;
        this.title = title;
        this.author = author;
        this.time = date;
        this.description = description;
        this.imageUrls = imageUrls;
        this.mark = mark;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.time = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getNewsId() {
        return newsId;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public @Nullable String getImageUrl() {
        return imageUrl;
    }

    public boolean isMark() {
        return mark;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    /** Separates image urls into a list and set imageUrl with the first one. */
    public void separateImageUrl() {
        List<String> images = Arrays.asList(imageUrls.trim().split(";|\\s+"));
        if (images.size() == 0 || (images.size() == 1 && (images.get(0) == null || images.get(0).isEmpty()))) {
            imageUrl = null;
        } else {
            imageUrl = images.get(0);
        }
    }
}
