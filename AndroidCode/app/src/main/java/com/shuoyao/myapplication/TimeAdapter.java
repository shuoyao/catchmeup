
package com.shuoyao.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeAdapter extends ArrayAdapter<String> {
    private String TAG = TimeAdapter.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<String> commontime;

    public TimeAdapter(Context context, int resource, ArrayList<String> commontime) {
        super(context, resource, commontime);
        this.context = context;
        this.resource = resource;
        this.commontime = commontime;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        String time = commontime.get(position);

        if (time.length() > 0) {

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

            TextView timeStart = (TextView) convertView.findViewById(R.id.meetup_request_start_time),
                    timeEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_time),
                    dateStart = (TextView) convertView.findViewById(R.id.meetup_request_start_date),
                    dateEnd = (TextView) convertView.findViewById(R.id.meetup_request_end_date);

            //nameTextView.setText("fromname: " + fromname + " fromemail: " + fromemail + " time: " + time);
            timeStart.setText(start[TIME] + " " + start[AMPM]);
            timeEnd.setText(end[TIME] + " " + end[AMPM]);
            dateStart.setText(start[DAY] + ", " + start[MONTH] + " " + start[DATE]);
            dateEnd.setText(end[DAY] + ", " + end[MONTH] + " " + end[DATE]);
        }

        return convertView;
    }
}

