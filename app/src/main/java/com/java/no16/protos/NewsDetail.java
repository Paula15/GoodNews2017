package com.java.no16.protos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * Data structure providing detailed news information.
 */
public class NewsDetail {

    @SerializedName("news_Title")
    @Expose
    private String title;

    @SerializedName("news_Author")
    @Expose
    private String author;

    @SerializedName("news_Time")
    @Expose
    private String date;

    @SerializedName("news_Content")
    @Expose
    private String content;

    private boolean favorite;

    @SerializedName("news_Pictures")
    @Expose
    private String imageUrlString;

    private List<String> imageUrls;

    @SerializedName("news_ID")
    @Expose
    private String newsId;

    public NewsDetail(String title, String author, String date, String content, boolean favorite, List<String> imageUrls, String newsId) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.content = content;
        this.favorite = favorite;
        this.imageUrls = imageUrls;
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImages(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /** Separates imageUrlString into a list of image url. */
    public void separateImageUrlString() {
        imageUrls = Arrays.asList(imageUrlString.trim().split(";| "));
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
