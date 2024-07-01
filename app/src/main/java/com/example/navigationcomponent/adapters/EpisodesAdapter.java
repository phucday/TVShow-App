package com.example.navigationcomponent.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.databinding.ItemContainerEpisodesBinding;
import com.example.navigationcomponent.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>{

    private List<Episode> episodes;
    private LayoutInflater layoutInflater;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerEpisodesBinding itemContainerEpisodesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_container_episodes,
                parent,false);
        return new EpisodesViewHolder(itemContainerEpisodesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bindEpisode(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    class EpisodesViewHolder extends RecyclerView.ViewHolder{
        ItemContainerEpisodesBinding itemContainerEpisodesBinding;
        public EpisodesViewHolder(  ItemContainerEpisodesBinding itemContainerEpisodesBinding) {
            super(itemContainerEpisodesBinding.getRoot());
            this.itemContainerEpisodesBinding = itemContainerEpisodesBinding;
        }

        private void bindEpisode(Episode episode){
            String title = "S";
            String season = episode.getSeason();
            if(season.length() == 1){
                season = "0".concat(season);
            }
            String episodeNumber = episode.getEpisode();
            if(episodeNumber.length() == 1){
                episodeNumber = "0".concat(episodeNumber);
            }
            episodeNumber = "E".concat(episodeNumber);
            title = title.concat(season).concat(episodeNumber);
            itemContainerEpisodesBinding.setTitle(title);
            itemContainerEpisodesBinding.setName(episode.getName());
            itemContainerEpisodesBinding.setAirDate(episode.getAirDate());
        }
    }

}
