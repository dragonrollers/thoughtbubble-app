package edu.stanford.cs147.thoughtbubble_app;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoardFullView extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String TAG = "BoardFullView";

    ArrayList<String> BoardArray;
    private ArrayAdapter<String> BoardAdapter;
    // Firebase
    private DatabaseHelper mDatabaseHelper;
    private AuthenticationHelper authHelper;
    private ChildEventListener allQuestionsListener;
    private ValueEventListener currUserListener;

    private User currUser;

    // boolean to denote where the activity came from
    private boolean save = false;

    // extra data save space (from previous activity)
    private String critique = null;
    private String question = null;
    private String answer = null;
    private String answererID = null;

    // strings to remember the newly passed intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_board_full_view);

        String context = getIntent().getStringExtra("context");
        String originalActivity = getIntent().getStringExtra("origin");

        if(!originalActivity.equals("SeeDetailedQuestion")){
            Button newBoard = (Button) findViewById(R.id.newBoardButton);
            newBoard.setVisibility(View.GONE);
        }

        mDatabaseHelper = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();

        BoardArray = new ArrayList<String>();
        BoardAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, BoardArray
        );

        loadUserFromDatabase();

        if(context.equals("save")){
            // we do not want to finish -- we want to cancel
            Button saveButton = (Button) findViewById(R.id.finishButton);
            saveButton.setText("Cancel");
            save = true;
            // save extra data to the global variable
            saveDataToGlobal();

        }

        if(BoardArray.size()==0){
            TextView tv = (TextView) findViewById(R.id.help);
            tv.setText("THERE SEEMS TO BE NO BOARD! PLEASE MAKE ONE.");
        }
    }

    private void loadBoards(HashMap<String, String> boards) {
        for (Map.Entry<String, String> entry : boards.entrySet()) {
            DatabaseReference ref = mDatabaseHelper.boards.child(entry.getValue());
            ValueEventListener boardListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Board currBoard = dataSnapshot.getValue(Board.class);
                    BoardArray.add(currBoard.getName());
                    BoardAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(boardListener);

        }

        ListView list = (ListView) findViewById(R.id.feed_list);
        list.setOnItemClickListener(this);
        list.setAdapter(BoardAdapter);
    }

    private void loadUserFromDatabase() {
        Log.d(TAG, "loadUserFromDatabase");
        final DatabaseReference currUserRef = mDatabaseHelper.users.child(authHelper.thisUserID);
        if (currUserListener == null) {
            currUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "getting currUser");
                    HashMap<String, String> boards = currUser.getBoards();
                    if (boards != null) {
                        loadBoards(boards);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        currUserRef.addListenerForSingleValueEvent(currUserListener);
    }

    private void saveDataToGlobal(){
        critique = getIntent().getStringExtra("critiqueText");
        question = getIntent().getStringExtra("questionText");
        answer = getIntent().getStringExtra("answerText");
        answererID = getIntent().getStringExtra("answererID");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(save){

            // TODO: Save the question, answer, critique, and reflection (already saved into the global variables)
            // into the database and the board that is selected
            // WE NEED TO SAVE IT HERE, NOT IN THE NEXT ACTIVITY

            Toast alert_saved = Toast.makeText(this, "Your Reflection is saved to the board", Toast.LENGTH_LONG);
            alert_saved.show();

            // ** I AM SETTING THIS TO 0 BECAUSE I AM USING FILLERS
            // ONCE WE GET THE DATABASE WORKING, THE CODE SHOULD BE
            // BoardArray.get(i)
            // please text Jenny if this confuses you
            String boardName = BoardArray.get(0);
            Intent indivBoard = new Intent(this, IndivBoardView.class);
            indivBoard.putExtra("CURRENT_BOARD", boardName);
            startActivity(indivBoard);
        }
    }

    public void cancelView(View view) {
        finish();
    }

    public void NewBoard(View view) {
        // TODO: SAVE THE NEW BOARD INTO THE DATABASE

        FragmentManager fm = getFragmentManager();
        AddNewBoard addNewBoardFragment = new AddNewBoard();
        addNewBoardFragment.show(fm, null);

    }
}

