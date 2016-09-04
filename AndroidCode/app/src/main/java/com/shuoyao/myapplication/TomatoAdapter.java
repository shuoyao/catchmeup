package com.shuoyao.myapplication;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sophia on 4/2/16.
 */

public class TomatoAdapter
        extends RecyclerView.Adapter<TomatoAdapter.TomatoHolder> {
    String TAG = TomatoAdapter.class.getSimpleName();

    ArrayList<Friend> friends;
    public static final String FRIEND_ID = "friend_id";
    public static final String FRIEND_NAME = "friend_name";
    public static int global_c = 0;

    public TomatoAdapter(ArrayList<Friend> friendList) {
        friends = friendList;
    }

    public class TomatoHolder extends RecyclerView.ViewHolder {
        private Friend mFriend;
        public TextView fname;


        public TomatoHolder(View itemView) {
            super(itemView);
            fname = (TextView) itemView.findViewById(R.id.fname);
        }

        public void bindFriend(Friend f) {
            mFriend = f;
            fname.setText(f.getName().replace(" ", "\n"));
            fname.bringToFront();
            fname.setZ(1);
        }
    }

    @Override
    public TomatoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        if (global_c % 2 == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_tomato_right, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_tomato_left, parent, false);
        }
        View subview = view.findViewById(R.id.tomato);
        global_c = global_c + 1;
        final TomatoHolder currentHolder = new TomatoHolder(view);
        subview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = currentHolder.getAdapterPosition();
                Intent i = new Intent(view.getContext(), FriendDetailsActivity.class);
                i.putExtra(FRIEND_NAME, friends.get(position).getName());
                i.putExtra(FRIEND_ID, friends.get(position).getEmail());
                v.getContext().startActivity(i);
            }
        });
        return currentHolder;
    }

    @Override
    public void onBindViewHolder(TomatoHolder holder, int position) {
        Friend f = friends.get(position);
        holder.bindFriend(f);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
//
//    @Override
//    public int getView() {
//
//    }

}

