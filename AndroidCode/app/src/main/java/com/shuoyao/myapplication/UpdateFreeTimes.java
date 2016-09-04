package com.shuoyao.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

import android.os.Handler;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JimmyCheung on 3/8/16.
 */
public class UpdateFreeTimes extends JobService {
    String TAG = "UpdateFreeTimes";
    ParseUser user = ParseUser.getCurrentUser();
    final Context context = this;
    private Handler mJobHandler = new Handler( new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msg ) {
            if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername() != null) {
                ArrayList<ArrayList<String>> allTimes = new ArrayList<ArrayList<String>>();
                final String[] FIELDS = new String[]{
                        CalendarContract.Calendars._ID
                };
                Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                long now = new Date().getTime();

                ContentUris.appendId(builder, now);
                ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * 14)); // checks for next week
                Uri eventUri = builder.build();
                Cursor cursor = null;
                ContentResolver cr = context.getContentResolver();
                cursor = cr.query(eventUri, new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "duration", "eventLocation"}, null, null, null);
                HashSet<String[]> result = new HashSet<String[]>();
                HashSet<String> calendarIDs = new HashSet<String>();
                try {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(1); // Date currently in unreadable format, need to change.
                            calendarIDs.add(id);
                            //Log.i("abcd", id + "ID");

                            //final Date begin = new Date(cursor.getLong(3));
                            //final Date end = new Date(cursor.getLong(4));
                            String begin = cursor.getString(3);
                            String end = cursor.getString(4);
                            ArrayList<String> thisDate = new ArrayList();
                           // Log.i("abcd", begin + "Start");
                            if(end == null) {
                                String duration = cursor.getString(5);
                                long totTime = 0;
                                String currTime = "";
                                if (duration != null) {
                                    for (int i = 0; i < duration.length(); i++) {
                                        try {
                                            currTime = currTime +  Integer.parseInt(String.valueOf(duration.charAt(i)));
                                        }
                                        catch(NumberFormatException e) {
                                            if (currTime != "") {
                                                if (duration.charAt(i) == 'S') {
                                                    totTime += Long.parseLong(currTime);
                                                } else if (duration.charAt(i) == 'M') {
                                                    totTime += Long.parseLong(currTime) * 60;
                                                } else if (duration.charAt(i) == 'H') {
                                                    totTime += Long.parseLong(currTime) * 3600;
                                                }
                                                currTime = "";
                                            }
                                        }
                                    }

                                    end = Long.toString(Long.parseLong(begin) + totTime);
                                }
                            }
                            //Log.i("abcd", end + " DurEnd");
                            //Log.i("abcd", end + "End");
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
                    Log.i("abcd", allTimes.toString() + "All");
                    user.put("freeTimes", jsonArray);
                    SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    sharedPreferencesEditor.putStringSet("OwnFreeTimes", freeTimes);

                    sharedPreferencesEditor.commit();
                    user.saveInBackground();
                }
            }
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }

    } );
    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(1);
        return false;
    }
}
