package com.example.navigationcomponent.listeners;

import com.example.navigationcomponent.models.TVShow;

public interface WatchListListener {

    void onTVShowClicked(TVShow tvShow);
    
    void removeTVShowFromWatchList(TVShow tvShow, int position);
}
