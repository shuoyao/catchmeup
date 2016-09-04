package com.shuoyao.myapplication;

public class FriendRequest {
    private Friend from;

    public FriendRequest(Friend from) {
        this.from = from;
    }

    public String getName() {
        return from.getName();
    }

    public String getEmail() {
        return from.getEmail();
    }
}
