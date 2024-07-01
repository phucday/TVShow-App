package com.example.navigationcomponent.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.ActivityPersonInforBinding;
import com.example.navigationcomponent.databinding.ActivityResetPasswordBinding;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUp();

    }

    private void setUp() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.btnSendEmailNewPw.setOnClickListener(v -> {
            if(validInputEmail()){
                sendToEmail(binding.edtEmail.getText().toString());
                logOut();
            }
        });
    }
    private void sendToEmail(String email){
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this, "Password reset email sent! Check your email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logOut() {
        Toast.makeText(ResetPasswordActivity.this, "Signing out...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                firestoreDb.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_FIREBASE_ID));
        HashMap<String,Object> ud = new HashMap<>();
        ud.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(ud)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(ResetPasswordActivity.this, SignInActivity.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ResetPasswordActivity.this, "Failed singing out: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validInputEmail(){
        if(binding.edtEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(ResetPasswordActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString()).matches()) {
            Toast.makeText(ResetPasswordActivity.this, "Enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}