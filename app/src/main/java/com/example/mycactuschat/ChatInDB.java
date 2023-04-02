package com.example.mycactuschat;

import android.net.Uri;

import java.util.Date;

public class ChatInDB {
    public String id;
    public String sender;
    public String text;
    public String time;

    public ChatInDB(String id, String sender, String text, String time) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.time = time;
    }

    public ChatInDB() {

    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
