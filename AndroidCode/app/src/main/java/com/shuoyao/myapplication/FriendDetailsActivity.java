package com.shuoyao.myapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class FriendDetailsActivity extends BaseActivity {
    private String TAG = FriendDetailsActivity.class.getSimpleName();
    public static String FRIEND_ID = "friend_id";
    public static String FRIEND_NAME = "friend_name";
    public static String curr_id = " ";
    public static ArrayList<Calendar[]> commontime;
    public static TimeAdapter aTimeAdapter;
    private static int userIndex = -1;
    SharedPreferences sharedPreferences;

    // UI References
    TextView nameView;
    TextView emailView;
    EditText updateFreq;
    //CalendarView freeTimeDisplay;
    WeekView mWeekView;
    List allSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String name = i.getStringExtra(FRIEND_NAME);
        String id = i.getStringExtra(FRIEND_ID);
        curr_id = id;
        nameView = (TextView) findViewById(R.id.friend_profile_name);
        emailView = (TextView) findViewById(R.id.friend_profile_email);
        updateFreq = (EditText) findViewById(R.id.friend_frequency_value);


        nameView.setText(name);
        emailView.setText(id);
        setupUI(findViewById(android.R.id.content));
        updateFreq.setCursorVisible(false);
        allSettings =  ParseUser.getCurrentUser().getList("FriendSettings");
        findIndex();
        if (userIndex != -1) {
            ArrayList<String> subList = (ArrayList<String>) allSettings.get(userIndex);
            if (subList.get(1).equals("-1")) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                updateFreq.setText(sharedPreferences.getString("defaultFrequency", "30"));
            }
            else{
                updateFreq.setText(subList.get(1));
            }
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setTitleTextColor(0xFFFFFFFF);
            setSupportActionBar(mToolbar);
            mToolbar.setTitle("Profile Page");
            analyze_freetime();
        }

    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        Calendar test1 = Calendar.getInstance();
        test1.set(Calendar.HOUR_OF_DAY, 15);
        test1.set(Calendar.MINUTE, 0);
        test1.set(Calendar.MONTH, newMonth);
        test1.set(Calendar.YEAR, newYear);
        Calendar test2 = (Calendar) test1.clone();

        test2.set(Calendar.HOUR_OF_DAY, 18);
        test2.set(Calendar.MONTH, newMonth);
        WeekViewEvent event2 = new WeekViewEvent(1, "Placeholder", test1, test2);
        event2.setColor(getResources().getColor(R.color.textBlock));
        events.add(event2);

        for (int i = 0; i < commontime.size(); i++) {
            WeekViewEvent event = new WeekViewEvent(1, "Placeholder", commontime.get(i)[0], commontime.get(i)[1]);
            event.setColor(getResources().getColor(R.color.textBlock));
            events.add(event);
        }
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Calendar startTime = event.getStartTime();
        Calendar endTime = event.getEndTime();
        String date = getMonth(startTime.get(Calendar.MONTH))+" "+startTime.get(Calendar.DAY_OF_MONTH) + getDayOfMonthSuffix(startTime.get(Calendar.DAY_OF_MONTH)) + ", " + startTime.get(Calendar.YEAR) ;
        String startTimeString = startTime.get(Calendar.HOUR)+":"+ String.format("%02d", startTime.get(Calendar.MINUTE));
        if (startTime.get(Calendar.AM_PM) == Calendar.PM) {
            startTimeString += " P.M.";
        }
        else {
            startTimeString += " A.M.";
        }
        String endTimeString = endTime.get(Calendar.HOUR)+":"+ String.format("%02d", endTime.get(Calendar.MINUTE));
        if (endTime.get(Calendar.AM_PM) == Calendar.PM) {
            endTimeString += " P.M.";
        }
        else {
            endTimeString += " A.M.";
        }
        String freeTimes = startTime.getTimeInMillis() + "-" + endTime.getTimeInMillis();
        String eventName = event.getName();
        String name = nameView.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("dateTime", "Are you sure you would like to send a meetup request to " + name + " for " + startTimeString + " to " + endTimeString + " on " + date + "?");
        bundle.putString("recipientEmail", curr_id);
        bundle.putString("senderName", ParseUser.getCurrentUser().get("name").toString());
        bundle.putString("senderEmail", ParseUser.getCurrentUser().getEmail());
        bundle.putString("freeTimes", freeTimes);
        RequestDialogFragment f = new RequestDialogFragment();
        f.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        f.show(ft, "Dialog");
        //ft.add(R.id.friend_details_content, f);
        //ft.commit();

        //Toast.makeText(this, "Request Sent to  " + name + " for " + startTimeString + " to " + endTimeString + " on " + date, Toast.LENGTH_LONG).show();
    }

    private void findIndex() {
        for (int i = 0; i < allSettings.size(); i++) {
            ArrayList<String> subList = (ArrayList<String>) allSettings.get(i);
            if (subList.get(0).equals(curr_id)) {
                //Log.e(TAG, curr_id);
                //Log.e(TAG, subList.get(1));
                userIndex = i;
            }
        }
    }
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(FriendDetailsActivity.this);
                    updateFreq.setCursorVisible(false);
                    validateInput(updateFreq);
                    return false;
                }

            });
        }
        else {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    updateFreq.setCursorVisible(true);
                    validateInput(updateFreq);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    private void validateInput(View v) {
        String newText = updateFreq.getText().toString();
        try {
            if (newText.length() > 0 && newText.charAt(0) == '-') {
                throw new NumberFormatException();
            }
            else {
                int newFriendFreq = Integer.parseInt(newText);
                ArrayList<String> subList = (ArrayList<String>) allSettings.get(userIndex);
                int numDaysLeft = Integer.parseInt(subList.get(2));
                int prevFriendFreq = Integer.parseInt(subList.get(1));
                if (newFriendFreq - prevFriendFreq + numDaysLeft <= 0){
                    subList.set(2, "0");
                }
                else {
                    subList.set(2, Integer.toString(newFriendFreq - prevFriendFreq + numDaysLeft));
                }
                ParseUser.getCurrentUser().put("FriendSettings", allSettings);
                ParseUser.getCurrentUser().saveInBackground();
            }
        }
        catch (NumberFormatException e) {
            updateFreq.setText("");
            Log.e(TAG, "Error: NumberFormatException in Text " + e);
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        // put previous stored frequency here
        //ParseUser.getCurrentUser().put("defaultFrequency", sharedPreferences.getString("defaultFrequency", "30"));
    };
    @Override
    protected void onResume() {
//        Log.d(TAG, "onResume the id is :" + FRIEND_ID);
        super.onResume();
        analyze_freetime();

    }

    private void analyze_freetime(){

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("friendName", curr_id);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> selfTimes = new ArrayList<String>();
        selfTimes.addAll(sharedPreferences.getStringSet("OwnFreeTimes", null));
        params.put("selfTimes", selfTimes);
        params.put("timeDiff", 0);
        //Log.d(TAG, params.toString());
        //set an adapter for commontimes
        //ListView friendRequestList = (ListView) findViewById(R.id.listViewMeetings);
        commontime = new ArrayList<Calendar[]>();
        //aTimeAdapter = new TimeAdapter(FriendDetailsActivity.this, R.layout.row_time, commontime);
        //friendRequestList.setAdapter(aTimeAdapter);

        //Log.d(TAG, commontime + " CD");
        ParseCloud.callFunctionInBackground("getFreeTimes", params, new FunctionCallback<ArrayList<ArrayList<Date>>>() {
            @Override
            public void done(ArrayList<ArrayList<Date>> freeTimes, ParseException e) {
                if (e == null) {
                    //Log.d(TAG, "sophialol" + freeTimes);
                    //do whatever you need with freeTimes.
                    for (ArrayList<Date> freeTime : freeTimes) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeZone(getTimeZone());
                        cal.setTime(freeTime.get(0));
                        Calendar cal2 = Calendar.getInstance();
                        cal.setTimeZone(getTimeZone());
                        cal.setTime(freeTime.get(1));
                        Calendar[] res = new Calendar[2];
                        res[0] = cal;
                        res[1] = cal2;
                        commontime.add(res);
                        //.d(TAG, "friday" + ft);
                    }
                } else {
                    //Log.e(TAG, e.toString());
                }
            }
        });
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);
        /*
        WeekView.MonthChangeListener mMonthChangeListener = new WeekView.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                // Populate the week view with some events.
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                for (int i = 0; i < commontime.size(); i++) {
                    Calendar startTime = Calendar.getInstance();
                    WeekViewEvent event = new WeekViewEvent(1, "Placeholder", commontime.get(i)[0], commontime.get(i)[1]);
                    event.setColor(getResources().getColor(R.color.textBlock));
                    events.add(event);
                }
                return events;
            }
        };
        */
// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.

        /*
        mWeekView.setOnEventListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("recipientEmail", curr_id);
                params.put("senderName", ParseUser.getCurrentUser().get("name").toString());
                params.put("senderEmail", ParseUser.getCurrentUser().getEmail());
                params.put("freeTimes", commontime.get(position));
                Log.d(TAG, params.toString());

                ParseCloud.callFunctionInBackground("sendMeetupRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String status, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, status);
                        } else {
                            Log.e(TAG, e.toString());
                        }
                    }
                });

                Toast.makeText(getApplicationContext(), "send request", Toast.LENGTH_SHORT).show();
            }
        });
        */
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



    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}
