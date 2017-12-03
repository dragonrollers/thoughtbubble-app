package edu.stanford.cs147.thoughtbubble_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Jenny on 12/2/17.
 */

public class DialogFragmentProfile extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to delete this interest?").setPositiveButton("Ok", null)
                .setNegativeButton("No way", null).create();
    }
}
