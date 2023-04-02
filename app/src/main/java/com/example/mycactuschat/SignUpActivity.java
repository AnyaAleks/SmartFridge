package com.example.mycactuschat;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout outlinedTextFieldNameSignUp, outlinedTextFieldSurnameSignUp, outlinedTextFieldEmailSignUp, outlinedTextFieldPasswordSignUp, outlinedTextFieldPhoneSignUp;
    private TextInputEditText textInputEditNameSignUp, textInputEditSurnameSignUp, textInputEditEmailSignUp, textInputEditPasswordSignUp, textInputEditPhoneSignUp;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z\\.]*@[A-Za-z]*\\.[a-z]{2,3}$");//"^[a-z0-9]+@[a-z]+.[a-z]{2,3}?");
   // private static boolean isEmailCorrect(String email){ return email.matches("^[A-Za-z\\.]*@[A-Za-z]*\\.[a-z]{2,3}$");}
    private static final Pattern PHONE_PATTERN = Pattern.compile("[0-9]{10}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //getSupportActionBar().hide();
        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_purple));
        getSupportActionBar().setTitle("Registration");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // кнопка назад
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Web HTML
        WebView webView = (WebView) findViewById(R.id.webvidewCactusShy);
        webView.loadUrl("file:///android_asset/cactus_shy.html");
        webView.setBackgroundColor(Color.TRANSPARENT);

        //TextInputLayout
        outlinedTextFieldNameSignUp = (TextInputLayout) findViewById(R.id.outlinedTextFieldNameSignUp);
        outlinedTextFieldSurnameSignUp = (TextInputLayout) findViewById(R.id.outlinedTextFieldSurnameSignUp);
        outlinedTextFieldEmailSignUp = (TextInputLayout) findViewById(R.id.outlinedTextFieldEmailSignUp);
        outlinedTextFieldPasswordSignUp = (TextInputLayout) findViewById(R.id.outlinedTextFieldPasswordSignUp);
        outlinedTextFieldPhoneSignUp = (TextInputLayout) findViewById(R.id.outlinedTextFieldPhoneSignUp);

        //TextInputEditText
        textInputEditNameSignUp = (TextInputEditText) findViewById(R.id.textInputEditNameSignUp);
        textInputEditSurnameSignUp = (TextInputEditText) findViewById(R.id.textInputEditSurnameSignUp);
        textInputEditEmailSignUp = (TextInputEditText) findViewById(R.id.textInputEditEmailSignUp);
        textInputEditPasswordSignUp = (TextInputEditText) findViewById(R.id.textInputEditPasswordSignUp);
        textInputEditPhoneSignUp = (TextInputEditText) findViewById(R.id.textInputEditPhoneSignUp);
    }

    //кнопка назад
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validatePhone() {
        String outlinedTextFieldPhoneSignUpString = outlinedTextFieldPhoneSignUp.getEditText().getText().toString().trim();

        if (!PHONE_PATTERN.matcher(outlinedTextFieldPhoneSignUpString).matches()) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.errorPhone, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        String outlinedTextFieldEmailSignUpString = outlinedTextFieldEmailSignUp.getEditText().getText().toString().trim();

        if (!EMAIL_PATTERN.matcher(outlinedTextFieldEmailSignUpString).matches()) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    public void gotoMainChatActivityInSignUp(View v){
        switch (v.getId()) {
            case R.id.gradientButtonRegistered:

                if (textInputEditNameSignUp.getText().toString().equals("")
                    || textInputEditSurnameSignUp.getText().toString().equals("")
                    || textInputEditEmailSignUp.getText().toString().equals("")
                    || textInputEditPasswordSignUp.getText().toString().equals("")
                    || textInputEditPhoneSignUp.getText().toString().equals("")) {

                    // Set error text
                    if (textInputEditNameSignUp.getText().toString().equals("")) {
                        outlinedTextFieldNameSignUp.setError("Error");
                    } else {
                        outlinedTextFieldNameSignUp.setError(null);
                    }

                    if (textInputEditSurnameSignUp.getText().toString().equals("")) {
                        outlinedTextFieldSurnameSignUp.setError("Error");
                    } else {
                        outlinedTextFieldSurnameSignUp.setError(null);
                    }

                    if (textInputEditEmailSignUp.getText().toString().equals("")) {
                        outlinedTextFieldEmailSignUp.setError("Error");
                    } else {
                        outlinedTextFieldEmailSignUp.setError(null);
                    }

                    if (textInputEditPasswordSignUp.getText().toString().equals("")) {
                        outlinedTextFieldPasswordSignUp.setError("Error");
                    } else {
                        outlinedTextFieldPasswordSignUp.setError(null);
                    }

                    if (textInputEditPhoneSignUp.getText().toString().equals("")) {
                        outlinedTextFieldPhoneSignUp.setError("Error");
                    } else {
                        outlinedTextFieldPhoneSignUp.setError(null);
                    }

                }
                else {
                    outlinedTextFieldNameSignUp.setError(null);
                    outlinedTextFieldSurnameSignUp.setError(null);

                    if(validatePhone()){
                        outlinedTextFieldPhoneSignUp.setError(null);

                        if(validateEmail()){
                            outlinedTextFieldEmailSignUp.setError(null);

                            if(textInputEditPasswordSignUp.getText().toString().length()>=6){
                                outlinedTextFieldPasswordSignUp.setError(null);

                                regStart(textInputEditPhoneSignUp.getText().toString()
                                        , textInputEditPasswordSignUp.getText().toString()
                                        , textInputEditNameSignUp.getText().toString()
                                        , textInputEditSurnameSignUp.getText().toString()
                                        ,textInputEditEmailSignUp.getText().toString());
                            }
                            else{
                                outlinedTextFieldPasswordSignUp.setError("Error");
                                Toast.makeText(getApplicationContext(), R.string.errorPassword, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            outlinedTextFieldEmailSignUp.setError("Error");
                        }
                    }
                    else{
                        outlinedTextFieldPhoneSignUp.setError("Error");
                    }
                }
                break;
            default:
                break;
        }
    }

    public void regStart(String phoneText, String passwordText, String nameText, String surnameText, String emailText){
        fStore.collection("Clients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int flagUserAlreadyExist=0;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Contacts data = new Gson().fromJson(document.getData().toString(), Contacts.class);
                        if(data.getEmailC().equals(emailText)){
                            flagUserAlreadyExist = 1;
                        }
                    }

                    if(flagUserAlreadyExist == 1){
                        Toast.makeText(SignUpActivity.this, R.string.already_registrate, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        regStartFinal(phoneText, passwordText, nameText, surnameText, emailText);
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, R.string.no_registrate, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void regStartFinal(String phoneText, String passwordText, String nameText, String surnameText, String emailText){
        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(SignUpActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();

                    String userId = mAuth.getCurrentUser().getUid();

                    //ЗАПИСАТЬ В json

                    Map<String,Object> userM = new HashMap<>();
                    userM.put("name", nameText);
                    userM.put("surname", surnameText);
                    userM.put("phone", "+7"+phoneText);
                    userM.put("email", emailText);
                    userM.put("id", userId);

                    fStore.collection("Clients").document(userId).set(userM).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUpActivity.this, R.string.success_registrate, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, R.string.no_registrate, Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }

}