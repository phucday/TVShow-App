package com.example.navigationcomponent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.ActivitySignInBinding;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;

    private PreferenceManager preferenceManager;
    private TVShowDatabase db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = TVShowDatabase.getTvShowDatabase(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            passData(preferenceManager.getString(Constants.KEY_USER_FIREBASE_ID));
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        setListeners();
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(view -> {
            if(isValidSingInDetail()){
                authenSignIn(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString());
            }
        });
        binding.tvForgotPw.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class)));
    }
    private void authenSignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            SignIn(user.getUid());
                        }
                    }else {
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void SignIn(String userIdFB){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userIdFB)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_FIREBASE_ID ,userIdFB);
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        String oldPw = documentSnapshot.getString(Constants.KEY_PASSWORD);
                        Log.d("oldPw",oldPw);
                        String newPw = binding.inputPassword.getText().toString();
                        if(!oldPw.equals(newPw)){
                            upDatePasswordIfReset(newPw,userIdFB);
                        }
                        // get userId
                        getUserId(userIdFB);
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }
    private void passData(String fireBaseId){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(fireBaseId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        Log.d("passDataNewUserPhone",preferenceManager.getString(Constants.KEY_PHONE));
                    }else{
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }
    private void upDatePasswordIfReset(String newPw,String uid){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> udPassword = new HashMap<>();
        udPassword.put(Constants.KEY_PASSWORD,newPw);
        database.collection(Constants.KEY_COLLECTION_USERS).document(uid)
                .update(udPassword)
                .addOnSuccessListener(unused -> {
                    Log.d("update Password after reset","success");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignInActivity.this, "Failed update Password after reset: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getUserId(String firebaseId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().getUserByFirebaseId(firebaseId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    preferenceManager.putString(Constants.KEY_USER_ID, String.valueOf(integer));
                }));
    }


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSingInDetail(){
        if(binding.inputEmail.getText().toString().isEmpty()){
            showToast("Enter Email");
            return false;
        }else if(binding.inputPassword.getText().toString().isEmpty()){
            showToast("Enter password");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Email not valid");
            return false;
        }
        return true;
    }
}