package com.shuoyao.myapplication;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by genji on 1/13/16.
 */
public class CircleApp extends Application {
    private static String TAG = CircleApp.class.getSimpleName();

    public static final String PARSE_CHANNEL = "Circle";
    public static final int NOTIFICATION_ID = 100;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("129387dkslkay8t9an")
                .clientKey("10t8cjasl5wjt5fhha")
                .server("http://catchmeupmdb.herokuapp.com/parse/")   // '/' important after 'parse'
                .build());
        //Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        /*
        ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
//        currentInstall.put("user", ParseUser.getCurrentUser());
        currentInstall.saveInBackground();

        ParsePush.subscribeInBackground(PARSE_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Successfully subscr3ibed!");
                } else {
                    Log.d(TAG, e.toString());
                }
            }
        });

        */
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }


    public static boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null &&
                ParseUser.getCurrentUser().getUsername() != null;
    }

}
