package com.shuoyao.myapplication;

/**
 * Created by sophia on 1/13/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment3 extends Fragment {
    private String TAG = TabFragment3.class.getSimpleName();
    private ArrayList<MeetupRequest> meetup = new ArrayList<MeetupRequest>();
    private TimeAdapter2 mtimeAdapter;
    SwipeRefreshLayout meetupListSwipe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_history, container, false);
        ListView meetupList = (ListView) rootView.findViewById(R.id.meetupListView);
        meetup = new ArrayList<MeetupRequest>();
        mtimeAdapter = new TimeAdapter2(getActivity(), R.layout.row_time2, meetup);
        meetupList.setAdapter(mtimeAdapter);
        meetupListSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.request_swipe);
        meetupListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });

        update();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        updateConfirmedRequests();
    }

    private void updateConfirmedRequests() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", ParseUser.getCurrentUser().getUsername());
        ParseCloud.callFunctionInBackground("getHistory", params, new FunctionCallback<ArrayList<ArrayList<String>>>() {
            @Override
            public void done(ArrayList<ArrayList<String>> data, ParseException e) {
                if (e == null) {
                    if (data == null) {
                        // Il n'y a pas de friend requests
                    } else {
                        meetup.clear();
                        mtimeAdapter.notifyDataSetChanged();
                        if (data.size() == 0) {

                        } else {
                            for (int i = 0; i < data.size(); i++ ) {
                                MeetupRequest mmr2 = new MeetupRequest(new Friend(data.get(i).get(1),data.get(i).get(0)), data.get(i).get(2));
                                meetup.add(mmr2);
                            }
                            mtimeAdapter.notifyDataSetChanged();

                        }
                    }
                } else {
                    Log.e(TAG + "outer", e.toString());
                }
            }
        });
        meetupListSwipe.setRefreshing(false);

    }

}



