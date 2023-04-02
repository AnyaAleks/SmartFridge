package com.example.mycactuschat;

import static android.service.controls.ControlsProviderService.TAG;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ContactsFragment extends Fragment {
    private FirebaseFirestore fStore;
    ArrayList<Contacts> contactsList;
    ContactsAdapter adapter;
    RecyclerView rvContacts;

    UserSettings userSettings = new UserSettings();

    public ContactsFragment(){

    }

    public static ContactsFragment newInstanse(){
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //для меню
        setHasOptionsMenu(true);

        //Shared Preference
        loadJsonData();

        //Считывание всех пользователей из БД
        fStore = FirebaseFirestore.getInstance();
        contactsList = new ArrayList<Contacts>();
        rvContacts = (RecyclerView) view.findViewById(R.id.recyclerViewContactsFragment);
        fStore.collection("Clients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Toast.makeText(getActivity(), document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
                                Log.i("MyLog", document.getId() + " => " + document.getData());
                                Contacts data = new Gson().fromJson(document.getData().toString(), Contacts.class);
                                //contactsList.add(document.toObject(Contacts.class));
                                //Toast.makeText(getActivity(), data.getNameC().toString(), Toast.LENGTH_SHORT).show();
                                contactsList.add(data);
                            }

                            adapter = new ContactsAdapter(contactsList);
                            rvContacts.setAdapter(adapter);
                            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent intent = new Intent(new Intent(getActivity(), ContactsInformationActivity.class));
                                    intent.putExtra(Contacts.class.getSimpleName(), contactsList.get(position));
                                    startActivity(intent);
                                }
                                @Override
                                public void onItemClick2(int position) {
                                    //Проверка на существующий чат
                                    fStore.collection("Messages")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    int flagMessageAlreadyExist=0;

                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            MessageInfo data = new Gson().fromJson(document.getData().toString(), MessageInfo.class);

                                                            String dataUserF = data.getIdFirstUser() +"";
                                                            String dataUserS = data.getIdSecondUser() +"";

                                                            if(dataUserF.equals(userSettings.getIdUser().toString()) && dataUserS.equals(contactsList.get(position).getIdC().toString())){
                                                                flagMessageAlreadyExist = 1;
                                                            }
                                                            if(dataUserS.equals(userSettings.getIdUser().toString()) && dataUserF.equals(contactsList.get(position).getIdC().toString())){
                                                                flagMessageAlreadyExist = 1;
                                                            }
                                                        }

                                                        if(flagMessageAlreadyExist==1){
                                                            Toast.makeText(getActivity(), "Сhat has already been created!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(new Intent(getActivity(), ConversationActivity.class));
                                                            intent.putExtra(Contacts.class.getSimpleName(), contactsList.get(position));
                                                            startActivity(intent);
                                                        }
                                                        else{
                                                            Map<String,Object> chataData = new HashMap<>();
                                                            chataData.put("idFirstUser", userSettings.getIdUser());
                                                            chataData.put("idSecondUser", contactsList.get(position).getIdC());
                                                            chataData.put("idChat", "chat_"+ userSettings.getIdUser()+"_"+ contactsList.get(position).getIdC());

                                                            //roomA - id чата
                                                            fStore.collection("Messages").document("chat_"+ userSettings.getIdUser()+"_"+ contactsList.get(position).getIdC()).set(chataData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getActivity(), "Сhat created!", Toast.LENGTH_SHORT).show();

                                                                    //передавать второго пользователя
                                                                    Intent intent = new Intent(new Intent(getActivity(), ConversationActivity.class));
                                                                    intent.putExtra(Contacts.class.getSimpleName(), contactsList.get(position));
                                                                    startActivity(intent);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getActivity(), "Ошибка создания чата"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                                                        Log.w("mylog", "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                }
                            });
                                } else {
                                    //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                                    Log.w("mylog", "Error getting documents.", task.getException());
                                }
                            }
                });
        return view;
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
        ArrayList<Contacts> filteredlist = new ArrayList<Contacts>();

        for (Contacts item : contactsList) {
            String fullname = item.getNameC() + " " + item.getSurnameC();
            if (fullname.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        //if (filteredlist.isEmpty()) {
            //Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        //} else {
            adapter = new ContactsAdapter(filteredlist);
            rvContacts.setAdapter(adapter);
            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(new Intent(getActivity(), ContactsInformationActivity.class));
                    intent.putExtra(Contacts.class.getSimpleName(), contactsList.get(position));
                    startActivity(intent);
                }
                @Override
                public void onItemClick2(int position) {
                    startActivity(new Intent(getActivity(), ConversationActivity.class));
                }
            });
        //}
    }
}
