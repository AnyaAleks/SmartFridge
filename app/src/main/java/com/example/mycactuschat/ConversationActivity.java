package com.example.mycactuschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class ConversationActivity extends AppCompatActivity {

    MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
    final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

    //Отправление в RV смс
    RecyclerView recyclerView;
    ArrayList<Chat> messagesList = new ArrayList<>();
    View view;

    String guid;
    Integer flagIsPhoto=0;
    Integer flagIsPhotoGalary=0;

    //Отправка фотографий
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    public static final int CAMERA_PERMISSION_CODE = 1002;
    public static final int CAMERA_REQUEST_CODE = 2;
    Uri image_uri;

    //либо камера либо галерея
    int flagSendDataImage=0;
    int flagFirstOpenChat=0;

    private FirebaseFirestore fStore;
    private StorageReference storageReference, imageReference;
    private FirebaseStorage storage;
    private UploadTask uploadTask;
    UserSettings userSettings = new UserSettings();
    String idCurrentChat;
    Contacts contacts;

    //Таймер обновлений сообщений
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            yourfunction();
            loadFromBDChat(contacts.getIdC());
            handler.postDelayed(this, 10000);
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //Shared Preference
        loadJsonData();

        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getSupportActionBar().hide(); //that Menu do not open

        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_purple));

        //Получение второго пользователя
        Bundle arguments = getIntent().getExtras();
        contacts = (Contacts) arguments.getSerializable(Contacts.class.getSimpleName());

        //Установка личных данных второго пользователя
        ImageView imageViewIconConversation = (ImageView) findViewById(R.id.imageViewIconConversation);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profile_image").child("user_"+ contacts.getIconC());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Picasso.get().load(downloadUrl).into(imageViewIconConversation);
            }
        });
        TextView textViewFullNSConversation = (TextView) findViewById(R.id.textViewFullNSConversation);
        textViewFullNSConversation.setText(contacts.getNameC()+" "+contacts.getSurnameC());

        //Для popup меню
        ImageButton imageButtonMenu=(ImageButton) findViewById(R.id.imageButtonMenu);
        imageButtonMenu.setOnClickListener(viewClickListener);

        //Для pop up menu message
        view = this.getWindow().getDecorView().findViewById(android.R.id.content);

        flagFirstOpenChat=1;
        loadFromBDChat(contacts.getIdC());

        //Start timer
        handler.postDelayed(runnable, 10000);
    }

    public void loadFromBDChat(String idAnotherUser){
        //Находим нужный чат для дальнейшей записи сообщений
        //ArrayList<Chat> messagesList_copy = messagesList;
        ArrayList<Chat> messagesList_copy = new ArrayList<>();
        messagesList_copy.addAll(messagesList);
        messagesList.clear();
        fStore.collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageInfo data = new Gson().fromJson(document.getData().toString(), MessageInfo.class);

                                if(data.getIdFirstUser().equals(userSettings.getIdUser().toString()) && data.getIdSecondUser().equals(idAnotherUser)){
                                    idCurrentChat=data.getIdChat();
                                }
                                if(data.getIdSecondUser().equals(userSettings.getIdUser().toString()) && data.getIdFirstUser().equals(idAnotherUser)){
                                    idCurrentChat=data.getIdChat();
                                }
                            }
                            // Toast.makeText(ConversationActivity.this, "!  "+idCurrentChat, Toast.LENGTH_SHORT).show();

                            //Записываем все сообщения из БД в список
                            fStore.collection("Messages").document(idCurrentChat).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d : list) {
                                            ChatInDB c = d.toObject(ChatInDB.class);

                                            //Toast.makeText(ConversationActivity.this, c.getId() + " = " + userSettings.getIdUser().toString(), Toast.LENGTH_SHORT).show();
                                            String subStrImage;
                                            try {
                                                subStrImage = c.getText().substring(0, 5);
                                            }catch (Exception e){
                                                subStrImage = "null_";
                                            }

                                            if(c.getSender().equals(userSettings.getIdUser().toString())){
                                                //проверка на текст или фото
                                                if(subStrImage.equals("chat_")){
                                                    messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_IN_IMAGE,c.getTime()));
                                                }else {
                                                    //добавление обычного текста
                                                    messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_OUT,c.getTime()));
                                                }

                                                // messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_OUT,c.getTime()));
                                            }else{

                                                //проверка на текст или фото
                                                if(subStrImage.equals("chat_")){
                                                    //загрузка из Storage фотографии
                                                    messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_OUT_IMAGE,c.getTime(),contacts.getIconC()));
                                                }else {
                                                    //добавление обычного текста
                                                    messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_IN,c.getTime(),contacts.getIconC()));
                                                }

                                                // messagesList.add(new Chat(c.getText(), ChatAdapter.MESSAGE_TYPE_IN,c.getTime()));
                                            }
                                        }

                                        if(messagesList.size() == messagesList_copy.size()){
                                            //Toast.makeText(ConversationActivity.this, "Old Data " + messagesList.size() + " " + messagesList_copy.size(), Toast.LENGTH_SHORT).show();
                                            if(flagFirstOpenChat == 1){
                                                //Toast.makeText(ConversationActivity.this, "Old Data In new chat", Toast.LENGTH_SHORT).show();
                                                updateAdapter();
                                                flagFirstOpenChat=0;
                                            }
                                        }else{
                                            updateAdapter();
                                            //Toast.makeText(ConversationActivity.this, "New Data " + messagesList.size() + " " + messagesList_copy.size(), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                       //Toast.makeText(ConversationActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                            Log.w("mylog", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    void updateAdapter(){
        //Сортировка сообщений по дате
        Collections.sort(messagesList, new MyDateComparator());

        ChatAdapter adapter = new ChatAdapter(ConversationActivity.this, messagesList);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(ConversationActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        llm.setStackFromEnd(true);
        adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Toast.makeText(ConversationActivity.this, "! "+String.valueOf(messagesList.get(position).messageType), Toast.LENGTH_SHORT).show();
                showPopupMenuForMessage(ConversationActivity.this, messagesList.get(position).messageType, position, messagesList.get(position).message);
            }
        });
    }


    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    public void gotoBackMainChatActivity(View v){
        switch (v.getId()) {
            case R.id.buttonBackMainChatActivity:
                Intent intent = new Intent(this, MainChatActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void sendData(View v){
        switch (v.getId()) {
            case R.id.buttonSendChat:

                EditText editTextMessageChat=(EditText) findViewById(R.id.editTextMessageChat);

                sendDataToDB(editTextMessageChat.getText().toString());

                editTextMessageChat.setText(null);
                break;
            case R.id.buttonImageSendChat:
                //check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup foe runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else{
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }
        }
    }

    ///MENU
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, CAMERA_PERMISSION_CODE);
                        }
                        else{
                            openCamera();
                        }
                    }
                    else{

                    }

                    flagSendDataImage=2;
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_PICK_CODE);

                    flagSendDataImage=1;
                }
                else if (optionsMenu[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    public void sendDataToDB(String textMessage){
        //сохраняем новое сообщение в БД

        String idChat= UUID.randomUUID().toString();

        Map<String,Object> chataData = new HashMap<>();
        chataData.put("id", idChat);//UUID.randomUUID().toString()); //id сообщения
        chataData.put("sender",  userSettings.getIdUser()); //id текущего пользователя
        chataData.put("text", textMessage); //текст сообщения
        long currentMilliseconds = new Date().getTime();
        chataData.put("time", String.valueOf(currentMilliseconds));
                //new SimpleDateFormat("HH:mm dd.MM.YYYY").format(Calendar.getInstance().getTime())); //время отправки

        //roomA - id чата
        //db.collection("rooms").document("roomA").collection("messages").document("message1").set(userM)       UUID.randomUUID().toString()
        fStore.collection("Messages").document(idCurrentChat).collection("Chat").document(idChat).set(chataData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(flagIsPhoto==1){
                    addToAdapterGalary(guid);
                    flagIsPhoto=0;
                }
                else if(flagIsPhotoGalary==1){
                    addToAdapterPhoto(guid);
                    flagIsPhotoGalary=0;
                }
                else{
                    messagesList.add(new Chat(textMessage, ChatAdapter.MESSAGE_TYPE_OUT,String.valueOf(currentMilliseconds)));
                    //  new SimpleDateFormat("HH:mm dd.MM.YYYY").format(Calendar.getInstance().getTime())));

                    updateAdapter();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConversationActivity.this, "Ошибка сохранения собщения"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Camera");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //скорее всего сохранение фоток в галерею здесь!
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void pickImageFromGallery(){
        chooseImage(ConversationActivity.this);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    openCamera();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied to camera...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {// && requestCode == IMAGE_PICK_CODE
            if(flagSendDataImage==1){
                //галерея

                //Сохранять в storage
                //Сохранять ссылку в БД
                //Достаем из Storage
                //Добавлять в recycler

                flagIsPhoto=1;

                guid = UUID.randomUUID().toString();
                addToStorageImage(data.getData(),guid);
                //sendDataToDB(idCurrentChat+"_message_"+guid);

                //addToAdapterGalary(guid);



            }
            if(flagSendDataImage==2){
                //фото

                flagIsPhotoGalary=1;

                guid = UUID.randomUUID().toString();
                addToStorageImage(image_uri,guid);
//                sendDataToDB(idCurrentChat+"_message_"+guid);

                //Сначала фото устанавливается в адаптор, а затем в Storage и БД

//                addToAdapterPhoto(guid);
//
//                flagIsPhotoGalary=0;
            }
        }

    }

    public void addToAdapterGalary(String guid){
        long currentMilliseconds = new Date().getTime();
        messagesList.add(new Chat(idCurrentChat+"_message_"+guid, ChatAdapter.MESSAGE_TYPE_IN_IMAGE,String.valueOf(currentMilliseconds)));
        //new SimpleDateFormat("HH:mm dd.MM.YYYY").format(Calendar.getInstance().getTime())));

        updateAdapter();

//        //Сортировка сообщений по дате
//        Collections.sort(messagesList, new MyDateComparator());
//        ChatAdapter adapter2 = new ChatAdapter(ConversationActivity.this, messagesList);
//        recyclerView.setAdapter(adapter2);
//        adapter2.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Toast.makeText(ConversationActivity.this, String.valueOf(messagesList.get(position).messageType), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void addToAdapterPhoto(String guid){
        long currentMilliseconds = new Date().getTime();
        messagesList.add(new Chat(idCurrentChat+"_message_"+guid, ChatAdapter.MESSAGE_TYPE_IN_IMAGE,String.valueOf(currentMilliseconds)));

        updateAdapter();

//        //Сортировка сообщений по дате
//        Collections.sort(messagesList, new MyDateComparator());
//        ChatAdapter adapter2 = new ChatAdapter(ConversationActivity.this, messagesList);
//        recyclerView.setAdapter(adapter2);
//        adapter2.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Toast.makeText(ConversationActivity.this, String.valueOf(messagesList.get(position).messageType), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void addToStorageImage(Uri imageMessage, String guid){
        //Id = GUIDчаста_message_1
        imageReference = storageReference.child("chat_image").child(idCurrentChat+"_message_"+guid);//UUID.randomUUID().toString());//"clients_profile_images/");

        ProgressBar progressBarProfile = (ProgressBar) findViewById(R.id.progressBarConversation);

        uploadTask = imageReference.putFile(imageMessage);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBarProfile.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConversationActivity.this,R.string.errorStored + " : " + e.toString(),Toast.LENGTH_SHORT).show();
                progressBarProfile.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBarProfile.setVisibility(View.GONE);
                Toast.makeText(ConversationActivity.this,R.string.successStored,Toast.LENGTH_SHORT).show();

                if(flagIsPhoto==1 || flagIsPhotoGalary==1){
                    sendDataToDB(idCurrentChat+"_message_"+guid);
                }
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.options_menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuSearch:

                                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                                            @Override
                                            public void onPositiveButtonClick(Object selection) {
                                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                                calendar.setTimeInMillis((Long)selection);
                                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                                String formattedDate  = format.format(calendar.getTime());


                                                filter(String.valueOf(calendar.getTimeInMillis()));

                                                //Toast.makeText(getApplicationContext(), "Дата " + formattedDate+"  "+calendar.getTimeInMillis(), Toast.LENGTH_SHORT).show();
                                                //dateValue1.setText(materialDatePicker.getHeaderText());
                                            }
                                        });
                                return true;
                            case R.id.menuClearHistory:
                                deleteChatHistory();

                                messagesList.clear();
                                updateAdapter();

                                return true;
                            case R.id.menuDeleteChat:
                                //Удаление подколлекций
                                deleteChatHistory();

                                //Удаление из БД
                                fStore.collection("Messages").document(idCurrentChat).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ConversationActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();

                                            //Закрытие окна Conversation
                                            startActivity(new Intent(ConversationActivity.this, MainChatActivity.class));
                                            finish();

                                        } else {
                                            Toast.makeText(ConversationActivity.this, "Error delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                return true;
                            case R.id.menuInviteChat:
                               //invite friends
                                return true;
                            default:
                                return false;
                        }
                    }
                });

