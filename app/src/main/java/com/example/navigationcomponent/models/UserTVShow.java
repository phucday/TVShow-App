package com.example.navigationcomponent.models;

import androidx.room.Entity;

@Entity(primaryKeys = {"userId","tvShowId"})
public class UserTVShow {
    private int userId;
    private int tvShowId;

    public UserTVShow(int userId, int tvShowId) {
        this.userId = userId;
        this.tvShowId = tvShowId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTvShowId() {
        return tvShowId;
    }

    public void setTvShowId(int tvShowId) {
        this.tvShowId = tvShowId;
    }
}
