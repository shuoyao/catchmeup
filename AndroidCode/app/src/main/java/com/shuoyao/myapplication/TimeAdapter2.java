
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

import java.util.ArrayList;

public class TimeAdapter2 extends ArrayAdapter<MeetupRequest> {
    private String TAG = TimeAdapter2.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<MeetupRequest> commontime;
    private TimeAdapter2 mTimeAdapter2;

    public TimeAdapter2(FragmentActivity context, int resource, ArrayList<MeetupRequest> commontime) {
        super(context, resource, commontime);
        this.context = context;
        this.resource = resource;
        this.commontime = commontime;
        this.mTimeAdapter2 = this;
    }

    public TimeAdapter2 getAdapterInstance() {
        return mTimeAdapter2;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        if (commontime.size() > 0) {
            final MeetupRequest meetupRequest = commontime.get(position);

//            String ttime = meetupRequest.split(",")[2];
//            String time = ttime.substring(1, ttime.length() - 2);
//            String fromemail = meetupRequest.split(",")[0].substring(2);
//            String fromname = meetupRequest.split(",")[1].substring(1);

            String time = meetupRequest.getRange();
            String fromemail = meetupRequest.getEmail();
            String fromname = meetupRequest.getName();

            // Parse Time:
            String[] range = time.split(" to ");
            String[] start = range[0].split(" ");
            String[] end = range[1].split(" ");
            // Indices:
            int DAY = 0,
                    MONTH = 1,
                    DATE = 2,
                    TIME = 3,
                    AMPM = 4,
                    TIMEZONE = 5;

            TextView nameTextView = (TextView) convertView.findViewById(R.id.meetup_request_name);
            TextView timeStart = (TextView) convertView.findViewById(R.id.meetup_request_start_time),
                    timeEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_time),
                    dateStart = (TextView) convertView.findViewById(R.id.meetup_request_start_date),
                    dateEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_date);

            //nameTextView.setText("fromname: " + fromname + " fromemail: " + fromemail + " time: " + time);
            nameTextView.setText(fromname);
            timeStart.setText(start[TIME] + " " + start[AMPM]);
            timeEnd.setText(end[TIME] + " " + end[AMPM]);
            dateStart.setText(start[DAY] + ", " + start[MONTH] + " " + start[DATE]);
            dateEnd.setText(end[DAY] + ", " + end[MONTH] + " " + end[DATE]);
        }

        return convertView;
    }
}

