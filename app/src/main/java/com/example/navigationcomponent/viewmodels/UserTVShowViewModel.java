package com.example.navigationcomponent.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.UserTVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class UserTVShowViewModel extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;

    public UserTVShowViewModel(@NonNull Application application) {
        super(application);
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public Completable insertUserTVShow(UserTVShow userTVShow){
        return tvShowDatabase.userTVShowDao().insertUserTVShow(userTVShow);
    }

    public Flowable<UserTVShow> getUserTVShow(int idUser){
        return tvShowDatabase.userTVShowDao().getUserTVShow(idUser);
    }

    public Completable deleteUserTVShow(UserTVShow userTVShow){
        return tvShowDatabase.userTVShowDao().deleteUserTVShow(userTVShow);
    }

    public Flowable<List<TVShow>> getTVShowForUser(int userId){
        return tvShowDatabase.userTVShowDao().getTVShowForUser(userId);
    }

    public Flowable<UserTVShow> checkTvShowUser(int userId, int tvShowId){
        return tvShowDatabase.userTVShowDao().checkTVShowOfUser(userId,tvShowId);
    }
}
