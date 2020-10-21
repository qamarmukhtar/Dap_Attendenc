package com.TechQamar.dapattendenc.Modules;

public class UserInformation {

    public String user_Name;
    public String latitude;
    public String longitude;

    public UserInformation(){               //default constructor which invokes on object creation of respective class in MainActivity.java

    }

    public UserInformation(String user_Name, String latitude, String longitude){    //parameterized constructor which will store the retrieved data from firebase
        this.user_Name=user_Name;
        this.latitude=latitude;
        this.longitude=longitude;
    }
}