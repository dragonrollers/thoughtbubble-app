package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnswerListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String TAG = "AnswerList Activity";

    // TODO Change this once we have authentication
    // *******NOTE THAT THIS IS DIFFERENT THAN THE USER THAT IS ASKING QUESTIONS (for demonstration purposes)*********
    private String THIS_USER_ID = "1";

    ArrayList<String> questionArray;
    ArrayAdapter<String> adapter;
    boolean unansweredView;

    // Firebase
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener unansweredQuestionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = DatabaseHelper.getInstance();

        setContentView(R.layout.activity_answer_list);

        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);
        profile.setBackgroundColor(unselected);
        ask.setBackgroundColor(unselected);
        answer.setBackgroundColor(selected);
        discover.setBackgroundColor(unselected);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//

        unansweredView = true;
        loadUnansweredQuestions();
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, questionArray
        );

        ListView list = (ListView) findViewById(R.id.ask_unanswered_list);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);

        Log.d(TAG, "ABOUT TO ATTACH UNANSWERED LISTENER");
        // Attach a listener to the adapter to populate it with the questions in the DB
        attachUnansweredQuestionsReadListener();

        switchToUnansweredHeader();



    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        String clickedName = questionArray.get(index); //todo: use this to retrieve the question, and question id
        if (unansweredView) {
            Intent writeAnswerActivity = new Intent(this, AnswerWriteActivity.class);
            //TODO: add the question id as an int to the intent
            writeAnswerActivity.putExtra("questionID", 123);
            startActivity(writeAnswerActivity);
        } else {
            Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);
            //TODO: add the question id as an int to the intent
            seeDetailedQuestion.putExtra("questionID", 123);
            startActivity(seeDetailedQuestion);
        }

    }

    private void loadUnansweredQuestions() {
        //TODO: the next lines of array creation should be replaced by pulling the
        //actual list of unanswered questions from the database
        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("UnansweredQuestion 1");
        dummyData.add("UnansweredQuestion 2");
        dummyData.add("UnansweredQuestion 3");
        dummyData.add("UnansweredQuestion 4");
        dummyData.add("UnansweredQuestion 5");

        questionArray = dummyData;

    }

    private void loadAnsweredQuestions() {
        System.out.println("LOAD ANSWERED QUESTIONS WAS CALLED");
        //TODO: the next lines of array creation should be replaced by pulling the
        //actual list of answered questions from the database
        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("AnsweredQuestion 1");
        dummyData.add("AnsweredQuestion 2");
        dummyData.add("AnsweredQuestion 3");

        questionArray = dummyData;


    }

    private void switchToUnansweredHeader() {
        TextView unansweredHeader = (TextView) findViewById(R.id.answer_unanswered_header_text);
        TextView answeredHeader = (TextView) findViewById(R.id.answer_answered_header_text);

        unansweredHeader.setTextColor(getResources().getColor(R.color.colorBlack));
        answeredHeader.setTextColor(getResources().getColor(R.color.colorGrey));
    }

    private void switchToAnsweredHeader() {
        TextView unansweredHeader = (TextView) findViewById(R.id.answer_unanswered_header_text);
        TextView answeredHeader = (TextView) findViewById(R.id.answer_answered_header_text);

        unansweredHeader.setTextColor(getResources().getColor(R.color.colorGrey));
        answeredHeader.setTextColor(getResources().getColor(R.color.colorBlack));
    }


    private void loadUnansweredContent() {
        unansweredView = true;
        loadUnansweredQuestions();
        adapter.clear();
        adapter.addAll(questionArray);
        adapter.notifyDataSetChanged();
        switchToUnansweredHeader();

        Log.d(TAG, "ABOUT TO ATTACH UNANSWERED LISTENER");
        // Attach a listener to the adapter to populate it with the questions in the DB
        attachUnansweredQuestionsReadListener();
    }

    private void loadAnsweredContent() {
        unansweredView = false;
        loadAnsweredQuestions();
        adapter.clear();
        adapter.addAll(questionArray);
        adapter.notifyDataSetChanged();
        switchToAnsweredHeader();
    }

    /**
     * ONCLICK METHOD: gotoUnansweredActivity
     * --------------------------------------
     */
    public void gotoUnansweredActivity(View view) {
        if (unansweredView) {
            Toast.makeText(this, "Already viewing unanswered Questions", Toast.LENGTH_SHORT).show();
        } else {
            loadUnansweredContent();
        }
    }

    /**
     * ONCLICK METHOD: gotoAnsweredActivity
     * --------------------------------------
     * Launches the answered activity
     *
     * @param view
     */
    public void gotoAnsweredActivity(View view) {
        if (unansweredView) {
            loadAnsweredContent();
        } else {
            Toast.makeText(this, "Already viewing answered Questions", Toast.LENGTH_SHORT).show();
        }
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    // This listener listens for any content added to the "questions" child of the database and when anything
    // is added, the question's text is added to the questionAdapter on the Discover page
    // TODO properly implement child removed/changed methods, or choose a different listener if more appropriate
    private void attachUnansweredQuestionsReadListener(){
        if (unansweredQuestionsListener == null) {
            unansweredQuestionsListener = new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "IN ON CHILD ADDED");

                    String questionKey = dataSnapshot.getValue(String.class);

                    DatabaseReference thisQuestion = mDatabaseHelper.questions.child(questionKey);

                    // Get the question from that part of the database
                    thisQuestion.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "IN SINGLE VALUE EVENT LISTENER");

                            Question question = dataSnapshot.getValue(Question.class);

                            // TODO add more than just question text
                            adapter.add(question.questionText);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            DatabaseReference incomingQuestions = mDatabaseHelper.users.child(THIS_USER_ID).child("incomingQuestions");
            incomingQuestions.addChildEventListener(unansweredQuestionsListener);
        }
    }

}
