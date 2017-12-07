package edu.stanford.cs147.thoughtbubble_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Jenny on 12/2/17.
 */

public class DialogFragmentProfile extends DialogFragment {

    private String TAG = "DialogFragmentProfile";
    private DatabaseHelper DBH;
    private AuthenticationHelper authHelper;

    private int mNum;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DBH = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();

        mNum = getArguments().getInt("num");

        return new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to delete this interest?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Removing interest");
                        DBH.removeInterest(authHelper.thisUserID, mNum);
                    }
                })
                .setNegativeButton("No way", null).create();
    }
}
