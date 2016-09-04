package com.shuoyao.myapplication;

/**
 * Created by sophia on 1/13/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.HashMap;

public class TabFragment4 extends Fragment {

    SharedPreferences sharedPreferences;
    String TAG = "TabFragment4";
    // UI References
    TextView nameEditText;
    TextView emailEditText;
    EditText freqEditText;
    Button logOutButton;
    Activity mActivity;

    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        View rootView = inflater.inflate(R.layout.tab_fragment_4, container, false);
        setupUI(rootView);
        nameEditText = (TextView) rootView.findViewById(R.id.settings_name_edittext);
        emailEditText = (TextView) rootView.findViewById(R.id.settings_name_email);
        freqEditText = (EditText) rootView.findViewById(R.id.default_frequency);
        freqEditText.setText(sharedPreferences.getString("defaultFrequency", "30"));
        logOutButton = (Button) rootView.findViewById(R.id.logout_button);
        Log.e(TAG, "View Created");
        freqEditText.setCursorVisible(false);
        /*
        freqEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN ||
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER ||
                                            event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION) {
                            Log.e(TAG, "Enter Pressed");
                            if (!event.isShiftPressed()) {
                                hideSoftKeyboard(mActivity);
                                freqEditText.setCursorVisible(false);
                                validateInput(freqEditText);
                                return true; // consum.e
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });
        */
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                HashMap<String, Object> params = new HashMap<String, Object>();
                ParseUser.getCurrentUser().put("defaultFrequency", sharedPreferences.getString("defaultFrequency", "30"));
                Intent i = new Intent(v.getContext(), WelcomeActivity.class);
                startActivity(i);
                }
            });

        /* Assume user is logged in */
        nameEditText.setText(ParseUser.getCurrentUser().get("name").toString());
        emailEditText.setText(ParseUser.getCurrentUser().getEmail());
        return rootView;
    }
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(mActivity);
                    freqEditText.setCursorVisible(false);
                    validateInput(freqEditText);
                    return false;
                }

            });
        }
        else {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    freqEditText.setCursorVisible(true);
                    validateInput(freqEditText);
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
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    private void validateInput(View v) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        Log.e(TAG, freqEditText.getText().toString());
        String newText = freqEditText.getText().toString();
        try {
            if (newText.length() > 0 && newText.charAt(0) == '-') {
                throw new NumberFormatException();
            }
            else {
                Integer.parseInt(newText);
                sharedPreferencesEditor.putString("defaultFrequency", freqEditText.getText().toString());
                sharedPreferencesEditor.commit();
            }
        }
        catch (NumberFormatException e) {
            freqEditText.setText(sharedPreferences.getString("defaultFrequency", "40"));
            Log.e(TAG, "Error: NumberFormatException in Text " + e);
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        ParseUser.getCurrentUser().put("defaultFrequency", sharedPreferences.getString("defaultFrequency", "40"));
    };
    public void Save(View view) {
        /* TODO: Implement this and the trigger for the function */
    }
}