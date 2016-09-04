package com.shuoyao.myapplication;

/**
 * Created by sophia on 1/13/16.
 */
import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment1 extends Fragment {
    private String TAG = TabFragment1.class.getSimpleName();
    final ArrayList<Friend> friends = new ArrayList<Friend>();
    private RecyclerView recyclerView;
    private TomatoAdapter mtomatoAdapter;
    SwipeRefreshLayout friendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_people, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mtomatoAdapter = new TomatoAdapter(friends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mtomatoAdapter);


        friendList = (SwipeRefreshLayout) rootView.findViewById(R.id.friend_list_swipe);
        friendList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFriendList();
            }
        });

        refreshFriendList();
        mtomatoAdapter.notifyDataSetChanged();

        return rootView;
    }







//

//        View rootView = inflater.inflate(R.layout.tab_people, container, false);
//
//        ListView listView = (ListView) rootView.findViewById(R.id.listViewFriends);
//        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.row_friend, friends);
//        listView.setAdapter(friendsAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent i = new Intent(view.getContext(), FriendDetailsActivity.class);
//                i.putExtra(FRIEND_NAME, friends.get(position).getName());
//                i.putExtra(FRIEND_ID, friends.get(position).getEmail());
//                Log.d(TAG, "get intent" + friends.get(position).getName() + friends.get(position).getEmail());
//                startActivity(i);
//            }
//        });
//
//        friendList = (SwipeRefreshLayout) rootView.findViewById(R.id.friend_list_swipe);
//        friendList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshFriendList();
//            }
//        });
//
//        refreshFriendList();
//
//        return rootView;



    /* TODO: Write triggers for this function */
    public void refreshFriendList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", ParseUser.getCurrentUser().getEmail());
        ParseCloud.callFunctionInBackground("getFriends", params, new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> data, ParseException e) {
                if (e==null) {
                    // TODO: Locally store friends, and only ask the server for changes
                    Log.e(TAG, "" + data.size());
                    friends.clear();
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
                                    friends.add(new Friend(name, email));
                                    //
                                } else {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        });
                    }
                    mtomatoAdapter.notifyDataSetChanged();
                    friendList.setRefreshing(false);
                } else {
                    Log.e(TAG, e.toString());

                }
            }
        });

    }
}