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

public class TabFragment2 extends Fragment {
    private String TAG = TabFragment2.class.getSimpleName();
    private ArrayList<FriendRequest> friendRequests = new ArrayList<>();
    private ArrayList<MeetupRequest> mr = new ArrayList<MeetupRequest>();
    private FriendRequestAdapter friendRequestAdapter;
    private MrAdapter mMrAdapter;
    SwipeRefreshLayout meetupListSwipe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_notif, container, false);
        ListView friendRequestList = (ListView) rootView.findViewById(R.id.friendRequestListView);
        ListView mrList = (ListView) rootView.findViewById(R.id.mrListView);
        friendRequests = new ArrayList<FriendRequest>();
        mr = new ArrayList<MeetupRequest>();
        friendRequestAdapter = new FriendRequestAdapter(getActivity(), R.layout.row_friend_request, friendRequests);
        mMrAdapter = new MrAdapter(getActivity(), R.layout.row_mr, mr);
        friendRequestList.setAdapter(friendRequestAdapter);
        mrList.setAdapter(mMrAdapter);

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
    }

    private void update() {
        updateFriendRequests();
        updateMeetupRequests();
        meetupListSwipe.setRefreshing(false);

    }

    private void updateFriendRequests() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", ParseUser.getCurrentUser().getUsername());
        ParseCloud.callFunctionInBackground("getFriendRequests", params, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> data, ParseException e) {
                if (e == null) {
                    friendRequests.clear();
                    if (data == null) {
                        // Il n'y a pas de friend requests
                    } else {
                        friendRequests.clear();
                        Log.e(TAG, "this is data" + data.toString());
                        for (String user : data) {
                            final String email = user;
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("username", user);
                            ParseCloud.callFunctionInBackground("getName", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String name, ParseException e) {
                                    if (e == null) {
                                        if (name == null) {
                                            Log.e(TAG, "name is null");
                                        } else {
                                            Log.e(TAG, name);
                                        }
                                        friendRequests.add(new FriendRequest(new Friend(name, email)));
                                        friendRequestAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    // error
                    Log.e(TAG + "outer", e.toString());
                }
            }
        });
    }
    private void updateMeetupRequests() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", ParseUser.getCurrentUser().getUsername());
        ParseCloud.callFunctionInBackground("getMeetupRequests", params, new FunctionCallback<ArrayList<ArrayList<String>>>() {
            @Override
            public void done(ArrayList<ArrayList<String>> data, ParseException e) {
                if (e == null) {
                    Log.e(TAG, "no error");
                    mr.clear();
                    mMrAdapter.notifyDataSetChanged();
                    if (data.size() == 0) {
                        // Il n'y a pas de friend requests
                        Log.d(TAG, "data was null");
                    } else {
                        Log.d(TAG, data.toString());;
                        for (int i = 0; i < data.size(); i++ ) {
                            MeetupRequest mmr = new MeetupRequest(new Friend(data.get(i).get(1),data.get(i).get(0)), data.get(i).get(2));
                            mr.add(mmr);
                        }
                        mMrAdapter.notifyDataSetChanged();
                    }
                } else {
                    // error
                    Log.e(TAG + "outer", e.toString());
                }
            }
        });

    }

}