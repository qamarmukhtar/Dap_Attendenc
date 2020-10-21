package com.TechQamar.dapattendenc.Modules;

public class Users {


    public String name;
    public String email;
    private String password;
    public String mobile;


    public Users(){

    }

    public Users(String name, String email, String password, String mobile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
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