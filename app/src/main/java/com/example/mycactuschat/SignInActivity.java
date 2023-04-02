package com.example.mycactuschat;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    //ArrayList<UserSettings> userSettingsList;
    UserSettings userSettings;
    public String userId="-1";
    public String userPassword="123456";

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private TextInputLayout outlinedTextFieldPhoneSignIn, outlinedTextFieldPasswordSignIn;
    private TextInputEditText textInputEditPhoneSignIn, textInputEditPasswordSignIn;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z\\.]*@[A-Za-z]*\\.[a-z]{2,3}$");//Pattern.compile("^[a-z0-9]+@[a-z]+.[a-z]{2,3}?");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //TextInputLayout
        outlinedTextFieldPhoneSignIn = (TextInputLayout) findViewById(R.id.outlinedTextFieldPhoneSignIn);
        outlinedTextFieldPasswordSignIn = (TextInputLayout) findViewById(R.id.outlinedTextFieldPasswordSignIn);

        //TextInputEditText
        textInputEditPhoneSignIn = (TextInputEditText) findViewById(R.id.textInputEditPhoneSignIn);
        textInputEditPasswordSignIn = (TextInputEditText) findViewById(R.id.textInputEditPasswordSignIn);

        getSupportActionBar().hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_purple));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // initialise the layout
        WebView webView = (WebView) findViewById(R.id.webvidewCactusHello);
        webView.loadUrl("file:///android_asset/cactus_hello.html");
        webView.setBackgroundColor(Color.TRANSPARENT);

        //Подчеркивание текста
        TextView textViewSignIn = (TextView) findViewById(R.id.textViewSignUp);
        textViewSignIn.setPaintFlags(textViewSignIn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        loadJsonData();
        //Toast.makeText(this, userSettings.getIdUser(), Toast.LENGTH_SHORT).show();
        if(!userSettings.getIdUser().equals("-1")){
            startActivity(new Intent(this, MainChatActivity.class));
        }
    }

    public void gotoSignUpActivity(View v){
        switch (v.getId()) {
            case R.id.textViewSignUp:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);

                finish();
                break;
            case R.id.buttonSignIn:

                if (textInputEditPhoneSignIn.getText().toString().equals("") || textInputEditPasswordSignIn.getText().toString().equals("")) {

                    // Set error text
                    if (textInputEditPhoneSignIn.getText().toString().equals("")) {
                        outlinedTextFieldPhoneSignIn.setError("Error");
                    } else {
                        outlinedTextFieldPhoneSignIn.setError(null);
                    }

                    if (textInputEditPasswordSignIn.getText().toString().equals("")) {
                        outlinedTextFieldPasswordSignIn.setError("Error");
                    } else {
                        outlinedTextFieldPasswordSignIn.setError(null);
                    }
                }
                else {
                    if(validateEmail()){
                        outlinedTextFieldPhoneSignIn.setError(null);

                        if(textInputEditPasswordSignIn.getText().toString().length()>=6){
                            outlinedTextFieldPasswordSignIn.setError(null);

                            userPassword=textInputEditPasswordSignIn.getText().toString();
                            authStart(textInputEditPhoneSignIn.getText().toString(), textInputEditPasswordSignIn.getText().toString());
                        }
                        else{
                            outlinedTextFieldPasswordSignIn.setError("Error");
                            Toast.makeText(getApplicationContext(), R.string.errorPassword, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        outlinedTextFieldPhoneSignIn.setError("Error");
                    }
                }
                break;
        }
    }

    private boolean validateEmail() {
        String outlinedTextFieldEmailSignUpString = outlinedTextFieldPhoneSignIn.getEditText().getText().toString().trim();

        if (!EMAIL_PATTERN.matcher(outlinedTextFieldEmailSignUpString).matches()) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }


    public void saveJsonData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userSettings);
        editor.putString("user settings", json);
        editor.apply();
    }

    public void loadJsonData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user settings", null);
        userSettings = gson.fromJson(json, UserSettings.class);

        if (userSettings == null) {
            userSettings = new UserSettings(userId,userPassword);
        }
    }

    public void authStart(String emailText, String passwordText){
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, R.string.welcome, Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String _userId = mAuth.getCurrentUser().getUid();

                            userSettings.setPasswordUser(passwordText);
                            userSettings.setIdUser(_userId);
                            saveJsonData();

                            //Toast.makeText(SignInActivity.this, _userId, Toast.LENGTH_SHORT).show();

                            Intent intent2 = new Intent(SignInActivity.this, MainChatActivity.class);
                            startActivity(intent2);
                            finish();

                        } else {
                            Toast.makeText(SignInActivity.this, R.string.no_user, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}