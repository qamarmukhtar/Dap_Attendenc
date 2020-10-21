package com.TechQamar.dapattendenc.Modules;

import java.io.Serializable;


public class SingleProductModel implements Serializable {

    private String address, date, mImageUrl, name, time;

    public SingleProductModel() {
    }

    public SingleProductModel(String address, String date, String mImageUrl, String name, String time) {
        this.address = address;
        this.date = date;
        this.mImageUrl = mImageUrl;
        this.name = name;
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }
}