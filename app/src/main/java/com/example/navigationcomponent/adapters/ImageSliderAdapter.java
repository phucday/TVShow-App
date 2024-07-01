package com.example.navigationcomponent.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.databinding.ItemContainerImageBinding;


public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {
    private String[] images;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] images){
        this.images = images;
    }
    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerImageBinding itemContainerImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_container_image
                ,parent, false );
        return new ImageSliderViewHolder(itemContainerImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.bindSliderImage(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class ImageSliderViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerImageBinding itemContainerImageBinding;
        public ImageSliderViewHolder(ItemContainerImageBinding itemContainerImageBinding) {
            super(itemContainerImageBinding.getRoot());
            this.itemContainerImageBinding = itemContainerImageBinding;
        }

        private void bindSliderImage(String imageURL){
            itemContainerImageBinding.setImageURL(imageURL);
        }
    }

}
