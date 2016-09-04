package com.shuoyao.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * Created by JimmyCheung on 4/25/16.
 */
public class RequestDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Log.e("RequestDialogFragment", "ASDASD");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString("dateTime"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES! params.put("recipientEmail", curr_id);
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("recipientEmail", getArguments().getString("recipientEmail"));
                        params.put("senderName", getArguments().getString("senderName"));
                        params.put("senderEmail", getArguments().getString("senderEmail"));
                        params.put("freeTimes", getArguments().getString("freeTimes"));
                        Toast.makeText(getActivity(), "Request Sent", Toast.LENGTH_LONG).show();

                        ParseCloud.callFunctionInBackground("sendMeetupRequest", params, new FunctionCallback<String>() {
                            @Override
                            public void done(String status, ParseException e) {
                                if (e == null) {
                                    Log.i("RequestDialogFragment", status);
                                } else {
                                    Log.e("RequestDialogFragment", e.toString());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}