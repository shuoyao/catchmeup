package com.shuoyao.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
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
import android.os.Handler;
import android.os.Message;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JimmyCheung on 3/8/16.
 */
public class CheckManualFrequency extends JobService {
    String TAG = "CheckManualFrequency";
    Context context = this;
    private Handler mJobHandler = new Handler( new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msg ) {
            if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername() != null) {
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
                        if (!(nameList == null)) {
                            for (int i = 0; i < nameList.size(); i++) {
                                ArrayList<String> friendDetailsObj = nameList.get(i);
                                //Log.e(TAG, friendDetailsObj.get(2));
                                //String[] friendDetails =  Arrays.copyOf(friendDetailsObj, friendDetailsObj.length, String[].class);
                                if (friendDetailsObj.size() == 0) {
                                    continue;
                                }
                                if (Integer.parseInt(friendDetailsObj.get(2)) > 0) {
                                    friendDetailsObj.set(2, Integer.toString(Integer.parseInt(friendDetailsObj.get(2)) - 1));
                                    //sharedPreferencesEditor.putStringSet(followingUsersArray[2], new HashSet<String>(Arrays.asList(friendDetails)));
                                    updatedArrayList.add(friendDetailsObj);
                                } else {
                                    Log.e(TAG, "30s");
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
                    }
                });
            }
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }

    } );
    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 2, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(2);
        return false;
    }
}
