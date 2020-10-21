package com.TechQamar.dapattendenc.Modules;

public class LatLong {

    String Latitude;
    String Longitude;
    String UserName;

    public LatLong(String latitude, String longitude,String userName) {
        Latitude = latitude;
        Longitude = longitude;
        UserName =  userName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getUser_Name() {
        return UserName;
    }

    public void setUser_Name(String userName) {
        UserName = userName;
    }
}
