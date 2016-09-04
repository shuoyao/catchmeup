package com.shuoyao.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sophia on 1/8/16.
 */
public class AddUserActivity extends Activity {

    private String TAG = AddUserActivity.class.getSimpleName();

    ArrayList<Friend> users;
    RequestUserAdapter requestUserAdapter;

    // UI references
    ListView requestUserListView;
    ImageButton requestUserSearch;
    EditText requestUserQuery;

    //display name, hangout history, and common time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        requestUserListView = (ListView) findViewById(R.id.request_user_listview);
        requestUserSearch = (ImageButton) findViewById(R.id.add_user_search);
        requestUserQuery = (EditText) findViewById(R.id.add_user_query);

        users = new ArrayList<>();

        requestUserAdapter = new RequestUserAdapter(this, R.layout.row_request_user, users);
        requestUserListView.setAdapter(requestUserAdapter);

        requestUserSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.clear();

                String query = requestUserQuery.getText().toString().toLowerCase();

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("email", ParseUser.getCurrentUser().getEmail());
                params.put("query", query);
                ParseCloud.callFunctionInBackground("searchResults", params, new FunctionCallback<HashMap<String, ArrayList<HashMap>>>() {
                    @Override
                    public void done(HashMap<String, ArrayList<HashMap>> data, ParseException e) {
                        if (e == null) {
                            for (HashMap<String, String> user : data.get("users")) {
                                users.add(new Friend(user.get("name"), user.get("email")));
                                requestUserAdapter.notifyDataSetChanged();
                            }
                        } else {
                            // Show error message
                            Log.e(TAG, e.toString());
                        }
                    }
                });

                requestUserAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUsers() {
        Friend user1 = new Friend("Genji Noguchi", "genji.noguchi@gmail.com");

        users.add(user1);
    }
}
