package com.example.navigationcomponent.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchListViewModel extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;

    public WatchListViewModel(@NonNull Application application) {
        super(application);
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchList(){
        return tvShowDatabase.tvShowDao().getWatchList();
    }

    public Completable removeTVShowWatchList(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchList(tvShow);
    }
}
