package com.example.mycactuschat;

import static android.service.controls.ControlsProviderService.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    //Для сохранения фотографий
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference, imageReference;
    private static final int SELECTED=100;
    private ProgressDialog progressDialog;
    private UploadTask uploadTask;


    ImageButton arrow;
    LinearLayout hiddenView;
    CardView cardView;

    SwitchMaterial switchMode, switchSavingPhoto, switchNotification;

    EditText editTextProfileName;
    EditText editTextProfileSurname;
    EditText editTextProfilePhone;
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+7[0-9]{10}");
//    EditText editTextProfileEmail;
    EditText editTextProfilePassword;
    TextView textViewFullNameSurname;

    UserSettings userSettings = new UserSettings();
    Contacts contactsCurrentUser;

    String imageProfileUri;

    //Установка фотографий
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    ImageView imageViewIconProfile;


    public ProfileFragment(){

    }

    public static ProfileFragment newInstanse(){
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageViewIconProfile = (ImageView) view.findViewById(R.id.imageViewIconProfileUser);
        editTextProfileName=(EditText) view.findViewById(R.id.editTextProfileName);
        editTextProfileSurname=(EditText) view.findViewById(R.id.editTextProfileSurname);
        editTextProfilePhone=(EditText) view.findViewById(R.id.editTextProfilePhone);
//        editTextProfileEmail=(EditText) view.findViewById(R.id.editTextProfileEmail);
        editTextProfilePassword=(EditText) view.findViewById(R.id.editTextProfilePassword);
        textViewFullNameSurname=(TextView) view.findViewById(R.id.textViewFullNameSurname);

        //забирание всех данных текущего пользователя
        getAllUsersData();

        loadJsonData();

        //Изменение полей пользователя
        TextView button = (TextView) view.findViewById(R.id.textViewDone);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Проверка на пустые поля
                if (editTextProfileName.getText().toString().equals("")
                        || editTextProfileSurname.getText().toString().equals("")
                        || editTextProfilePhone.getText().toString().equals("")
//                        || editTextProfileEmail.getText().toString().equals("")
                        || editTextProfilePassword.getText().toString().equals("")){
                    Toast.makeText(getActivity(),R.string.errorNull,Toast.LENGTH_SHORT).show();
                }
                else{
                    if(validatePhone()){
                        //Проверка на длину пароля
                        if(editTextProfilePassword.getText().toString().length() < 6){
                            Toast.makeText(getActivity(),R.string.errorPassword,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Обновления в БД
                            updateCourses(editTextProfilePhone.getText().toString(), editTextProfileName.getText().toString(),editTextProfileSurname.getText().toString(), editTextProfilePassword.getText().toString());
                        }
                    }
                }
            }
        });

        //Выход из профиля
        TextView textViewLogout = (TextView) view.findViewById(R.id.textViewLogout);
        textViewLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userSettings.setIdUser("-1");
                saveJsonData();

                LogoutDialogFragment nextFrag= new LogoutDialogFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, nextFrag, "findThisFragment").addToBackStack(null).commit();
            }
        });

