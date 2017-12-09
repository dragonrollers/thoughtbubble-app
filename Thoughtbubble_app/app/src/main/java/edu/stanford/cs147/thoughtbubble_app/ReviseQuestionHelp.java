package edu.stanford.cs147.thoughtbubble_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Jenny on 12/2/17.
 */

public class ReviseQuestionHelp extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String HELP_MESSAGE = "If you do not like how the question is phrased, you should definitely fix it! If you like it, please toggle the switch off.";
        return new AlertDialog.Builder(getActivity())
                .setMessage(HELP_MESSAGE).setPositiveButton("Got it!", null).create();
    }
}
