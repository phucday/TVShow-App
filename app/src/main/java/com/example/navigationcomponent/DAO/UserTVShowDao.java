package com.example.navigationcomponent.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.UserTVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface UserTVShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUserTVShow(UserTVShow userTVShow);

    @Delete
    Completable deleteUserTVShow(UserTVShow userTVShow);

    @Query("SELECT * FROM UserTVShow WHERE UserTVShow.userId = :idUser")
    Flowable<UserTVShow> getUserTVShow(int idUser);
    @Query("DELETE FROM UserTVShow")
    Completable deleteAllUserTVShow();

    @Query("SELECT * FROM  UserTVShow WHERE  " + " usertvshow.tvShowId = :tvShowId AND usertvshow.userId = :idUser")
    Flowable<UserTVShow> checkTVShowOfUser(int idUser, int tvShowId);

    @Query("SELECT * FROM tvShows INNER JOIN UserTVShow ON tvshows.id = UserTVShow.tvShowId WHERE  UserTVShow.userId = :userId")
    Flowable<List<TVShow>> getTVShowForUser(int userId);

    // test save thu 2 userId khac nhau
}
