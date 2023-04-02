package com.example.mycactuschat;

public class SimpleMessageView {
    public String simpleText;
    public String simpleTime;

    public SimpleMessageView(String simpleText, String simpleTime) {
        this.simpleText = simpleText;
        this.simpleTime = simpleTime;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public String getSimpleTime() {
        return simpleTime;
    }
}