//        String url="https://firebasestorage.googleapis.com/v0/b/retrieve-images-958e5.appspot.com/o/9.PNG?alt=media&token=6bd05383-0070-4c26-99cb-dcb17a23f7eb";
//        Glide.with(getApplicationContext()).load(url).into(imageViewIconProfile);

        getUrlAsync();

        //Рус и Англ локализация
        Chip chipEn = (Chip) view.findViewById(R.id.chip_EN);
        Chip chipRu = (Chip) view.findViewById(R.id.chip_RU);

        //выставление настроек пользователя
        if(userSettings.getLocalisationUser()==1){
            chipEn.setChecked(true);
            chipRu.setChecked(false);
        }
        else{
            chipEn.setChecked(false);
            chipRu.setChecked(true);
        }

        chipEn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userSettings.setLocalisationUser(1);
                saveJsonData();

                chipRu.setChecked(false);
                chipEn.setChecked(true);

                Locale locale2 = new Locale("en");
                Locale.setDefault(locale2);
                Configuration config2 = getActivity().getBaseContext().getResources().getConfiguration();
                config2.locale = locale2;
                getActivity().getBaseContext().getResources().updateConfiguration(config2, getActivity().getBaseContext().getResources().getDisplayMetrics());

                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
        chipRu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userSettings.setLocalisationUser(2);
                saveJsonData();

                chipRu.setChecked(true);
                chipEn.setChecked(false);

                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
                config.locale = locale;
                getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        //Сохранение фоток в галерею
        switchSavingPhoto = view.findViewById(R.id.switchSavingPhoto);
        if(userSettings.getSavingPhotoUser()==1){
            switchSavingPhoto.setChecked(true);
        }
        else{
            switchSavingPhoto.setChecked(false);
        }

        switchSavingPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    userSettings.setSavingPhotoUser(1);
                    saveJsonData();
                    switchSavingPhoto.setChecked(true);
                }
                else{
                    userSettings.setSavingPhotoUser(2);
                    saveJsonData();
                    switchSavingPhoto.setChecked(false);
                }
            }
        });

        //Получение уведомлений
        switchNotification = view.findViewById(R.id.switchNotification);
        if(userSettings.getNotificationUser()==1){
            switchNotification.setChecked(true);
        }
        else{
            switchNotification.setChecked(false);
        }

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    userSettings.setNotificationUser(1);
                    saveJsonData();
                    switchNotification.setChecked(true);
                }
                else{
                    userSettings.setNotificationUser(2);
                    saveJsonData();
                    switchNotification.setChecked(false);
                }
            }
        });

        //Переключение темы
        switchMode = view.findViewById(R.id.switchDarkMode);
        //выставление настроек пользователя
        if(userSettings.getModeUser()==1){
            switchMode.setChecked(false);
            switchMode.setText(R.string.light_mode);
        }
        else{
            switchMode.setChecked(true);
            switchMode.setText(R.string.dark_mode);
        }

        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    userSettings.setModeUser(2);
                    saveJsonData();

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    //getActivity().finish();
                    //startActivity(getActivity().getIntent());
                    switchMode.setChecked(true);
                    switchMode.setText(R.string.dark_mode);

                }
                else{
                    userSettings.setModeUser(1);
                    saveJsonData();

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    //getActivity().finish();
                    //startActivity(getActivity().getIntent());
                    switchMode.setChecked(false);
                    switchMode.setText(R.string.light_mode);
                }
            }
        });

        //Изменение фотографии пользователя
        ImageButton buttonChangeImageProfile = (ImageButton) view.findViewById(R.id.buttonChangeImageProfile);
        buttonChangeImageProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickImageFromGallery();
            }
        });

        //Удаление профиля
        TextView textViewDeleteProfile = (TextView) view.findViewById(R.id.textViewDeleteProfile);
        textViewDeleteProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //Удаление из Authenticate
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();

                //Удаление из БД
                fStore.collection("Clients").document(userSettings.getIdUser()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), R.string.success_delete_profile, Toast.LENGTH_SHORT).show();

                            userSettings.setIdUser("-1");
                            saveJsonData();

                            startActivity(new Intent(getActivity(), SignInActivity.class));
                        } else {
                            Toast.makeText(getActivity(), R.string.error_delete_profile, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spisok();
        super.onViewCreated(view, savedInstanceState);
    }

    public void spisok(){
        //Выпадной список
        cardView = (CardView) getView().findViewById(R.id.base_cardview);
        arrow = (ImageButton) getView().findViewById(R.id.arrow_button);
        hiddenView = (LinearLayout) getView().findViewById(R.id.hidden_view);

        arrow.setOnClickListener(view -> {
            // If the CardView is already expanded, set its visibility
            // to gone and change the expand less icon to expand more.
            if (hiddenView.getVisibility() == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenView.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_baseline_pen);
            }

            // If the CardView is not expanded, set its visibility to
            // visible and change the expand more icon to expand less.
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenView.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up);
            }
        });
    }

    //handle result of picked image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {// && requestCode == IMAGE_PICK_CODE

            //Set from galary
            imageViewIconProfile.setImageURI(data.getData());

            ///uploadFoto
            imageReference = storageReference.child("profile_image").child("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString());//UUID.randomUUID().toString());//"clients_profile_images/");

//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMax(100);
//            progressDialog.setMessage("Uploading...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.show();
//            progressDialog.setCancelable(false);
            ProgressBar progressBarProfile = (ProgressBar) getView().findViewById(R.id.progressBarProfile);

            uploadTask = imageReference.putFile(data.getData());

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
//                    progressDialog.incrementSecondaryProgressBy((int)progress);


                    progressBarProfile.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),R.string.errorStored + " : " + e.toString(),Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                    progressBarProfile.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //progressDialog.dismiss();
                    progressBarProfile.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),R.string.successStored,Toast.LENGTH_SHORT).show();

                }
            });

            //Сохранение в БД
            updateCourses(editTextProfilePhone.getText().toString(), editTextProfileName.getText().toString(),editTextProfileSurname.getText().toString(), editTextProfilePassword.getText().toString());
        }
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //Permission for images from gallery
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
                    Toast.makeText(getActivity(), "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Сохранение в настройки
    public void saveJsonData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userSettings);
        editor.putString("user settings", json);
        editor.apply();
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


    private void updateCourses(String phoneText, String nameText, String surnameText, String passwordText) {
        //Обновление данных в БД
        Map<String,Object> userM = new HashMap<>();
        userM.put("name", nameText);
        userM.put("surname", surnameText);
        userM.put("phone", phoneText);
        userM.put("email",  FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userM.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        userM.put("icon",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

        fStore.collection("Clients")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(userM)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), R.string.successUpdate, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), MainChatActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), R.string.errorUpdate, Toast.LENGTH_SHORT).show();
            }
        });

        //Обновление данных в Authenticate
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), userSettings.getPasswordUser());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(passwordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                        }
                    }
                });
    }

    public void getAllUsersData(){
        fStore.collection("Clients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    contactsCurrentUser = new Gson().fromJson(document.getData().toString(), Contacts.class);
                                    //imageViewIconProfile
                                    editTextProfileName.setText(contactsCurrentUser.getNameC());
                                    editTextProfileSurname.setText(contactsCurrentUser.getSurnameC());
                                    editTextProfilePhone.setText(contactsCurrentUser.getPhoneC());
                                    textViewFullNameSurname.setText(contactsCurrentUser.getNameC() + " " + contactsCurrentUser.getSurnameC());
                                    editTextProfilePassword.setText(userSettings.getPasswordUser());
                                }
                            }
                        }
                    }
                });
    }

    //Преобразование в Uri из БД
    private void getUrlAsync (){
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profile_image").child("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
               // imageProfileUri = dateRef.toString();//downloadUrl.toString().substring(8);
                //Toast.makeText(getActivity(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                imageViewIconProfile.setImageURI(downloadUrl);
                Picasso.get().load(downloadUrl).into(imageViewIconProfile);
            }
        });
    }

    private boolean validatePhone() {
        String outlinedTextFieldPhoneSignUpString = editTextProfilePhone.getText().toString().trim();

        if (!PHONE_PATTERN.matcher(outlinedTextFieldPhoneSignUpString).matches()) {
            Toast toast = Toast.makeText(getActivity(), R.string.errorPhone, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }
}
