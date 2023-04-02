package com.example.mycactuschat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;

public class LogoutDialogFragment extends Fragment {

    public LogoutDialogFragment()
    {
    }

    public static LogoutDialogFragment newInstanse(){
        return new LogoutDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_logout_dialog, container, false);

        //Кнопка Yes
        Chip chipYes = (Chip) view.findViewById(R.id.chip_Yes);
        chipYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        //Кнопка No
        Chip chipNo = (Chip) view.findViewById(R.id.chip_No);
        chipNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(LogoutDialogFragment.this).commit();
            }
        });

        return view;
    }
}
