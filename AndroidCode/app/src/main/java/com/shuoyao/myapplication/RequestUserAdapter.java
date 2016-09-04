package com.shuoyao.myapplication;

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

/**
 * Created by genji on 1/14/16.
 */
public class RequestUserAdapter extends ArrayAdapter<Friend> {
    private String TAG = RequestUserAdapter.class.getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<Friend> users;

    private RequestUserAdapter mAdapter;

    public RequestUserAdapter(Context context, int resource, ArrayList<Friend> users) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        this.users = users;
        this.mAdapter = this;
    }

    public RequestUserAdapter getAdapterInstance() {
        return mAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend user = users.get(position);
        final int k = position;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView addUserName = (TextView) convertView.findViewById(R.id.add_user_name);
        Button addUserButton = (Button) convertView.findViewById(R.id.add_user_button);
        addUserName.setText(user.getName());
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("senderEmail", ParseUser.getCurrentUser().getEmail());
                params.put("recipientEmail", users.get(k).getEmail());
                ParseCloud.callFunctionInBackground("sendFriendRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if (e == null) {
                            // hurray!
                            // TODO: Show UI change to indicate the request went through
                            users.remove(k);
                            getAdapterInstance().getAdapterInstance().notifyDataSetChanged();
                            Log.d(TAG, "Sent friend request");
                        } else {
                            // Show error message
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        });



        return convertView;
    }

}

