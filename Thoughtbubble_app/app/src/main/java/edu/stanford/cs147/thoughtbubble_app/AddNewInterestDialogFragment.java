package edu.stanford.cs147.thoughtbubble_app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;

/**
 * Created by Jenny on 12/2/17.
 */

public class AddNewInterestDialogFragment extends DialogFragment {

    private String TAG = "AddNewInterestDialogFragment";

    DatabaseHelper DBH;
    AuthenticationHelper authHelper;
    EditText interestField;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final DatabaseHelper DBH = DatabaseHelper.getInstance();
        final AuthenticationHelper authHelper = AuthenticationHelper.getInstance();

        View v = inflater.inflate(R.layout.add_new_interest_dialog_fragment, container, false);
        interestField = (EditText) v.findViewById(R.id.add_interest_view);
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

                String newInterest = interestField.getText().toString();
                if (newInterest.trim().length() != 0) { //user input validation
                    Log.d(TAG, "Adding interest: " + newInterest);
                    DBH.writeNewInterest(authHelper.thisUserID, newInterest);
                    dismiss();
                }
            }
        });

        return v;
    }

}
