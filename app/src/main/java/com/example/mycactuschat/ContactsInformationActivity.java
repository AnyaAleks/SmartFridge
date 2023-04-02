package com.example.mycactuschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

        getSupportActionBar().hide();
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

    public void gotoMainChatActivityInfo(View v){
        switch (v.getId()) {
            case R.id.textViewCancel:
                Intent intent = new Intent(this, MainChatActivity.class);
                startActivity(intent);

                finish();
                break;
        }
    }
}