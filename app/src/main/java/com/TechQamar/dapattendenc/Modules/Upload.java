package com.TechQamar.dapattendenc.Modules;

import android.location.Address;

import java.util.List;

public class Upload {
    private String address;
    private String mImageUrl;
    public String date;
    public String name;
    public String time;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String address, String mImageUrl, String date, String name, String time) {
        this.address = address;
        this.mImageUrl = mImageUrl;
        this.date = date;
        this.name = name;
        this.time = time;
    }

    public Upload(List<Address> address, String toString, String date, String name, String time) {

    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }



}