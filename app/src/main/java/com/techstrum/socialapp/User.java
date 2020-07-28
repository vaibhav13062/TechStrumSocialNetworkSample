package com.techstrum.socialapp;


import android.net.Uri;

import com.google.android.gms.tasks.Task;

import java.net.URL;

public class User {
    public String name ,email,phone,date,gender,city,country,pinCode,address;
    public Integer uploads;



    public User(){

    }



    public String getName() {
        return name;
    }

    public User(String name, String email, String phone, String date, String gender, String city, String country, String pinCode, String address, Integer uploads ){
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.date=date;
        this.gender=gender;
        this.city=city;
        this.country=country;
        this.pinCode=pinCode;
        this.address=address;
        this.uploads=uploads;


    }
}

