package com.shuoyao.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by genji on 1/23/16.
 */
public class MeetupRequestAdapter extends ArrayAdapter<MeetupRequest> {
    private String TAG = MeetupRequestAdapter.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<MeetupRequest> meetupRequests = null;

    private MeetupRequestAdapter adapterInstance;

    public MeetupRequestAdapter(Context context, int resource, ArrayList<MeetupRequest> meetupRequests) {
        super(context, resource, meetupRequests);
        this.context = context;
        this.resource = resource;
        this.meetupRequests = meetupRequests;
        this.adapterInstance = this;
    }

    public MeetupRequestAdapter getAdapterInstance() {
        return adapterInstance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MeetupRequest meetupRequest = meetupRequests.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView nameTextView = (TextView)convertView.findViewById(R.id.meetup_request_name);
        TextView rangeTextView = (TextView)convertView.findViewById(R.id.meetup_request_start_date);
        nameTextView.setText(meetupRequest.getFrom().getName());
        // TODO: Jimmy, style the date range here. - finished, styled to say something like "Monday Jul 10 4:30 PM PST to Monday Jul 10 5:30 PM PST

        String[] dateList = meetupRequest.getRange().split("-");
        String dateRange = formatTime(new Date(Integer.parseInt(dateList[0])), new Date(Integer.parseInt(dateList[1])));
        rangeTextView.setText(dateRange);
        ImageButton accept = (ImageButton) convertView.findViewById(R.id.friend_request_accept);
        ImageButton deny = (ImageButton) convertView.findViewById(R.id.friend_request_deny);
        /*
        Drawable sourceDrawable = accept.getDrawable();

        //Convert drawable in to bitmap
        Bitmap sourceBitmap = Util.convertDrawableToBitmap(sourceDrawable);

        //Pass the bitmap and color code to change the icon color dynamically.

        Bitmap mFinalBitmap = Util.changeImageColor(sourceBitmap, 0x1B5F20);

        accept.setImageBitmap(mFinalBitmap);
        */
        final int positionToRemove = position;
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("senderUsername", meetupRequest.getEmail());
                params.put("recipientUsername", ParseUser.getCurrentUser().getUsername());
                params.put("dateRange", meetupRequest.getRange());
                ParseCloud.callFunctionInBackground("acceptMeetupRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String status, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, status);
                            meetupRequests.remove(positionToRemove);
                            getAdapterInstance().notifyDataSetChanged();
                        } else {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("senderUsername", meetupRequest.getEmail());
                params.put("recipientUsername", ParseUser.getCurrentUser().getUsername());
                params.put("dateRange", meetupRequest.getRange());
                ParseCloud.callFunctionInBackground("denyMeetupRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String status, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, status);
                            meetupRequests.remove(positionToRemove);
                            getAdapterInstance().notifyDataSetChanged();
                        } else {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        });

        return convertView;
    }
//    private String getTimeZone(){
//        Calendar cal = Calendar.getInstance();
//        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
//// Got local offset, now loop through available timezone id(s).
//        String [] ids = TimeZone.getAvailableIDs();
//        String name = null;
//        for (String id : ids) {
//            TimeZone tz = TimeZone.getTimeZone(id);
//            if (tz.getRawOffset() == milliDiff) {
//                // Found a match.
//                name = id;
//                break;
//            }
//        }
//        return name;
//    }
    private String formatTime(Date Date1, Date Date2) {
        long ts = System.currentTimeMillis();
        Date localTime = new Date(ts);
        Date localDate0 = new Date(Date1.getTime() - TimeZone.getDefault().getOffset(localTime.getTime()));
        Date localDate1 = new Date(Date2.getTime() - TimeZone.getDefault().getOffset(localTime.getTime()));
        Date1 = localDate0;
        Date2 = localDate1;
//        String timeZone = getTimeZone();
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd hh:mm a");
        String dateToStr0 = format.format(Date1);
        String dateToStr1 = format.format(Date2);
        return dateToStr0 + " " + " to " + dateToStr1;
//       return dateToStr0 + " " + timeZone + " to " + dateToStr1 + " " + timeZone;
    }
}
