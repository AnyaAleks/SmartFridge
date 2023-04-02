package com.example.mycactuschat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    ArrayList<Message> user;
    public ArrayList<Message> messagesList = new ArrayList<Message>();

    MessageAdapter adapter;
    RecyclerView rvContacts;

    private FirebaseFirestore fStore;
    UserSettings userSettings = new UserSettings();

    Contacts currentUserForChating;
    public ArrayList<Contacts> contactsList = new ArrayList<Contacts>();

    public MessagesFragment(){

    }

    public static MessagesFragment newInstanse(){
        return new MessagesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        //для меню
        setHasOptionsMenu(true);

        loadJsonData();

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String idAnotherUser="";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageInfo dataMessage = new Gson().fromJson(document.getData().toString(), MessageInfo.class);
                                //Log.i("MyLog", dataMessage.getIdFirstUser());


                                //Доставание другого пользователя
                                String dataUserF = dataMessage.getIdFirstUser() +"";
                                String dataUserS = dataMessage.getIdSecondUser() +"";

                                if(dataMessage.getIdFirstUser().equals(userSettings.getIdUser().toString()) || dataMessage.getIdSecondUser().equals(userSettings.getIdUser().toString())){
                                    if(dataUserF.equals(userSettings.getIdUser().toString())){
                                        idAnotherUser=dataUserS;
                                    }
                                    if(dataUserS.equals(userSettings.getIdUser().toString())){
                                        idAnotherUser=dataUserF;
                                    }

                                    String finalIdAnotherUser = idAnotherUser;

                                    fStore = FirebaseFirestore.getInstance();
                                    fStore.collection("Clients")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            //Toast.makeText(getActivity(), document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
                                                            //Log.i("MyLog", document.getId() + " => " + document.getData());
                                                            Contacts dataClient = new Gson().fromJson(document.getData().toString(), Contacts.class); //dasha//Unterminated object at line 1 column 34 path $.surname

                                                            if(dataClient.getIdC().equals(finalIdAnotherUser)){
                                                                //Log.i("FINDME", dataMessage.getIdChat());

                                                                //хранятся все сообщения (неотсортированные)
                                                                ArrayList<SimpleMessageView> simpleMessageViewList = new ArrayList<SimpleMessageView>();
                                                                String ggg="";
                                                                //поиск данных чата для вывода последнего сообщения чата
                                                                fStore.collection("Messages").document(dataMessage.getIdChat()).collection("Chat").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        if (!queryDocumentSnapshots.isEmpty()) {
                                                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                                            for (DocumentSnapshot d : list) {
                                                                                ChatInDB allChats = d.toObject(ChatInDB.class);
                                                                                simpleMessageViewList.add(new SimpleMessageView(allChats.getText(),allChats.getTime()));
                                                                            }
                                                                            //Сортировка сообщений по дате
                                                                            Collections.sort(simpleMessageViewList, new MySimpleDateComparator());
                                                                            //ggg=simpleMessageViewList.get(0).getSimpleText();

                                                                            contactsList.add(dataClient);

                                                                            String subStrImage;
                                                                            try {
                                                                                subStrImage = simpleMessageViewList.get(simpleMessageViewList.size()-1).getSimpleText().substring(0, 5);
                                                                            }catch (Exception e){
                                                                                subStrImage = "null_";
                                                                            }

                                                                            //проверка на текст или фото
                                                                            if(subStrImage.equals("chat_")){
                                                                                messagesList.add(new Message(dataClient.getIconC(), dataClient.getSurnameC()+" "+dataClient.getNameC(), "photo", simpleMessageViewList.get(simpleMessageViewList.size()-1).getSimpleTime()));
                                                                            }else {
                                                                                //добавление обычного текста
                                                                                messagesList.add(new Message(dataClient.getIconC(), dataClient.getSurnameC()+" "+dataClient.getNameC(), simpleMessageViewList.get(simpleMessageViewList.size()-1).getSimpleText(), simpleMessageViewList.get(simpleMessageViewList.size()-1).getSimpleTime()));
                                                                            }

                                                                            Collections.sort(messagesList, new MyMessagesComparator());

                                                                            adapter = new MessageAdapter(messagesList);
                                                                            rvContacts = (RecyclerView) view.findViewById(R.id.recyclerViewMessageFragment);
                                                                            rvContacts.setAdapter(adapter);
                                                                            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                                            adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                                                                                @Override
                                                                                public void onItemClick(int position) {
                                                                                    //currentUserForChating = contactsList.get(position);

                                                                                    for(int i=0; i<contactsList.size();i++){
                                                                                        if((contactsList.get(i).getSurnameC()+" "+contactsList.get(i).getNameC()).equals(messagesList.get(position).getHeaderM())){
                                                                                            currentUserForChating = contactsList.get(i);
                                                                                        }
                                                                                    }

                                                                                    //Toast.makeText(getActivity(),  messagesList.get(position).getHeaderM() + "  " + currentUserForChating.getSurnameC(), Toast.LENGTH_SHORT).show();

                                                                                    gotoDescription();
                                                                                }
                                                                            });

                                                                        } else {
                                                                            //Toast.makeText(getActivity(), "No data found in Database for SIMPLE", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                                if(messagesList.size()!=0){
                                                                    if(messagesList.get(messagesList.size()-1).getHeaderM().equals(dataClient.getSurnameC()+" "+dataClient.getNameC())){
                                                                        messagesList.add(new Message(dataClient.getIconC(), dataClient.getSurnameC()+" "+dataClient.getNameC(), "no message", "0"));
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    } else {
                                                        //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                                                        Log.d("FIFA", "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                            Log.w("mylog", "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }

    public void gotoDescription(){
        Intent intent = new Intent(new Intent(getActivity(), ConversationActivity.class));
        intent.putExtra(Contacts.class.getSimpleName(), currentUserForChating);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String text) {
        ArrayList<Message> filteredlist = new ArrayList<Message>();

        for (Message item : messagesList) {
            String fullname = item.getHeaderM();
            if (fullname.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        //if (filteredlist.isEmpty()) {
            //Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        //} else {
            adapter = new MessageAdapter(filteredlist);
            rvContacts.setAdapter(adapter);
            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    gotoDescription();
                }
            });
        //}
    }

    //Доставание из настроек
    public void loadJsonData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user settings", null);
        userSettings = gson.fromJson(json, UserSettings.class);

        if (userSettings == null) {
            Toast.makeText(getActivity(), "Empty Settings!", Toast.LENGTH_SHORT).show();
            //userSettings = new UserSettings(userId);
        }
    }
}

