package edu.stanford.cs147.thoughtbubble_app;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Jenny on 12/2/17.
 */

public class AddNewBoard extends DialogFragment {

    private String TAG = "AddNewBoard";

    DatabaseHelper DBH;
    AuthenticationHelper authHelper;
    EditText newBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final DatabaseHelper DBH = DatabaseHelper.getInstance();
        final AuthenticationHelper authHelper = AuthenticationHelper.getInstance();

        View v = inflater.inflate(R.layout.activity_add_new_board, container, false);
        newBoard = (EditText) v.findViewById(R.id.new_board_text);
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

                // TODO: GET NEW BOARD
                String new_board_name = newBoard.getText().toString();
                //String new_board_id = newBoard.getId();
                DBH.addBoard(authHelper.thisUserID, new_board_name, "0");
                Intent indivBoardView = new Intent(v.getContext(), IndivBoardView.class);
                indivBoardView.putExtra("CURRENT_BOARD", new_board_name);
                startActivity(indivBoardView);
                dismiss();
            }
        });

        return v;
    }

}