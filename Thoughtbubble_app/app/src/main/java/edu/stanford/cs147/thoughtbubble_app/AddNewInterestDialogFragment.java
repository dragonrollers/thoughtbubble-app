package edu.stanford.cs147.thoughtbubble_app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jenny on 12/2/17.
 */

public class AddNewInterestDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_new_interest_dialog_fragment, container, false);
        // Watch for button clicks.

        Button cancel = (Button) v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                // ((FragmentDialog)getActivity()).showDialog();
                dismiss();
            }
        });

        Button update = (Button) v.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                // ((FragmentDialog)getActivity()).showDialog();
                dismiss();
            }
        });

        return v;
    }

}
