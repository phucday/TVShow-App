package com.example.navigationcomponent.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.responses.TVShowDetailsResponse;
import com.example.navigationcomponent.respositories.TVShowDetailsRepository;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowDatabase tvShowDatabase;
    public TVShowDetailsViewModel(@NonNull Application application){
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId){
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow){
        return tvShowDatabase.tvShowDao().addToWatchList(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWatchList(int tvShowId){
        return tvShowDatabase.tvShowDao().getTVShowFromWatchList(tvShowId);
    }

    public Completable removeTVShowFromWatchList(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchList(tvShow);
    }

}
