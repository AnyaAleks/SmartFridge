package com.example.mycactuschat;

import android.net.Uri;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Chat {
    public String message;
    public int messageType;
    public String messageTime;
    public String messageUserImage="123456";

    // Constructor for current user (receive)
    public Chat(String message, int messageType, String messageTime) {
        this.message = message;
        this.messageType = messageType;
        this.messageTime = messageTime;
        //messageTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    // Constructor for another user (sender)
    public Chat(String message, int messageType, String messageTime, String messageUserImage) {
        this.message = message;
        this.messageType = messageType;
        this.messageTime = messageTime;
        this.messageUserImage = messageUserImage;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
