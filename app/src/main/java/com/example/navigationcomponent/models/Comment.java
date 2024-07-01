package com.example.navigationcomponent.models;

import com.google.firebase.Timestamp;


public class Comment {
    private int userId;
    private int moveId;
    private String avarUser;
    private String content;
    private String timestamp;

    public Comment(){}
    public Comment(int userId, int moveId, String avarUser, String content, String timestamp) {
        this.userId = userId;
        this.moveId = moveId;
        this.avarUser = avarUser;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int moveId) {
        this.moveId = moveId;
    }

    public String getAvarUser() {
        return avarUser;
    }

    public void setAvarUser(String avarUser) {
        this.avarUser = avarUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
