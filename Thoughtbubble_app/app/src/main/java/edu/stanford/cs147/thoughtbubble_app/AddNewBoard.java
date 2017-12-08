package edu.stanford.cs147.thoughtbubble_app;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jenny on 12/2/17.
 */

public class AddNewBoard extends DialogFragment {

    private String TAG = "AddNewBoard";

    DatabaseHelper DBH;
    AuthenticationHelper authHelper;
    EditText newBoard;

    String questionID;
    String reflection;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final DatabaseHelper DBH = DatabaseHelper.getInstance();
        final AuthenticationHelper authHelper = AuthenticationHelper.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            questionID = bundle.getString("questionID");
            reflection = bundle.getString("reflection");
        }

        Log.d(TAG, "questionID=" + questionID + " reflection=" + reflection);

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

                final String new_board_name = newBoard.getText().toString();
                String boardID = DBH.addBoard(authHelper.thisUserID, new_board_name);
                DBH.addQuestionToBoard(authHelper.thisUserID, boardID, questionID, reflection);
                Intent indivBoardView = new Intent(v.getContext(), IndivBoardView.class);
                indivBoardView.putExtra("CURR_BOARD", new_board_name);
                indivBoardView.putExtra("boardID", boardID);
                startActivity(indivBoardView);
                dismiss();
            }
        });

        return v;
    }

}