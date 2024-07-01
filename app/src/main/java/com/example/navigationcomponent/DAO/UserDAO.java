package com.example.navigationcomponent.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.navigationcomponent.models.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(User user);

    @Delete
    Completable deleteUser(User user);

    @Query("DELETE FROM users")
    Completable deleteAllUser();

    @Update
    Completable updateUser(User user);


    @Query("SELECT * FROM users")
    Flowable<List<User>> getAllUser();

    @Query("SELECT * FROM users WHERE users.id = :userId")
    Flowable<User> getUserById(int userId);

    @Query("SELECT users.id FROM users WHERE users.fireBaseId = :firebaseId")
    Flowable<Integer> getUserByFirebaseId(String firebaseId);
}
