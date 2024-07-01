package com.example.navigationcomponent.ultilities;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BindingAdapters {

    @BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageURL, String URL){
        try {
            imageURL.setAlpha(0f);
            Picasso.get().load(URL).noFade().into(imageURL, new Callback() {
                @Override
                public void onSuccess() {
                    imageURL.animate().setDuration(300).alpha(1f).start();
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }catch (Exception exception){

        }
    }
}
