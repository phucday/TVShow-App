package com.example.navigationcomponent.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.navigationcomponent.DAO.TVShowDao;
import com.example.navigationcomponent.DAO.UserDAO;
import com.example.navigationcomponent.DAO.UserTVShowDao;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.User;
import com.example.navigationcomponent.models.UserTVShow;


@Database(entities = {TVShow.class, User.class, UserTVShow.class}, version = 2, exportSchema = false)
public abstract class TVShowDatabase extends RoomDatabase {

    private static TVShowDatabase tvShowDatabase;

    // Add migration
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `UserTVShow` (`userId` INTEGER NOT NULL, `tvShowId` INTEGER NOT NULL, PRIMARY KEY(`userId`, `tvShowId`))");
        }
    };
    public static synchronized TVShowDatabase getTvShowDatabase(Context context){
        if(tvShowDatabase == null){
            tvShowDatabase = Room.databaseBuilder(
                    context,
                    TVShowDatabase.class,
                    "tv_shows_db"
            ).fallbackToDestructiveMigration().addMigrations(MIGRATION_1_2).build();
        }
        return tvShowDatabase;
    }
    public abstract TVShowDao tvShowDao();
    public abstract UserDAO userDao();
    public abstract UserTVShowDao userTVShowDao();
}
