package com.example.mycactuschat;

import android.net.Uri;

import java.io.Serializable;

public class Contacts implements Serializable {
    private String name, surname, id, phone, email, icon;

    public Contacts(String email, String icon, String id, String name, String phone, String surname){
        this.icon=icon;
        this.name=name;
        this.surname=surname;
        this.id=id;
        this.phone=phone;
        this.email=email;
    }

    public Contacts(){

    }

    public String getIconC(){return icon;}

    public String getNameC() {
        return name;
    }

    public String getSurnameC() {
        return surname;
    }

    public String getIdC() {
        return id;
    }

    public String getPhoneC() {
        return phone;
    }

    public String getEmailC() {
        return email;
    }
}
