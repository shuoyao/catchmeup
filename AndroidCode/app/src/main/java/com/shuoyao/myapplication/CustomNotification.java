package com.shuoyao.myapplication;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by genji on 1/13/16.
 */
public class CustomNotification {

    private String TAG = CustomNotification.class.getSimpleName();

    private Context mContext;

    public CustomNotification() {
    }

    public CustomNotification(Context mContext) {
        this.mContext = mContext;
    }

    /* TODO: Style notifications */
    public void showFriendRequestNotification(String fromName, String timestamp) {
        Intent i = new Intent(mContext, MainActivity.class);

        showNotificationMessage(fromName, fromName + "added you on Circle!", i);
    }

    public void showMeetupRequestNotification(String fromName, String timestamp) {
        Intent i = new Intent(mContext, MainActivity.class);

        showNotificationMessage(fromName, fromName + "is requesting a meetup on Circle!", i);
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(message)) {
            Log.e(TAG, "message was empty");
            return;
        }

        if (isAppIsInBackground(mContext)) {
            Log.e(TAG, "app is in background");
            // notification icon
            int icon = R.mipmap.ic_launcher;

            int smallIcon = R.drawable.ic_push;

            int mNotificationId = CircleApp.NOTIFICATION_ID;

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            Notification notification = mBuilder.setSmallIcon(smallIcon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(inboxStyle)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationId, notification);
            Log.e(TAG, "End");
        } else {
            Log.e(TAG, "App is not in background");
        }
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
