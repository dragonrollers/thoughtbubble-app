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

        String HELP_MESSAGE = "What would you like to know about this friend?\n\n";
        HELP_MESSAGE += "Read through their Ask Me Abouts to see what they are comfortable talking about, ";
        HELP_MESSAGE += "and their previously answered questions to get some inspiration!";
        return new AlertDialog.Builder(getActivity())
                .setMessage(HELP_MESSAGE).setPositiveButton("Got it!", null).create();
    }
}
