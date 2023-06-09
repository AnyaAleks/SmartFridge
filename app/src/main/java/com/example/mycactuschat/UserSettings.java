package com.example.mycactuschat;

import android.net.Uri;

import java.util.Date;

public class UserSettings {
    public String idUser;
    public int modeUser;
    public int savingPhotoUser;
    public int notificationUser;
    public int localisationUser;
    public String passwordUser;

    public UserSettings(String idUser, String passwordUser){
        this.idUser = idUser;
        this.passwordUser = passwordUser;
        this.modeUser=1; //светлая тема
        this.localisationUser=1; //английская локализация
        this.savingPhotoUser=1; //автоматическое сохранение фоток в галерею
        this.notificationUser=1; //разрешен приход сообщений
    }

    public UserSettings(){
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public void setModeUser(int modeUser) {
        this.modeUser = modeUser;
    }

    public void setLocalisationUser(int localisationUser) {
        this.localisationUser = localisationUser;
    }

    public void setNotificationUser(int notificationUser) {
        this.notificationUser = notificationUser;
    }

    public void setSavingPhotoUser(int savingPhotoUser) {
        this.savingPhotoUser = savingPhotoUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public int getModeUser() {
        return modeUser;
    }

    public int getLocalisationUser() {
        return localisationUser;
    }

    public int getNotificationUser() {
        return notificationUser;
    }

    public int getSavingPhotoUser() {
        return savingPhotoUser;
    }
}
