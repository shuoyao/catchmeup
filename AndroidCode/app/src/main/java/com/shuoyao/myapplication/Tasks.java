package com.shuoyao.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JimmyCheung on 10/28/15.
 */
public class Tasks extends AsyncTask<Void, Boolean, Boolean> {
    Timer timer = new Timer();
    private Context context;
    private ParseUser user;
    private String TAG = Tasks.class.getSimpleName();


    Tasks(Context cxt) {
        this.context = cxt;
        if (ParseUser.getCurrentUser() == null) {
            Log.i("abcd", "No Parse User Exists 1");
        } else {
            this.user = ParseUser.getCurrentUser();
        }

    }

    TimerTask dailyTask = new TimerTask() {

        public void run() {
            if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername() != null) {
                ArrayList<ArrayList<String>> allTimes = new ArrayList<ArrayList<String>>();
                final String[] FIELDS = new String[]{
                        CalendarContract.Calendars._ID
                };
                Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                long now = new Date().getTime();

                ContentUris.appendId(builder, now);
                ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * 7)); // checks for next week
                Uri eventUri = builder.build();
                Cursor cursor = null;
                ContentResolver cr = context.getContentResolver();
                cursor = cr.query(eventUri, new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, null);
                HashSet<String[]> result = new HashSet<String[]>();
                HashSet<String> calendarIDs = new HashSet<String>();
                try {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(1); // Date currently in unreadable format, need to change.
                            calendarIDs.add(id);
                            Log.i("abcd", id);

                            //final Date begin = new Date(cursor.getLong(3));
                            //final Date end = new Date(cursor.getLong(4));
                            String begin = cursor.getString(3);
                            String end = cursor.getString(4);
                            ArrayList<String> thisDate = new ArrayList();
                            Log.i("abcd", begin);
                            Log.i("abcd", end);
                            thisDate.add(begin);
                            thisDate.add(end);
                            allTimes.add(thisDate); //Currently using an ArrayList of an ArrayList of dates - can be changed for convenience
                        }
                        cursor.close();
                    }
                } catch (Exception e) {
                    Log.i("errors", e.getMessage() + " asd");
                }
               /* for (String id : calendarIDs) {
                    Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                    //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
                    long now = new Date().getTime();

                    ContentUris.appendId(builder, now);
                    ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 7); // checks for next week

                    Cursor eventCursor = cr.query(builder.build(),
                            new String[]{"begin", "end", "allDay"}, "Calendars._id=" + id,
                            null, "startDay ASC, startMinute ASC");
                    if (eventCursor.getCount() > 0) {
                        eventCursor.moveToFirst();
                        do {

                            final Date begin = new Date(eventCursor.getLong(0));
                            final Date end = new Date(eventCursor.getLong(1));
                            ArrayList<Date> thisDate = new ArrayList();
                            thisDate.add(begin);
                            thisDate.add(end);
                            allTimes.add(thisDate); //Currently using an ArrayList of an ArrayList of dates - can be changed for convenience
                        } while (eventCursor.moveToNext());
                    }
                    eventCursor.close();
                } */
                HashSet<String> freeTimes = new HashSet();
                now = new Date().getTime();
                long latestTime = 0;
                for (int i = 0; i < allTimes.size() - 1; i++) {
                    if (i == 0 && now + 3600000 <= Long.parseLong(allTimes.get(i).get(0))) {
                        freeTimes.add(Long.toString(now) + "-" + allTimes.get(i).get(0));
                    }
                    if (latestTime <= Long.parseLong(allTimes.get(i).get(1)) - 3600000) {
                        freeTimes.add(allTimes.get(i).get(1) + "-" + allTimes.get(i + 1).get(0));
                    }
                    if (i == allTimes.size() - 2 && Long.parseLong(allTimes.get(i + 1).get(1)) + 3600000 <= now + (DateUtils.DAY_IN_MILLIS * 7)) {
                        freeTimes.add(allTimes.get(i + 1).get(1) + "-" + Long.toString(now + (DateUtils.DAY_IN_MILLIS * 7)));
                    }
                    latestTime = Math.max(latestTime, Long.parseLong(allTimes.get(i).get(1)));
                }
                if (freeTimes.size() == 0) {
                    if (allTimes.size() == 1) {
                        if (now + 3600000 <= Long.parseLong(allTimes.get(0).get(0))) {
                            freeTimes.add(Long.toString(now) + '-' + allTimes.get(0).get(0));
                        } else if (Long.parseLong(allTimes.get(0).get(1)) <= now + (DateUtils.DAY_IN_MILLIS * 7)) {
                            freeTimes.add(allTimes.get(0).get(0) + '-' + Long.toString(now + (DateUtils.DAY_IN_MILLIS * 7)));
                        }
                    } else if (allTimes.size() == 0) {
                        freeTimes.add(Long.toString(now) + '-' + Long.toString(now + (DateUtils.DAY_IN_MILLIS * 7)));
                    }
                }
                if (ParseUser.getCurrentUser() == null) {
                    Log.i("abcd", "No Parse User Exists 2");
                } else {
                    String[] array = freeTimes.toArray(new String[freeTimes.size()]);
                    JSONArray jsonArray = new JSONArray();
                    if (array.length > 0) {
                        int len = array.length;
                        for (int i = 0; i < len; i++) {
                            jsonArray.put(array[i]);
                        }

                    }
                    Log.i("abcd", allTimes.toString());
                    user.put("freeTimes", jsonArray);
                    SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    sharedPreferencesEditor.putStringSet("OwnFreeTimes", freeTimes);

                    sharedPreferencesEditor.commit();
                    user.saveInBackground();
                }
            }
        }
    };
    TimerTask updateFollowed = new TimerTask() {
        public void run() {
            if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername() != null) {

        /*
                SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                HashSet<String> testData = new HashSet<String>();
                sharedPreferencesEditor.putStringSet("Following", testData);
                sharedPreferencesEditor.commit();
        */
                HashMap<String, Object> nameParam = new HashMap<String, Object>();
                nameParam.put("username", ParseUser.getCurrentUser().getEmail());
                ParseCloud.callFunctionInBackground("getFriendSettings", nameParam, new FunctionCallback<ArrayList<ArrayList<String>>>() {
                    @Override
                    public void done(ArrayList<ArrayList<String>> nameL, ParseException e) {
                        final ArrayList<ArrayList<String>> nameList = nameL;
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                        ArrayList<String> selfTimes = new ArrayList<String>();
                        selfTimes.addAll(sharedPreferences.getStringSet("OwnFreeTimes", new HashSet<String>()));
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        //Object[] followingUsersObjectArr = nameList.toArray();
                        Calendar cal = Calendar.getInstance();
                        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
                        ArrayList<ArrayList<String>> updatedArrayList = new ArrayList<ArrayList<String>>();
                        //final String[] followingUsersArray = Arrays.copyOf(followingUsersObjectArr, followingUsersObjectArr.length, String[].class);
                        for (int i = 0; i < nameList.size(); i++) {
                            ArrayList<String> friendDetailsObj = nameList.get(i);
                            Log.e(TAG, friendDetailsObj.get(2));
                            //String[] friendDetails =  Arrays.copyOf(friendDetailsObj, friendDetailsObj.length, String[].class);
                            if (friendDetailsObj.size() == 0) {
                                continue;
                            }
                            if (Integer.parseInt(friendDetailsObj.get(2)) > 0) {
                                friendDetailsObj.set(2, Integer.toString(Integer.parseInt(friendDetailsObj.get(2)) - 1));
                                //sharedPreferencesEditor.putStringSet(followingUsersArray[2], new HashSet<String>(Arrays.asList(friendDetails)));
                                updatedArrayList.add(friendDetailsObj);
                            } else {
                                //Log.e(TAG, "30s");
                                final int k = i;
                                params.put("friendName", nameList.get(i).get(0));
                                params.put("selfTimes", selfTimes);
                                params.put("timeDiff", milliDiff);
                                String[] dates = selfTimes.get(0).split("-");
                                //Log.e("WELP", Arrays.toString(dates));
                                ArrayList<Date> dateList = new ArrayList<Date>();
                                Date date1 = new Date(Long.parseLong(dates[0]));
                                Date date2 = new Date(Long.parseLong(dates[1]));
                                dateList.add(date1);
                                dateList.add(date2);
                                //Log.e("WELP", dateList.toString());
                                ParseCloud.callFunctionInBackground("getFreeTimes", params, new FunctionCallback<ArrayList<ArrayList<Date>>>() {
                                    @Override
                                    public void done(ArrayList<ArrayList<Date>> freeTimes, ParseException e) {
                                        if (e == null) {
                                            Log.e("WELP", freeTimes + "A");
                                            if (freeTimes == null) {
                                                freeTimes = new ArrayList<ArrayList<Date>>();
                                            }
                                            final int freeTimeSize = freeTimes.size();
                                            //do whatever you need with freeTimes.
                                            if (freeTimes.size() > 0) {
                                                HashMap<String, Object> params2 = new HashMap<String, Object>();
                                                Object username = params2.put("username", nameList.get(k).get(0));
                                                ParseCloud.callFunctionInBackground("getName", params2, new FunctionCallback<String>() {
                                                    @Override
                                                    public void done(String name, ParseException e) {
                                                        int icon = R.mipmap.ic_launcher;

                                                        int smallIcon = R.drawable.ic_push;

                                                        int mNotificationId = CircleApp.NOTIFICATION_ID;
                                                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                                                        intent.putExtra("friend_name", name);
                                                        intent.putExtra("friend_id", nameList.get(k).get(0));
                                                        String title = "Circle: New Mutual Free Times!";
                                                        String message = name + " has " + freeTimeSize + " free times with you!";
                                                        PendingIntent resultPendingIntent =
                                                                PendingIntent.getActivity(
                                                                        context,
                                                                        0,
                                                                        intent,
                                                                        PendingIntent.FLAG_CANCEL_CURRENT
                                                                );

                                                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                                                context);
                                                        Notification notification = mBuilder.setSmallIcon(smallIcon).setTicker(title).setWhen(0)
                                                                .setAutoCancel(true)
                                                                .setContentTitle(title)
                                                                .setStyle(inboxStyle)
                                                                .setContentIntent(resultPendingIntent)
                                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                                                                .setContentText(message)
                                                                .build();

                                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                        notificationManager.notify(mNotificationId, notification);
                                                    }
                                                });
                                            }
                                        } else {
                                            Log.e("WELP", e.toString());
                                        }
                                    }
                                });
                                if (friendDetailsObj.get(1).equals("-1")) {
                                    String defaultPref = sharedPreferences.getString("defaultFrequency", "30");
                                    friendDetailsObj.set(2, defaultPref);
                                } else {
                                    friendDetailsObj.set(2, friendDetailsObj.get(1));
                                }
                                updatedArrayList.add(friendDetailsObj);
                            }
                        }
                        ParseUser.getCurrentUser().put("FriendSettings", updatedArrayList);
                    }
                });
            }
        }
    };
    protected Boolean doInBackground(Void... params) {
        timer.schedule(dailyTask, 0, 30 * 1000); //in milliseconds
        timer.schedule(updateFollowed, 10, 60 * 1000);
        return null;
    }
}