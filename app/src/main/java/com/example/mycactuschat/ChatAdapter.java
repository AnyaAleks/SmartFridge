package com.example.mycactuschat;

import android.content.Context;
import android.location.GnssAntennaInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<Chat> list;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    public static final int MESSAGE_TYPE_IN_IMAGE = 3;
    public static final int MESSAGE_TYPE_OUT_IMAGE = 4;

    //listener
    private ChatAdapter.OnItemClickListener mListener;

    //listener
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    //listener
    public void setOnItemClickListener(ChatAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public ChatAdapter(Context context, ArrayList<Chat> list) {
        this.context = context;
        this.list = list;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV,dateTV;
        ImageView imageIconAnotherUser;
        MessageInViewHolder(final View itemView, ChatAdapter.OnItemClickListener listener) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            dateTV = itemView.findViewById(R.id.date_text);
            imageIconAnotherUser = itemView.findViewById(R.id.roundedImageViewChatLeft);

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
        void bind(int position) {
            Chat messageModel = list.get(position);
            messageTV.setText(messageModel.message);
            //dateTV.setText(messageModel.messageTime);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YY");
            long currentMilliseconds = Long.parseLong(messageModel.messageTime);
            dateTV.setText(dateFormat.format(currentMilliseconds).toString());

            //dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dateRef = storageRef.child("profile_image").child("user_"+messageModel.messageUserImage);
            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    imageIconAnotherUser.setImageURI(downloadUrl);
                    Picasso.get().load(downloadUrl).into(imageIconAnotherUser);
                }
            });
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV,dateTV;
        MessageOutViewHolder(final View itemView, ChatAdapter.OnItemClickListener listener) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            dateTV = itemView.findViewById(R.id.date_text);

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
        void bind(int position) {
            Chat messageModel = list.get(position);
            messageTV.setText(messageModel.message);
            //dateTV.setText(messageModel.messageTime);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YY");
            long currentMilliseconds = Long.parseLong(messageModel.messageTime);
            dateTV.setText(dateFormat.format(currentMilliseconds).toString());

            //dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
        }
    }

    private class MessageImageInViewHolder extends RecyclerView.ViewHolder {

        TextView dateTV;
        ImageView imageTV;
        MessageImageInViewHolder(final View itemView, ChatAdapter.OnItemClickListener listener) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.date_text);
            imageTV=itemView.findViewById(R.id.image_text);

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
        void bind(int position) {
            Chat messageModel = list.get(position);
            //dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YY");
            long currentMilliseconds = Long.parseLong(messageModel.messageTime);
            dateTV.setText(dateFormat.format(currentMilliseconds).toString());

            //dateTV.setText(messageModel.messageTime);

            //imageTV.setImageURI(messageModel.messageImage);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dateRef = storageRef.child("chat_image").child(messageModel.message);
            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
//                    Uri uriImageSource = downloadUrl;
//                    Picasso.get().load(uriImageSource).into(imageTV);
                    imageTV.setImageURI(downloadUrl);
                    Picasso.get().load(downloadUrl).into(imageTV);
                }
            });
        }
    }

    private class MessageImageOutViewHolder extends RecyclerView.ViewHolder {

        TextView dateTV;
        ImageView imageTV,imageIconAnotherUser;
        MessageImageOutViewHolder(final View itemView, ChatAdapter.OnItemClickListener listener) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.date_text);
            imageTV=itemView.findViewById(R.id.image_text);
            imageIconAnotherUser = itemView.findViewById(R.id.roundedImageViewChatLeftImage);

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
        void bind(int position) {
            Chat messageModel = list.get(position);
            //dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
            //dateTV.setText(messageModel.messageTime);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.YY");
            long currentMilliseconds = Long.parseLong(messageModel.messageTime);
            dateTV.setText(dateFormat.format(currentMilliseconds).toString());

            //imageTV.setImageURI(messageModel.messageImage);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dateRef = storageRef.child("chat_image").child(messageModel.message);
            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    imageTV.setImageURI(downloadUrl);
                    Picasso.get().load(downloadUrl).into(imageTV);
                }
            });

            StorageReference storageRefImage = FirebaseStorage.getInstance().getReference();
            StorageReference dateRefImage = storageRefImage.child("profile_image").child("user_"+messageModel.messageUserImage);
            dateRefImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    imageIconAnotherUser.setImageURI(downloadUrl);
                    Picasso.get().load(downloadUrl).into(imageIconAnotherUser);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false),mListener);
        }
        else if(viewType == MESSAGE_TYPE_IN_IMAGE){
            return new MessageImageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_right_image, parent, false),mListener);
        }
        else if(viewType == MESSAGE_TYPE_OUT_IMAGE){
            return new MessageImageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_left_image, parent, false),mListener);
        }
        else{
            return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false),mListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list.get(position).messageType == MESSAGE_TYPE_IN) {
            ((MessageInViewHolder) holder).bind(position);
        }
        else if(list.get(position).messageType == MESSAGE_TYPE_IN_IMAGE){
            ((MessageImageInViewHolder) holder).bind(position);
        }
        else if(list.get(position).messageType == MESSAGE_TYPE_OUT_IMAGE){
            ((MessageImageOutViewHolder) holder).bind(position);
        }
        else {
            ((MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).messageType;
    }
}