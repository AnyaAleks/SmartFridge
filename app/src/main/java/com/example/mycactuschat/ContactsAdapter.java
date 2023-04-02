package com.example.mycactuschat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private Uri uriImageSource;

    //listener
    private ContactsAdapter.OnItemClickListener mListener;

    //listener
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemClick2(int position);
    }

    //listener
    public void setOnItemClickListener(ContactsAdapter.OnItemClickListener listener){
        mListener=listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewIconContacts;
        public TextView textViewHeaderContacts;
        public ImageButton imageButtonInformationContacts;
        public ImageButton imageButtonMessageContacts;

        public ViewHolder(View itemView, ContactsAdapter.OnItemClickListener listener) {
            super(itemView);

            imageViewIconContacts=(ImageView) itemView.findViewById(R.id.roundedImageViewIconContacts);
            textViewHeaderContacts=(TextView) itemView.findViewById(R.id.textViewHeaderContacts);
            imageButtonInformationContacts=(ImageButton) itemView.findViewById(R.id.imageButtonInformationContacts);
            imageButtonMessageContacts=(ImageButton) itemView.findViewById(R.id.imageButtonMessageContacts);

            imageButtonInformationContacts.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            imageButtonMessageContacts.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick2(position);
                        }
                    }
                }
            });
        }
    }
    private List<Contacts> contactList;

    public ContactsAdapter(List<Contacts> contacts) {
        contactList = contacts;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_contacts, parent, false);

        ContactsAdapter.ViewHolder viewHolder = new ContactsAdapter.ViewHolder(contactView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        Contacts message = contactList.get(position);

        ImageView icon=holder.imageViewIconContacts;
        TextView header=holder.textViewHeaderContacts;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profile_image").child("user_"+ message.getIconC());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                uriImageSource = downloadUrl;
                Picasso.get().load(uriImageSource).into(icon);
            }
        });

        header.setText(message.getSurnameC()+" "+message.getNameC());
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

}
