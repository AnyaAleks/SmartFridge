package com.example.mycactuschat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private OnItemClickListener mListener; //listener

    public interface OnItemClickListener{ //listener
        void onItemClick(int position);
    }

    //listener
    public void setOnItemClickListener(OnItemClickListener listener){ //listener
        mListener=listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewIconMessage;
        public TextView textViewHeaderMessage;
        public TextView textViewDialogMessage;
        public TextView textViewDateMassage;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            imageViewIconMessage=(ImageView) itemView.findViewById(R.id.roundedImageViewIconMessage);
            textViewHeaderMessage=(TextView) itemView.findViewById(R.id.textViewHeaderMessage);
            textViewDialogMessage=(TextView) itemView.findViewById(R.id.textViewDialogMessage);
            textViewDateMassage=(TextView) itemView.findViewById(R.id.textViewDateMassage);

            //listener
            ///itemView
            itemView.setOnClickListener(new View.OnClickListener(){
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
        }
    }
    private List<Message> messageList;

    public MessageAdapter(List<Message> users) {
        messageList = users;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_message, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        ImageView icon=holder.imageViewIconMessage;
        TextView header=holder.textViewHeaderMessage;
        TextView dialog=holder.textViewDialogMessage;
        TextView date=holder.textViewDateMassage;

        //icon.setImageResource(message.getIconM());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profile_image").child("user_"+ message.getIconM());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                icon.setImageURI(downloadUrl);
                Picasso.get().load(downloadUrl).into(icon);
            }
        });

        header.setText(message.getHeaderM()+" ");
        dialog.setText(message.getDialogM()+" ");

        //дата и время
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM");
        long currentMilliseconds = Long.parseLong(message.getDateM());
        date.setText(dateFormat.format(currentMilliseconds).toString());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}
