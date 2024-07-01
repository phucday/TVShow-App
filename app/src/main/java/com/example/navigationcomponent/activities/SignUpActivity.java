package com.example.navigationcomponent.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.ActivitySignUpBinding;
import com.example.navigationcomponent.models.User;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private String encodeImage;
    private PreferenceManager preferenceManager;
    private TVShowDatabase db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = TVShowDatabase.getTvShowDatabase(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        setListeners();

    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(view -> {
            if(isValidSignUpDetails()){
                authenSignUp(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString());
            }
        });
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void authenSignUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,task -> {
                   if(task.isSuccessful()){
                       FirebaseUser user = mAuth.getCurrentUser();
                       if(user != null){
                           signUp(user.getUid());
                       }
                   }else {
                       Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                   }
                });
    }
    private void signUp(String userId) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_IMAGE,encodeImage);
        user.put(Constants.KEY_PHONE,binding.inputPhoneNumber.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_FIREBASE_ID,userId);
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodeImage);
                    preferenceManager.putString(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
                    // add user to database
                    User userDb = new User((String) user.get(Constants.KEY_NAME), (String) user.get(Constants.KEY_EMAIL),
                            (String) user.get(Constants.KEY_PASSWORD), Integer.parseInt((String) user.get(Constants.KEY_PHONE)) , userId);
                    addUser(userDb);
                    getUserId(userId);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(runnable -> {
                    loading(false);
                    showToast(runnable.getMessage());
                });
    }

    private void getUserId(String firebaseId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().getUserByFirebaseId(firebaseId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    preferenceManager.putString(Constants.KEY_USER_ID, String.valueOf(integer));
                    Log.d("userIdSignUpActivity",integer+"");
                }));
    }
    private void addUser(User user){

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().insertUser(user)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("Main_AddUserDb:","ok");
                })
        );

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodeImage = encodeImage(bitmap);
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private String encodeImage(Bitmap bitmap){

        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth/ bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidSignUpDetails() {
        if (encodeImage == null) {
            showToast("Enter image");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter Name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (binding.conFirmPassword.getText().toString().isEmpty()) {
            showToast("Enter ConfirmPassword");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.conFirmPassword.getText().toString())) {
            showToast("Password & confirm password must be same");
            return false;
        }else{
            return true;
        }
    }
}