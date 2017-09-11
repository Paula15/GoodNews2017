package com.java.no16.protos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data structure providing a list of {@link SimpleNews}
 */
public class SimpleNewsList {

    @SerializedName("list")
    @Expose
    private List<SimpleNews> simpleNewsList;

    public SimpleNewsList(List<SimpleNews> simpleNewsList) {
        this.simpleNewsList = simpleNewsList;
    }

    public void setSimpleNewsList(List<SimpleNews> simpleNewsList) {
        this.simpleNewsList = simpleNewsList;
    }

    public List<SimpleNews> getSimpleNewsList() {
        return simpleNewsList;
    }

    public int getSimpleNewsCount() {
        if (simpleNewsList == null) {
            return 0;
        }
        return simpleNewsList.size();
    }
}
