package com.example.chess2;

public class User {

    private String username;
    private long rating;

    public User(String username, long rating) {
        this.username = username;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public long getRating() {
        return rating;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
