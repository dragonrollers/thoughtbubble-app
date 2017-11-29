package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // For debugging
    private static final String TAG = "MainActivity";

    // Firebase
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener allQuestionsListener;

    ArrayList<String> questionArray;
    private ArrayAdapter<String> questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionArray = new ArrayList<String>();
        questionArray.add("Question 1");
        questionArray.add("Question 2");
        questionArray.add("Question 3");

        questionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, questionArray
        );

        ListView list = (ListView) findViewById(R.id.feed_list);
        list.setOnItemClickListener(this);
        list.setAdapter(questionAdapter);


        // Attach a listener to the adapter to populate it with the questions in the DB
        mDatabaseHelper = DatabaseHelper.getInstance();
        attachAllQuestionsReadListener();

    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        String clickedName = questionArray.get(index);
        Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);
        //TODO: add the question id as an int to the intent
        seeDetailedQuestion.putExtra("questionID", 123);
        startActivity(seeDetailedQuestion);

    }

    public void ProfilePage(View view) {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }

    public void AnswerPage(View view) {
        Intent intent = new Intent(this, AnswerListActivity.class);
        startActivity(intent);
    }

    public void AskPage(View view) {
        Intent intent = new Intent(this, AskWriteActivity.class);
        startActivity(intent);
    }

    public void DiscoverPage(View view) {
        // controlled by the main page
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
                    questionAdapter.add(question.questionText);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            mDatabaseHelper.questions.addChildEventListener(allQuestionsListener);
        }
    }

    // Method to detatch the AllQuestionsReadListener (really just an example of how we would detatch such a listener)
    // Methods like this will be used once we do authentication
    private void detachAllQuestionsReadListener(){
        if (allQuestionsListener != null) {
            mDatabaseHelper.questions.removeEventListener(allQuestionsListener);
            allQuestionsListener = null;
        }
    }

}
