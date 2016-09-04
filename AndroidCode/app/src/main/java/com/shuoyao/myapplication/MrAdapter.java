package com.shuoyao.myapplication;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by genji on 1/23/16.
 */
public class MrAdapter extends ArrayAdapter<MeetupRequest> {
    private String TAG = MrAdapter.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<MeetupRequest> meetupRequests = new ArrayList<MeetupRequest>();

    private MrAdapter adapterInstance;
    private String time = "" ;
    private String fromemail= "" ;
    private String fromname = "" ;


    public MrAdapter(FragmentActivity context, int resource, ArrayList<MeetupRequest> meetupRequests) {
        super(context, resource, meetupRequests);
        this.context = context;
        this.resource = resource;
        this.meetupRequests = meetupRequests;
        this.adapterInstance = this;
    }

    public MrAdapter getAdapterInstance() {
        return adapterInstance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        if (meetupRequests.size() > 0) {
        final MeetupRequest meetupRequest = meetupRequests.get(position);
        //Todo: fix index error for null case
        Log.d("sodagreen in adapter", meetupRequest.toString());

//            String ttime = meetupRequest.split(",")[2];
//            time = ttime.substring(1, ttime.length() - 1);
//            fromemail = meetupRequest.split(",")[0].substring(2);
//            fromname = meetupRequest.split(",")[1].substring(1);
//            Log.d("sodagreen","update the mradapter");


            time = meetupRequest.getRange();
            fromemail = meetupRequest.getEmail();
            fromname = meetupRequest.getName();

            // Parse Time:
            Log.d(TAG, "time from meetuprequest is :" + time);
//            String[] range = time.split(" to ");
//            String[] start = range[0].split(" ");
//            String[] end = range[1].split(" ");
            String[] range = time.split("-");
            long istart = Long.parseLong(range[0]);
            long iend = Long.parseLong(range[1]);
            String start = "";
            String end = "";
//            Date[] r = formatTime(new Date(istart), new Date(iend));

            SimpleDateFormat newFormat = new SimpleDateFormat("EEEE MMM dd hh:mm a");
//            start= newFormat.format(r[0]);
//            end= newFormat.format(r[1]);
            start= newFormat.format(new Date(istart));
            end= newFormat.format(new Date(iend));

            // Indices:
            int     DAY = 0,
                    MONTH = 1,
                    DATE = 2,
                    TIME = 3,
                    AMPM = 4,
                    TIMEZONE = 5;

            ImageButton accept = (ImageButton) convertView.findViewById(R.id.meetup_request_accept);
            ImageButton deny = (ImageButton) convertView.findViewById(R.id.meetup_request_deny);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.meetup_request_name);
            TextView timeStart = (TextView) convertView.findViewById(R.id.meetup_request_start_time),
                     timeEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_time);
//                     dateStart = (TextView) convertView.findViewById(R.id.meetup_request_start_date),
//                     dateEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_date);

            //nameTextView.setText("fromname: " + fromname + " fromemail: " + fromemail + " time: " + time);
            nameTextView.setText(fromname);
//                       timeStart.setText(start[TIME] + " " + start[AMPM]);
//            timeEnd.setText(end[TIME] + " " + end[AMPM]);
//            dateStart.setText(start[DAY] + ", " + start[MONTH] + " " + start[DATE]);
//            dateEnd.setText(end[DAY] + ", " + end[MONTH] + " " + end[DATE]);
            timeStart.setText(start);
            timeEnd.setText(end);
//            dateStart.setText(start);
//            dateEnd.setText(end);


            final int positionToRemove = position;
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "accepted!", Toast.LENGTH_SHORT).show(); ;

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("friendUsername",fromemail);
                    params.put("friendName",fromname);
                    params.put("freeTimes",time);
                    params.put("selfUsername",ParseUser.getCurrentUser().getUsername());
                    params.put("selfName",ParseUser.getCurrentUser().get("name").toString());
                    Log.d(TAG, params.toString());
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
                    Toast.makeText(getContext(), "denied!", Toast.LENGTH_SHORT).show(); ;

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("friendUsername",fromemail);
                    params.put("friendName",fromname);
                    params.put("freeTimes",time);
                    params.put("selfUsername",ParseUser.getCurrentUser().getUsername());
                    params.put("selfName",ParseUser.getCurrentUser().get("name").toString());
                    Log.d(TAG, "sodagreen" + params.toString());
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


        } else {
            time = "";
            fromemail ="";
            fromname = "";
            Log.d("sodagreen","empty case");

        }
        return convertView;
    }

    private Date[] formatTime(Date Date1, Date Date2) {
        long ts = System.currentTimeMillis();
        Date localTime = new Date(ts);
        Date localDate0 = new Date(Date1.getTime() - TimeZone.getDefault().getOffset(localTime.getTime()));
        Date localDate1 = new Date(Date2.getTime() - TimeZone.getDefault().getOffset(localTime.getTime()));

        Date1 = localDate0;
        Date2 = localDate1;
        TimeZone timeZone = getTimeZone();
        SimpleDateFormat format = new SimpleDateFormat("EEEE MMM dd hh:mm a");
        Date[] result = new Date[2];
        result[0] = Date1;
        result[1] = Date2;
        return result;
        //String dateToStr0 = format.format(Date1);
        //String dateToStr1 = format.format(Date2);
        //return dateToStr0 + " " + timeZone + " to " + dateToStr1 + " " + timeZone;
    }

    private TimeZone getTimeZone(){
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
// Got local offset, now loop through available timezone id(s).
        String [] ids = TimeZone.getAvailableIDs();
        String name = null;
        TimeZone timeZone = null;
        for (String id : ids) {
            TimeZone tz = TimeZone.getTimeZone(id);
            if (tz.getRawOffset() == milliDiff) {
                // Found a match.
                name = id;
                timeZone = tz;
                break;
            }
        }
        return timeZone;
        //return name;
    }
}
