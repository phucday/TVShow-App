package com.example.navigationcomponent.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.navigationcomponent.DAO.UserDAO;
import com.example.navigationcomponent.activities.PersonInforActivity;
import com.example.navigationcomponent.activities.ResetPasswordActivity;
import com.example.navigationcomponent.activities.SignInActivity;
import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.FragmentUserBinding;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;


public class FragmentUser extends Fragment {

    private FragmentUserBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater,container,false);
        preferenceManager = new PreferenceManager(requireActivity().getApplicationContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("FragmentUser_SEND_DATA",preferenceManager.getString("testSendData"));
        Log.d("NameUserNew",preferenceManager.getString(Constants.KEY_NAME));

        loadImageUser();

        changeInfor();

        binding.btnLogOut.setOnClickListener(v -> logOut());

        binding.tvForgotPass.setOnClickListener(v -> startActivity(new Intent(requireActivity().getApplicationContext(), ResetPasswordActivity.class)));
    }


    private void changeInfor() {
        binding.tvInforPerson.setOnClickListener(v ->startActivity(new Intent(requireActivity().getApplicationContext(), PersonInforActivity.class)));
        binding.tvSetting.setOnClickListener(v -> Toast.makeText(requireActivity().getApplicationContext(), "Will be updated", Toast.LENGTH_SHORT).show());
    }

    private void logOut() {
        Toast.makeText(requireActivity(), "Signing out...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
        firestoreDb.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_FIREBASE_ID));
        HashMap<String,Object> ud = new HashMap<>();
        ud.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(ud)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(requireActivity(), SignInActivity.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Failed singing out: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadImageUser(){
        String sImage = preferenceManager.getString(Constants.KEY_IMAGE);
        byte[] bytes = Base64.decode(sImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Cấu hình màu cao nhất
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length,options);
        binding.imgProfile.setImageBitmap(bitmap);
    }

}