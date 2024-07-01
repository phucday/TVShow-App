package com.example.navigationcomponent.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.ActivityPersonInforBinding;
import com.example.navigationcomponent.models.User;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PersonInforActivity extends AppCompatActivity {

    private ActivityPersonInforBinding binding;
    private PreferenceManager preferenceManager;
    private TVShowDatabase db;
    private FirebaseFirestore firestoreDb;
    private int userId;
    private String email;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonInforBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUp();

        upDate();
    }

    private void upDate() {
        binding.btnUpdatePersonInfor.setOnClickListener(v -> {
            upDateDB();
            upDataFB();
        });
    }

    private void setUp() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = TVShowDatabase.getTvShowDatabase(getApplicationContext());
        firestoreDb = FirebaseFirestore.getInstance();
        userId = Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));

        email = preferenceManager.getString(Constants.KEY_EMAIL);
        binding.tvEmail.setText(email);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }

    private void upDateDB(){
        int phone = Integer.parseInt(binding.edtPhone.getText().toString());
        String name = binding.edtName.getText().toString();
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(db.userDao().getUserById(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    String fbId = user.getFireBaseId();
                    String pw = user.getPassword();
                    if (!user.getName().equals(name) || !user.getEmail().equals(email) || user.getPhoneNumber() != phone) {
                        User userUd = new User(name,email,pw,phone,fbId);
                        userUd.setId(user.getId());
                        compositeDisposable.add(db.userDao().updateUser(userUd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Toast.makeText(PersonInforActivity.this, "Update success", Toast.LENGTH_SHORT).show()));
                        Log.d("upDateDB","successDB");
                    }else{
                        Toast.makeText(PersonInforActivity.this, "your data is identical", Toast.LENGTH_SHORT).show();
                    }
                }));

    }
    private void upDataFB(){
        HashMap<String,Object> userUpDate = new HashMap<>();
        userUpDate.put(Constants.KEY_NAME,binding.edtName.getText().toString());
        userUpDate.put(Constants.KEY_PHONE,binding.edtPhone.getText().toString());

        String fbID = preferenceManager.getString(Constants.KEY_USER_FIREBASE_ID);
        firestoreDb.collection(Constants.KEY_COLLECTION_USERS).document(fbID)
                .update(userUpDate)
                .addOnSuccessListener(unused -> {
                    Log.d("upDataFB","successFB");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PersonInforActivity.this, "PersonInforActivityupDataFB failed: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}