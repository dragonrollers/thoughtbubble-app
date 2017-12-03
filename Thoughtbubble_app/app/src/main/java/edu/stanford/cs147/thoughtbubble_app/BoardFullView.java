package edu.stanford.cs147.thoughtbubble_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class BoardFullView extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> BoardArray;
    private ArrayAdapter<String> BoardAdapter;
    // Firebase
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener allQuestionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_full_view);

        // TODO: fill the array with backend data
        BoardArray = new ArrayList<String>();
        BoardArray.add("Board1");
        BoardArray.add("Board2");
        BoardArray.add("Board3");

        BoardAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, BoardArray
        );

        ListView list = (ListView) findViewById(R.id.feed_list);
        list.setOnItemClickListener(this);
        list.setAdapter(BoardAdapter);

        // Attach a listener to the adapter to populate it with the questions in the DB
        mDatabaseHelper = DatabaseHelper.getInstance();
        attachAllQuestionsReadListener();
    }

    // This listener listens for any content added to the "questions" child of the database and when anything
    // is added, the question's text is added to the questionAdapter on the Discover page
    // TODO properly implement child removed/changed methods, or choose a different listener if more appropriate
    private void attachAllQuestionsReadListener(){
        if (allQuestionsListener == null) { // It start out null eventually when we add authentication
            allQuestionsListener = new ChildEventListener() {
                @Override

                // Whenever a child is added to the "questions" part of the database, add the new question to the adapter
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Question question = dataSnapshot.getValue(Question.class);

                    // TODO store more than just question text in question array
                    BoardAdapter.add(question.questionText);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            mDatabaseHelper.questions.addChildEventListener(allQuestionsListener);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // TODO: START NEW ACTIVITY
    }

    public void cancelView(View view) {
        finish();
    }
}