//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getApplicationContext(), "onDismiss",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        popupMenu.show();
    }

    public void deleteChatHistory(){
        //Поиск сообщения в БД
        fStore.collection("Messages").document(idCurrentChat).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        ChatInDB c = d.toObject(ChatInDB.class);

                        String subStrImage;
                        try {
                            subStrImage = c.getText().substring(0, 5);
                        }catch (Exception e){
                            subStrImage = "null_";
                        }

                        //проверка на текст или фото
                        if(subStrImage.equals("chat_")){
                            //Удаление из БД
                            deleteAllMessages(c.getId());

                            //Удаление из Storage фотки
                            deleteFromStorageImage(c.getText());
                        }else {
                            //удаление обычного текста
                            //Удаление из БД
                            deleteAllMessages(c.getId());
                        }
                    }
                } else {
                    //Toast.makeText(ConversationActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteAllMessages(String textId){
        //Удаление из БД
        fStore.collection("Messages").document(idCurrentChat).collection("Chat").document(textId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(ConversationActivity.this, "Message deleted " + c.getId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConversationActivity.this, "Error delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteFromStorageImage(String textImage){
        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child("chat_image/"+textImage);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ConversationActivity.this, "Successful delete", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ConversationActivity.this, "Error delete ST", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Chat> filteredlist = new ArrayList<>();

        for (Chat item : messagesList) {
            String time = item.getMessageTime();

            if (Long.parseLong(time) >= Long.parseLong(text)) {
                filteredlist.add(item);
            }
        }

        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found.", Toast.LENGTH_SHORT).show();
        } else {
            //Сортировка сообщений по дате
            Collections.sort(filteredlist, new MyDateComparator());

            //updateAdapter();
//            //updateAdapter
            ChatAdapter adapter = new ChatAdapter(ConversationActivity.this, filteredlist);
            recyclerView = findViewById(R.id.recycler_view);
            LinearLayoutManager llm = new LinearLayoutManager(ConversationActivity.this);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(adapter);
            llm.setStackFromEnd(true);
            adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    showPopupMenuForMessage(ConversationActivity.this, messagesList.get(position).messageType, position, messagesList.get(position).message);
                }
            });
        }
    }

    private void showPopupMenuForMessage(Context context, int typeMessage, int positionMessage, String textMessage) {

        final CharSequence[] optionsMenu_1_2 = {"Edit message", "Delete message"};
        final CharSequence[] optionsMenu_3_4 = {"Zoom image", "Delete image"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(typeMessage==1 || typeMessage==2){
            builder.setItems(optionsMenu_1_2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(optionsMenu_1_2[i].equals("Edit message")){
                        EditText editTextMessageChat=(EditText) findViewById(R.id.editTextMessageChat);
                        editTextMessageChat.setText(textMessage);
                    }
                    else if(optionsMenu_1_2[i].equals("Delete message")){
                        //deleteAllTypeMessage(messagesList.get(positionMessage).messageTime, messagesList.get(positionMessage).message, positionMessage);

                        //Поиск сообщения в БД
                        fStore.collection("Messages").document(idCurrentChat).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        ChatInDB c = d.toObject(ChatInDB.class);

                                        if(messagesList.get(positionMessage).messageTime.equals(c.getTime()) && messagesList.get(positionMessage).message.equals(c.getText())){
                                            //Toast.makeText(ConversationActivity.this, c.getTime(), Toast.LENGTH_SHORT).show();

                                            //Удаление из БД
                                            fStore.collection("Messages").document(idCurrentChat).collection("Chat").document(c.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Toast.makeText(ConversationActivity.this, "Message deleted" + c.getId(), Toast.LENGTH_SHORT).show();
                                                        //Удаление из адаптера
                                                        messagesList.remove(positionMessage);

                                                        updateAdapter();

                                                    } else {
                                                        Toast.makeText(ConversationActivity.this, "Error delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    //Toast.makeText(ConversationActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            builder.show();
        }
        else{
            builder.setItems(optionsMenu_3_4, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(optionsMenu_3_4[i].equals("Zoom image")){

                        //Отправка uri фотографии во фрагмент
                        Bundle bundle = new Bundle();
                        bundle.putString("message", messagesList.get(positionMessage).message);
                        ZoomImageFragment fragInfo = new ZoomImageFragment();
                        fragInfo.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragment_container_zoom_image, fragInfo) //new ZoomImageFragment()
                                .addToBackStack(null)
                                .commit();

                        RecyclerView recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
                        recycler_view.setVisibility(View.GONE);

                    }
                    else if(optionsMenu_3_4[i].equals("Delete image")){

                        //Поиск сообщения в БД
                        fStore.collection("Messages").document(idCurrentChat).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        ChatInDB c = d.toObject(ChatInDB.class);

                                        if(messagesList.get(positionMessage).messageTime.equals(c.getTime()) && messagesList.get(positionMessage).message.equals(c.getText())){
                                            //Toast.makeText(ConversationActivity.this, c.getText(), Toast.LENGTH_SHORT).show();

                                            //Удаление из БД
                                            fStore.collection("Messages").document(idCurrentChat).collection("Chat").document(c.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ConversationActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                                                        //Удаление из адаптера
                                                        messagesList.remove(positionMessage);
                                                        updateAdapter();

                                                        //Удаление из Storage фотки
                                                        deleteFromStorageImage(c.getText());

                                                    } else {
                                                        Toast.makeText(ConversationActivity.this, "Error delete DB", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    //Toast.makeText(ConversationActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            builder.show();
        }
    }

    public void showRecyclerView(){
        RecyclerView recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setVisibility(View.VISIBLE);
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