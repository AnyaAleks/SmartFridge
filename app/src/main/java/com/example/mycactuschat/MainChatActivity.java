package com.example.mycactuschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainChatActivity extends AppCompatActivity {


    Menu optionMenu;

    FirebaseFirestore db;
    UserSettings userSettings = new UserSettings();
    NotificationManager notificationManager;

    Map<String,Integer> countMessagesInChatMap=new HashMap<>();

    //Таймер обновлений нахождения в приложении
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isForeground(MainChatActivity.this)){
                //Toast.makeText(MainChatActivity.this, "TRUE", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(MainChatActivity.this, "FALSE", Toast.LENGTH_SHORT).show();
                loadFromBDChat();
            }

            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        db = FirebaseFirestore.getInstance();
        loadJsonData();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel("fcm_default_channel",0);

        //Start timer
        ///loadFromBDChat();
        handler.postDelayed(runnable, 5000);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_purple));
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        BottomNavigationView navigation=(BottomNavigationView) findViewById(R.id.botttom_navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        //Появление пункта меню messages
        loadFragment(MessagesFragment.newInstanse());
        navigation.setSelectedItemId(R.id.pageMessage);
        //getSupportActionBar().setTitle("M E S S A G E S");

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //Выбор пункта меню NavigationBarView
    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.pageContacts:
                    loadFragment(ContactsFragment.newInstanse());
                    getSupportActionBar().setTitle(R.string.contacts_caps);
                    return true;
                case R.id.pageMessage:
                    loadFragment(MessagesFragment.newInstanse());
                    getSupportActionBar().setTitle(R.string.messages_caps);
                    return true;
                case R.id.pageProfile:
                    loadFragment(ProfileFragment.newInstanse());
                    getSupportActionBar().setTitle(R.string.profile_caps);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment){
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//
//        optionMenu=menu;
//
//        MenuItem searchItem = menu.findItem(R.id.actionSearch); //item
//        SearchView searchView = (SearchView) searchItem.getActionView(); // of item
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) { //Вызывается, когда пользователь отправляет запрос
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                if(!newText.isEmpty()) {
////                    filterMenu(newText.toLowerCase());
////                } else {
////                    categoryAdapter.updateCA(itemCategoryList);
////                }
//                return false;
//
//            }
//        });
//        return true;
//    }



    public void gotoCoversationActivity(View v){
        switch (v.getId()) {
            case R.id.buttonAddChat:
                Toast.makeText(MainChatActivity.this, R.string.future_update, Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(this, ConversationActivity.class);
//                startActivity(intent);


//                CollectionReference citiesRef = db.collection("cities");
//                citiesRef.whereArrayContains("regions", "west_coast");
//
//                Map<String,Object> userM = new HashMap<>();
//                userM.put("eee", "nameText");
//                userM.put("rrr", "surnameText");
//                userM.put("ttt", "phoneText");
//
//                Task<Void> messageRef = db.collection("rooms").document("roomA").collection("messages").document("message1").set(userM);

                //finish();
                break;
        }
    }

    //Проверка на закрытое приложение
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void loadFromBDChat(){
        loadJsonData();
        Map<String,Integer> currentCountMessagesInChatMap=new HashMap<>(countMessagesInChatMap);

        db.collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageInfo data = new Gson().fromJson(document.getData().toString(), MessageInfo.class);

                                db.collection("Messages").document(data.idChat).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Integer counMessager=0;
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot d : list) {
                                                //ChatInDB c = d.toObject(ChatInDB.class);
                                                counMessager+=1;
                                            }
                                            countMessagesInChatMap.put(data.idChat, counMessager);
//
//                                            Log.w("HOWMACH-CURRENT", String.valueOf(currentCountMessagesInChatMap.get(data.idChat)));
//                                            Log.w("HOWMACH-FIRST", String.valueOf(countMessagesInChatMap.get(data.idChat)));

                                            if(!String.valueOf(currentCountMessagesInChatMap.get(data.idChat)).equals("null")){
                                                if(countMessagesInChatMap.get(data.idChat) != currentCountMessagesInChatMap.get(data.idChat)){

                                                    //поиск имени того кто отправил
                                                    String idAnotherUser="";
                                                    String dataUserF = data.getIdFirstUser() +"";
                                                    String dataUserS = data.getIdSecondUser() +"";
                                                    if(dataUserF.equals(userSettings.getIdUser().toString()) || dataUserS.equals(userSettings.getIdUser().toString())) {
                                                        if(dataUserF.equals(userSettings.getIdUser().toString())){
                                                            idAnotherUser=dataUserS;
                                                        }
                                                        if(dataUserS.equals(userSettings.getIdUser().toString())){
                                                            idAnotherUser=dataUserF;
                                                        }
                                                        String finalIdAnotherUser = idAnotherUser;

                                                        db.collection("Clients")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                Contacts dataClient = new Gson().fromJson(document.getData().toString(), Contacts.class);

                                                                                if(userSettings.getNotificationUser()==1) {
                                                                                    if (dataClient.getIdC().equals(finalIdAnotherUser)) {
                                                                                        sendNotification((dataClient.getNameC() + " " + dataClient.getSurnameC()));
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        } else {
                                            //Toast.makeText(MainChatActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            //Log.w("HOWMACH", countMessagesInChatMap.toString(), task.getException());
                        } else {
                            //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                            Log.w("HOWMACH", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(messageBody)
                        .setSmallIcon(R.drawable.ic_stat_notification_sailing)
                        .setContentText("You have new message ✈ ✉")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    //Доставание из настроек
    public void loadJsonData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user settings", null);
        userSettings = gson.fromJson(json, UserSettings.class);

        if (userSettings == null) {
            Toast.makeText(this, "Empty Settings!", Toast.LENGTH_SHORT).show();
            //userSettings = new UserSettings(userId);
        }
    }
}