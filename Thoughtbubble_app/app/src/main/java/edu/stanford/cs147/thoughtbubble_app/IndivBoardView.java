package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndivBoardView extends AppCompatActivity {

    private String TAG = "IndivBoardView";

    private DatabaseHelper DBH;
    private ValueEventListener currBoardListener;
    private Board currBoard;
    private ArrayList<Question> questions;
    private HashMap<String, String> questionReflections; // <questionID, reflection>
    private QuestionAdapter questionAdapter;

    private String boardName;
    private String boardID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBH = DatabaseHelper.getInstance();

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_indiv_board_view);

        questions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questions);

        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setAdapter(questionAdapter);

        boardName = getIntent().getStringExtra("CURR_BOARD");
        boardID = getIntent().getStringExtra("boardID");
        Log.d(TAG, "boardName=" + boardName + " boardID=" + boardID);
        TextView title = (TextView) findViewById(R.id.boardTitle);
        title.setText(boardName);
        readBoardContent();

    }

    private void readBoardContent() {
        
        DatabaseReference ref = DBH.boards.child(boardID);
        if (currBoardListener == null) {
            currBoardListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currBoard = dataSnapshot.getValue(Board.class);
                    questionReflections = currBoard.getQuestions();
                    if (questionReflections != null) {
                        loadQuestions();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        ref.addListenerForSingleValueEvent(currBoardListener);
    }

    private void loadQuestions() {
        for (Map.Entry<String, String> entry : questionReflections.entrySet()) {
            String questionID = entry.getKey();
            DatabaseReference ref = DBH.questions.child(questionID);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);
                    questions.add(question);
                    questionAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(listener);
        }
    }

    public void finishActivity(View view) {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
        finish();
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
