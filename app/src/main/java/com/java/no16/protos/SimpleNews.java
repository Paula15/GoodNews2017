package com.java.no16.protos;

import java.io.Serializable;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class SimpleNews implements Serializable {
    private String newsId;
    private String title;
    private String author;
    private String date;
    private String description;
    private String imageUrl;
    private boolean mark;

    public SimpleNews(String newsId, String title, String author, String date, String description, String imageUrl, boolean mark) {
        this.newsId = newsId;
        this.title = title;
        this.author = author;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
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
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isMark() {
        return mark;
    }
}
