package com.example.mycactuschat;

public class ModelChat {
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDelegate() {
        return delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    String receiver;

    public ModelChat() {
    }

    String sender;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ModelChat(String message, String receiver, String sender, String type, String timestamp, boolean delegate) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.timestamp = timestamp;
        this.delegate = delegate;
    }

    String type;
    String timestamp;
    boolean delegate;
}
