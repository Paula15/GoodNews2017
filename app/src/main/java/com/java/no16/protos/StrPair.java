package com.java.no16.protos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/** String pair used for parsing json */
public class StrPair {

    @SerializedName("word")
    @Expose
    private String key;

    @SerializedName("count")
    @Expose
    private int value;

    public StrPair(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
