package com.TechQamar.dapattendenc.Modules;

public class userdata {

    public String name;
    public String email;
    public String mobile;
    public String password;


    public userdata(){

    }

    public userdata(String name, String email, String password, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

}