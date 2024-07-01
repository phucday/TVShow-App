package com.example.navigationcomponent.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.databinding.ItemContainerCommentBinding;
import com.example.navigationcomponent.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<Comment> commentList;
    private LayoutInflater layoutInflater;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerCommentBinding commentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_container_comment,parent,false);
        return new CommentViewHolder(commentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bindComment(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ItemContainerCommentBinding binding;

        public CommentViewHolder(@NonNull ItemContainerCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindComment(Comment comment ){
            binding.setComment(comment);
        }
    }
}
