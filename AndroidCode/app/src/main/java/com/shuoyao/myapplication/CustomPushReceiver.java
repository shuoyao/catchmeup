package com.shuoyao.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by genji on 1/13/16.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();


    private CustomNotification customNotification;

    private Intent parseIntent;

    public CustomPushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.d(TAG, "PUSH RECEIVED");
        super.onPushReceive(context, intent);

        if (intent == null) {
            return;
        }

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.d(TAG, "Push received: " + json);

            parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     *    json params:
     *      data: json object
     *        request_type: "FRIEND_REQUEST" or "MEETUP_REQUEST"
     *        request: json with request details
     *          from: json with user details
     *          timestamp: timestamp that the server received the request
     *          timeframe: the requested meetup time (MEETUP ONLY)
     *          message: Accompanying message
     */
    private void parsePushJson(Context context, JSONObject json) {
        try {
            // TODO: Make this code less bad, and implement notifications
            final Context finalContext = context;
            JSONObject data = json.getJSONObject("data");
            final String requestType = data.getString("request_type");

            final JSONObject request = data.getJSONObject("request");
            final JSONObject fromObject = request.getJSONObject("from");

            HashMap<String, Object> params = new HashMap<>();
            params.put("username", fromObject.get("email"));

            ParseCloud.callFunctionInBackground("getName", params, new FunctionCallback<String>() {
                @Override
                public void done(String name, ParseException e) {
                    if (e == null) {
                        try {
                            Friend from = new Friend(name, fromObject.getString("email"));

                            String timestamp = request.getString("timestamp");
                            String message = request.getString("message");

                            Intent resultIntent = new Intent(finalContext, MainActivity.class);

                            if (requestType.compareTo("FRIEND_REQUEST") == 0) {
                                showNotificationMessage(finalContext, from.getName(), message, resultIntent);
                            } else if (requestType.compareTo("MEETUP_REQUEST") == 0) {
                                showNotificationMessage(finalContext, from.getName(), message, resultIntent);
                            }
                        } catch (JSONException jsone) {

                        }
                    }
                }
            });


        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }


    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        customNotification = new CustomNotification(context);

        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        customNotification.showNotificationMessage(title, message, intent);
    }
}
