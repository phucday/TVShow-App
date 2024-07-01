package com.example.navigationcomponent.ultilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BindingAvarComment {

    @BindingAdapter("android:imageAvarComment")
    public static void setImageURL(ImageView imageURL, String image){
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Cấu hình màu cao nhất
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length,options);
       imageURL.setImageBitmap(bitmap);
    }

}
