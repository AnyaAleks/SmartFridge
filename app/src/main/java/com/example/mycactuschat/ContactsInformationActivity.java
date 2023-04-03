package com.example.mycactuschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ContactsInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_information);

        getSupportActionBar().setTitle(R.string.newreciept_caps); ///НАЗВАНИЕ РЕЦЕПТА
        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_purple));

        Bundle arguments = getIntent().getExtras();
        Contacts contacts;
        contacts = (Contacts) arguments.getSerializable(Contacts.class.getSimpleName());

        ImageView imageViewIconContactsInfo = (ImageView) findViewById(R.id.imageViewIconContactsInfo);
        //imageView:
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profile_image").child("user_"+ contacts.getIconC());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Picasso.get().load(downloadUrl).into(imageViewIconContactsInfo);
            }
        });
        TextView textViewNameSurnameContactsInfo = (TextView) findViewById(R.id.textViewNameSurnameContactsInfo);
        textViewNameSurnameContactsInfo.setText(contacts.getNameC() + " "+contacts.getSurnameC());
        TextView textViewPhoneContactsInfo = (TextView) findViewById(R.id.textViewPhoneContactsInfo);
        textViewPhoneContactsInfo.setText(contacts.getPhoneC());
        TextView textViewEmailContactsInfo = (TextView) findViewById(R.id.textViewEmailContactsInfo);
        textViewEmailContactsInfo.setText(contacts.getEmailC());
    }

    public void saveNewRecipe(View v){
        switch (v.getId()) {
            case R.id.textViewCancel:
                ///СОХРАНЕНИЕ
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        searchItem.setVisible(false);

        MenuItem addItem = menu.findItem(R.id.actionSMT);
        addItem.setIcon(R.drawable.ic_baseline_cancel);
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });

        return true;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu,inflater);
//        inflater.inflate(R.menu.search_menu, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.actionSearch);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return false;
//            }
//        });
//

//    }
}