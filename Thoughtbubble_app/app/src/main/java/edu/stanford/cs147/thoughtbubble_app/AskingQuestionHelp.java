package edu.stanford.cs147.thoughtbubble_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Jenny on 12/2/17.
 */

public class AskingQuestionHelp extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return new AlertDialog.Builder(getActivity())
                .setMessage("This is the help message that we should eventually display").setPositiveButton("Got it!", null).create();
    }
}
