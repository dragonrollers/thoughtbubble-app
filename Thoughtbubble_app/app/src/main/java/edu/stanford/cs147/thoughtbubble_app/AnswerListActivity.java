package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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


    //ArrayList<String> questionArray;
    //ArrayAdapter adapter;

    ArrayList<Question> questionArray;
    private AnonQuestionAdapter unansweredQuestionAdapter;
    private QuestionAdapter answeredQuestionAdapter;
    private ListView listView1;

    Question clickedQuestion;

    boolean unansweredView;

    // Firebase database
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener unansweredQuestionsListener;
    private ChildEventListener answeredQuestionsListener;
    private DatabaseReference incomingQuestions;

    // Authentication
    private AuthenticationHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);

        mDatabaseHelper = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();

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


        // Setting up places to display content
        questionArray = new ArrayList<Question>();


        unansweredQuestionAdapter= new AnonQuestionAdapter(this,
                R.layout.unanswered_question_items, questionArray);

        answeredQuestionAdapter = new QuestionAdapter(this,
                R.layout.no_profile_pic_question_row, questionArray);

        Log.d(TAG, "unanswered is null: " + (unansweredQuestionAdapter==null));
        Log.d(TAG, "answered is null: " + (answeredQuestionAdapter==null));
        Log.d(TAG, "listView is null: " + (listView1==null));


        listView1 = (ListView)findViewById(R.id.ask_unanswered_list);
        listView1.setOnItemClickListener(this);

        switchToUnansweredHeader();
        loadUnansweredContent();



    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        System.out.println("ITEM CLICKED");
        clickedQuestion = questionArray.get(index); //todo: use this to retrieve the question, and question id
        if (unansweredView) {
            Intent writeAnswerActivity = new Intent(this, AnswerWriteActivity.class);
            writeAnswerActivity.putExtra("questionID", clickedQuestion.questionID);
            writeAnswerActivity.putExtra("questionText", clickedQuestion.questionText);
            startActivity(writeAnswerActivity);
        } else {
            Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);
            seeDetailedQuestion.putExtra("answererID", clickedQuestion.answererID);
            seeDetailedQuestion.putExtra("questionID", clickedQuestion.questionID);
            seeDetailedQuestion.putExtra("questionText", clickedQuestion.questionText);
            seeDetailedQuestion.putExtra("answerText", clickedQuestion.answerText);
            seeDetailedQuestion.putExtra("origin", "Answer");
            startActivity(seeDetailedQuestion);
        }

    }

    private void loadUnansweredQuestions() {
        // TODO if no unanswered questions, display something appropriate!
        questionArray.clear();

        // If there are any incoming questions, attach a listener to the adapter to populate it with the questions in the DB
        Log.d(TAG, "ABOUT TO ATTACH UNANSWERED LISTENER");
        Log.d(TAG, "THIS USER ID " + authHelper.thisUserID);
        incomingQuestions = mDatabaseHelper.users.child(authHelper.thisUserID).child("incomingQuestions");
        incomingQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    detachAnsweredQuestionsReadListener();
                    attachUnansweredQuestionsReadListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Problem with to database: " + databaseError.toString());
            }
        });

    }

    private void loadAnsweredQuestions() {
        System.out.println("LOAD ANSWERED QUESTIONS WAS CALLED");
        // TODO if no answered questions, display something appropriate!
        questionArray.clear();

        // If there are any incoming questions, attach a listener to the adapter to populate it with the questions in the DB
        incomingQuestions = mDatabaseHelper.users.child(authHelper.thisUserID).child("incomingQuestions");
        incomingQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    detachUnansweredQuestionsReadListener();
                    attachAnsweredQuestionsReadListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Problem with to database: " + databaseError.toString());
            }
        });


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
        Log.d(TAG, "In load unanswered content");
        unansweredView = true;
        Log.d(TAG, "unanswered is null2: " + (unansweredQuestionAdapter==null));
        Log.d(TAG, "answered is null2: " + (answeredQuestionAdapter==null));
        Log.d(TAG, "listView is null2: " + (listView1==null));
        listView1.setAdapter(unansweredQuestionAdapter);
        Log.d(TAG, "Set unanswered adapter");
        loadUnansweredQuestions();
        unansweredQuestionAdapter.notifyDataSetChanged();
        switchToUnansweredHeader();

    }

    private void loadAnsweredContent() {
        unansweredView = false;
        Log.d(TAG, "unanswered is null3: " + (unansweredQuestionAdapter==null));
        Log.d(TAG, "answered is null3: " + (answeredQuestionAdapter==null));
        Log.d(TAG, "listView is null3: " + (listView1==null));
        listView1.setAdapter(answeredQuestionAdapter);
        loadAnsweredQuestions();
        answeredQuestionAdapter.notifyDataSetChanged();
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


    // This listener listens to any changes that happen on this user's incoming questions portion of the database
    // TODO properly implement child changed methods, or choose a different listener if more appropriate
    private void attachUnansweredQuestionsReadListener(){
        if (unansweredQuestionsListener == null) {
            unansweredQuestionsListener = new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.d(TAG, "IN ON CHILD ADDED");

                    String questionKey = dataSnapshot.getValue(String.class);

                    DatabaseReference thisQuestion = mDatabaseHelper.questions.child(questionKey);

                    // Get the question from that part of the database
                    thisQuestion.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.d(TAG, "IN SINGLE VALUE EVENT LISTENER");

                            Question question = dataSnapshot.getValue(Question.class);
                            question.questionID = dataSnapshot.getKey();

                            if (question.answerText == null) {
                                // TODO add more than just question text
                                questionArray.add(question);
                                //Log.d(TAG, question.toString());
                                unansweredQuestionAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error with database: " + databaseError.toString());
                        }
                    });

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);

                    // TODO add more than just question text
                    questionArray.remove(question.questionText);
                    unansweredQuestionAdapter.notifyDataSetChanged();
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            incomingQuestions.addChildEventListener(unansweredQuestionsListener);
        }
    }


    private void detachUnansweredQuestionsReadListener(){
        if (unansweredQuestionsListener != null) {
            incomingQuestions.removeEventListener(unansweredQuestionsListener);
            unansweredQuestionsListener = null;
        }
    }

    // This listener listens to any changes that happen on this user's incoming questions portion of the database
    // TODO properly implement child changed methods, or choose a different listener if more appropriate
    private void attachAnsweredQuestionsReadListener(){
        if (answeredQuestionsListener == null) {
            answeredQuestionsListener = new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.d(TAG, "IN ON CHILD ADDED");

                    String questionKey = dataSnapshot.getValue(String.class);

                    DatabaseReference thisQuestion = mDatabaseHelper.questions.child(questionKey);

                    // Get the question from that part of the database
                    thisQuestion.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.d(TAG, "IN SINGLE VALUE EVENT LISTENER");

                            Question question = dataSnapshot.getValue(Question.class);
                            question.questionID = dataSnapshot.getKey();

                            if (question.answerText != null) {
                                // TODO add more than just question text
                                questionArray.add(question);
                                answeredQuestionAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error with database: " + databaseError.toString());
                        }
                    });

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);

                    // TODO add more than just question text
                    questionArray.remove(question);
                    answeredQuestionAdapter.notifyDataSetChanged();
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            incomingQuestions.addChildEventListener(answeredQuestionsListener);
        }
    }

    private void detachAnsweredQuestionsReadListener(){
        if (answeredQuestionsListener != null) {
            incomingQuestions.removeEventListener(answeredQuestionsListener);
            answeredQuestionsListener = null;
        }
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
