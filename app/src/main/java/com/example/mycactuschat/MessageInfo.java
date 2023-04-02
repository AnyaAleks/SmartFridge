package com.example.mycactuschat;

import android.net.Uri;

import java.util.Date;

public class MessageInfo {
    public String idChat;
    public String idFirstUser;
    public String idSecondUser;

    // Constructor
    public MessageInfo(String idFirstUser, String idSecondUser, String idChat) {
        this.idFirstUser = idFirstUser;
        this.idSecondUser = idSecondUser;
        this.idChat = idChat;
    }

    public MessageInfo() {

    }

    public String getIdFirstUser() {
        return idFirstUser;
    }

    public String getIdSecondUser() {
        return idSecondUser;
    }

    public String getIdChat() {
        return idChat;
    }
}
