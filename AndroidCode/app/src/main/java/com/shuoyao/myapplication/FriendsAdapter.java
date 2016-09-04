package com.shuoyao.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;


public class FriendsAdapter extends ArrayAdapter<Friend> {

    Context context;
    int resource;
    ArrayList<Friend> friends;
    public FriendsAdapter(Context context, int resource, ArrayList<Friend> friends) {
        super(context, resource, friends);
        this.context = context;
        this.resource = resource;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = friends.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView usernameTextView = (TextView) convertView.findViewById(R.id.friend_name_view);
        //TextView userContentTextView = (TextView) convertView.findViewById(R.id.friend_content_view);
        usernameTextView.setText(friend.getName());
       // userContentTextView.setText(friend.getEmail());

        /*
        final Switch follow_switch = (Switch) convertView.findViewById(R.id.row_friend_switch);
        follow_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    follow_switch.setText("following");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                    Set<String> followingUsers = sharedPreferences.getStringSet("Following", new HashSet<String>());
                    followingUsers.add("PUT_USERNAME_HERE");
                    sharedPreferencesEditor.putStringSet("Following", followingUsers);
                    sharedPreferencesEditor.commit();
                } else {
                    follow_switch.setText("not following");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                    Set<String> followingUsers = sharedPreferences.getStringSet("Following", new HashSet<String>());
                    followingUsers.remove("PUT_USERNAME_HERE");
                    sharedPreferencesEditor.putStringSet("Following", followingUsers);
                    sharedPreferencesEditor.commit();
                }
            }
        });
        */

        return convertView;

    }

}
