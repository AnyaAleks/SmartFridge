package com.example.mycactuschat;

import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class ZoomImageFragment extends Fragment {

    private ImageView iv;

    public ZoomImageFragment() {
    }

    public static ZoomImageFragment newInstanse(){
        return new ZoomImageFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        View view = inflater.inflate(R.layout.fragment_zoom_image, container, false);

        //Получение данных из активности
        String strtext = getArguments().getString("message");

        iv = (ImageView) view.findViewById(R.id.imageViewZoomImage);

        //Установка изображения из storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("chat_image").child(strtext);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                iv.setImageURI(downloadUrl);
                Picasso.get().load(downloadUrl).into(iv);
            }
        });

        //Кнопка X
        Chip chipX = (Chip) view.findViewById(R.id.chip_x);
        chipX.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((ConversationActivity) getActivity()).showRecyclerView();
                getActivity().onBackPressed();


            }
        });

        final Animation animationRotateCenter = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_center);

        //Кнопка zoom in
        Chip chipZoomIn = (Chip) view.findViewById(R.id.chip_zoom_in);
        chipZoomIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // calculate current scale x and y value of ImageView
                float x = iv.getScaleX();
                float y = iv.getScaleY();
                // set increased value of scale x and y to perform zoom in functionality
                iv.setScaleX((float) (x + 0.5));
                iv.setScaleY((float) (y + 0.5));
            }
        });

        //Кнопка zoom out
        Chip chipZoomOut = (Chip) view.findViewById(R.id.chip_zoom_out);
        chipZoomOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // calculate current scale x and y value of ImageView
                float x = iv.getScaleX();
                float y = iv.getScaleY();
                // set increased value of scale x and y to perform zoom in functionality
                iv.setScaleX((float) (x - 0.5));
                iv.setScaleY((float) (y - 0.5));
            }
        });

        //Кнопка rotate
        Chip chipRotate = (Chip) view.findViewById(R.id.chip_rotate);
        chipRotate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                iv.startAnimation(animationRotateCenter);
            }
        });




        return view;
    }
}
