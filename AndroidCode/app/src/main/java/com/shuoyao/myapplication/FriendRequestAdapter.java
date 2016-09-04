package com.shuoyao.myapplication;

import android.content.Context;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;
import java.util.HashMap;


public class FriendRequestAdapter extends ArrayAdapter<FriendRequest> {
    private String TAG = FriendRequestAdapter.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<FriendRequest> friendRequests = null;

    private FriendRequestAdapter mFriendRequestAdapter;

    public FriendRequestAdapter(FragmentActivity context, int resource, ArrayList<FriendRequest> friendRequests) {
        super(context, resource, friendRequests);
        this.context = context;
        this.resource = resource;
        this.friendRequests = friendRequests;
        this.mFriendRequestAdapter = this;
    }

    public FriendRequestAdapter getAdapterInstance() {
        return mFriendRequestAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendRequest friendRequest = friendRequests.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView nameTextView = (TextView)convertView.findViewById(R.id.friend_request_name);
        nameTextView.setText(friendRequest.getName());

        ImageButton accept = (ImageButton) convertView.findViewById(R.id.friend_request_accept);
        ImageButton deny = (ImageButton) convertView.findViewById(R.id.friend_request_deny);
        final int positionToRemove = position;
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Jimmy, implement accept friend request - EDIT: VERIFIED WORKS implemented, have not added code in done function
                // TODO: make request disappear.
                // [PARAM] selfUsername: ParseUser.getCurrentUser().getUsername()
                // [PARAM] friendUsername: friendRequest.getEmail()
                // NOTE: you might need to make friendRequest final (final FriendRequest friendRequest).
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("friendUsername", friendRequest.getEmail());
                params.put("selfUsername", ParseUser.getCurrentUser().getUsername());
                Log.i(TAG, params.toString());
                ParseCloud.callFunctionInBackground("acceptFriendRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String status, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, status);
                            friendRequests.remove(positionToRemove);
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
                // TODO: Jimmy, implement deny friend request - EDIT: VERIFIED WORKS implemented, have not added code in done function
                // TODO: make request disappear.
                // [PARAM] selfUsername: ParseUser.getCurrentUser().getUsername()
                // [PARAM] friendUsername: friendRequest.getEmail()
                // NOTE: you might need to make friendRequest final (final FriendRequest friendRequest).
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("friendUsername", friendRequest.getEmail());
                params.put("selfUsername", ParseUser.getCurrentUser().getUsername());
                ParseCloud.callFunctionInBackground("denyFriendRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String status, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, status);
                            friendRequests.remove(positionToRemove);
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


}
