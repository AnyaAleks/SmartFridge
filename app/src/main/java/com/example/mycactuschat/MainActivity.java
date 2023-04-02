package com.example.mycactuschat;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    UserSettings userSettings = new UserSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//Токен для физического устройства для рассылок
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.i("TOKENMESSAGE", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.i("TOKENMESSAGE", token);
                    }
                });

//        long currentMilliseconds = new Date().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YY");
//        Toast.makeText(this, dateFormat.format(currentMilliseconds), Toast.LENGTH_SHORT).show();

        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // initialise the layout
        WebView webView = (WebView) findViewById(R.id.webvidew);
        webView.loadUrl("file:///android_asset/cactus_dancing.html");
        webView.setBackgroundColor(Color.TRANSPARENT);

        loadJsonData();

        if (userSettings == null) {

        }
        else{
            //выставление настроек пользователя цвет темы
            if(userSettings.getModeUser()==1){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            //выставление настроек пользователя локализация
            if(userSettings.getLocalisationUser()==1){
                Locale locale2 = new Locale("en");
                Locale.setDefault(locale2);
                Configuration config2 = getBaseContext().getResources().getConfiguration();
                config2.locale = locale2;
                getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
            }
            else{
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            }
        }




        Intent intent = new Intent(this, SignInActivity.class);

        //таймер
        CountDownTimer timer = new CountDownTimer(3000, 1000) //3 second Timer
        {
            public void onTick(long l)
            {

            }

            @Override
            public void onFinish()
            {
                startActivity(intent);
                finish();
            };
        }.start();
    }

    //Доставание из настроек
    public void loadJsonData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user settings", null);
        userSettings = gson.fromJson(json, UserSettings.class);

        if (userSettings == null) {
            Toast.makeText(this, "Empty Settings!", Toast.LENGTH_SHORT).show();
            //userSettings = new UserSettings(userId);
        }
    }
}