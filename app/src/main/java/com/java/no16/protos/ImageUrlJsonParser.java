package com.java.no16.protos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Data structure providing image url parsing from Baidu image search result. */
public class ImageUrlJsonParser {

    class ObjUrl {

        @SerializedName("objURL")
        @Expose
        private String objUrl;

        String getUrl() {
            return objUrl;
        }
    }

    @SerializedName("imgs")
    @Expose
    private List<ObjUrl> imgUrl;

    public String getUrl() {
        return imgUrl.get(0).getUrl();
    }
}
