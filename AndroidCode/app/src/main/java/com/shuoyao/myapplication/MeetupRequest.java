package com.shuoyao.myapplication;

/**
 * Created by genji on 1/23/16.
 */
public class MeetupRequest {
    private Friend from;
    private String range;


    public MeetupRequest() {}

    public MeetupRequest(Friend from, String range) {
        this.from = from;
        this.range = range;
    }

    public Friend getFrom() {
        return this.from;
    }

    public void setFrom(Friend f) {
        this.from = f;
    }

    public String getName() {
        return this.from.getName();
    }

    public String getEmail() {
        return this.from.getEmail();
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
