package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // For debugging
    private static final String TAG = "MainActivity";

    boolean yourfeed = false;

    // Firebase database
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener allQuestionsListener;
    private ChildEventListener yourQuestionsListener;

    // Firebase auth
    private AuthenticationHelper authHelper;
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;


    private ArrayList<Question> questionArray;
    private QuestionAdapter questionAdapter;

    // For your questions feed
    private ArrayList<Question> answeredQuestionArray;
    private ArrayList<Question> unansweredQuestionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_main);

        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);
        profile.setBackgroundColor(unselected);
        ask.setBackgroundColor(unselected);
        answer.setBackgroundColor(unselected);
        discover.setBackgroundColor(selected);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//

        // Initialize database helper
        mDatabaseHelper = DatabaseHelper.getInstance();


        // Setting up places to display content
        questionArray = new ArrayList<Question>();
        answeredQuestionArray = new ArrayList<>();
        unansweredQuestionArray = new ArrayList<>();

        questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questionArray);

        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setOnItemClickListener(this);
        listView1.setAdapter(questionAdapter);


        // Authentication
        authHelper = AuthenticationHelper.getInstance();
        authHelper.authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            // This firebaseAuth contains whether or not user is signed in or not
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "USER ALREADY SIGNED IN: " + user.getDisplayName());
                    onSignedInInitialize();

                } else if (!authHelper.signInAlreadyStarted){

                    // Set to true to prevent weird nested sign-in bug
                    authHelper.signInAlreadyStarted = true;

                    // user is signed out
                    Log.d(TAG, "USER NOT SIGNED IN");
                    onSignedOutCleanup();

                    startActivityForResult(authHelper.getAuthUIInstance(), RC_SIGN_IN);
                }
            }
        };
        authHelper.auth.addAuthStateListener(authHelper.authListener);



    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        Question question = questionArray.get(index);
        Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);

        seeDetailedQuestion.putExtra("questionID", question.questionID);
        seeDetailedQuestion.putExtra("questionText", question.questionText);
        seeDetailedQuestion.putExtra("answerText", question.answerText);
        seeDetailedQuestion.putExtra("critiqueText", question.critiqueText);
        seeDetailedQuestion.putExtra("answererID", question.answererID);
        seeDetailedQuestion.putExtra("questionerID", question.questionerID);
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





    // Auth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Handle the case where this is a new user
                authHelper.handleNewUserCreation();
                onSignedInInitialize(); // Redundant if coming from main activity but not if coming from settings->sign-out
                Toast.makeText(MainActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();

                return;
            } else {
                // MUST BE SET TO FALSE TO ALLOW FUTURE SIGN-IN
                authHelper.signInAlreadyStarted = false;
                    // Sign in failed
                    if (response == null) {
                        // User pressed back button
                        Toast.makeText(MainActivity.this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        // Not the most graceful way to handle the errors - should probably have a 404 page-type-thing
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(MainActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
            }
        }




    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "IN ON RESUME");
        authHelper.auth.addAuthStateListener(authHelper.authListener);
        if (authHelper.auth.getCurrentUser() != null) {
            authHelper.setThisUserID();
            // TODO if we were being robust we would keep track of which part the user was actually on
            loadYourContent();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (authHelper.authListener != null) {
            authHelper.auth.removeAuthStateListener(authHelper.authListener);
        }
        detachAllQuestionsReadListener();
        detachYourQuestionsReadListener();
        questionAdapter.clear();

    }

    private void onSignedInInitialize(){
        Log.d(TAG, "IN ON SIGNED IN INITIALIZE");
        authHelper.setThisUserID();

        Log.d(TAG, "USER IS: " + authHelper.thisUserID);
        loadYourContent();


    }

    private void onSignedOutCleanup(){
        questionAdapter.clear();

        // Don't know which was attached upon sign-out
        // Both check to make sure listener isn't null before removing, so it's okay to call both
        detachAllQuestionsReadListener();
        detachYourQuestionsReadListener();

    }

    private void loadYourContent(){

        Log.d(TAG, "IN LOAD YOUR CONTENT");

        yourfeed = false;

        questionArray.clear();
        questionAdapter.notifyDataSetChanged();

        // TODO create the correct listeners and make the correct one detach and the other one attach (shouldn't be the same one)
        detachAllQuestionsReadListener();
        attachYourQuestionsReadListener();




        switchToYourHeader();

    }

    private void loadFriendsContent(){

        Log.d(TAG, "IN LOAD FRIENDS' CONTENT");

        yourfeed = true;

        questionArray.clear();
        questionAdapter.notifyDataSetChanged();

        // TODO create the correct listeners and make the correct one detach and the other one attach (shouldn't be the same one)
        detachYourQuestionsReadListener();
        attachAllQuestionsReadListener();

        switchToFriendHeader();

    }

    private void switchToYourHeader() {
        TextView friendHeader = (TextView) findViewById(R.id.friend_feed_text);
        TextView yourHeader = (TextView) findViewById(R.id.your_feed_text);

        yourHeader.setTextColor(getResources().getColor(R.color.colorBlack));
        friendHeader.setTextColor(getResources().getColor(R.color.colorGrey));
    }

    private void switchToFriendHeader() {
        TextView friendHeader = (TextView) findViewById(R.id.friend_feed_text);
        TextView yourHeader = (TextView) findViewById(R.id.your_feed_text);

        yourHeader.setTextColor(getResources().getColor(R.color.colorGrey));
        friendHeader.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    public void gotoYourFeed(View view) {
        if (yourfeed) {
            loadYourContent();
        } else {
            Toast.makeText(this, "Already viewing your feed!", Toast.LENGTH_SHORT).show();
        }

    }

    public void gotoFriendFeed(View view) {
        if (!yourfeed) {
            loadFriendsContent();
        } else {
            Toast.makeText(this, "Already viewing your friends' feed!", Toast.LENGTH_SHORT).show();
        }
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
                    //Log.d(TAG, "IN ON CHILD ADDED");

                    String questionID = dataSnapshot.getKey();
                    DatabaseReference questionRef = mDatabaseHelper.questions.child(questionID);

                    questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Question question = dataSnapshot.getValue(Question.class);
                            questionArray.add(0, question);

                            questionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.");
                        }
                    });
                    //Log.d(TAG, question.toString());


                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String questionID = dataSnapshot.getKey();
                    DatabaseReference questionRef = mDatabaseHelper.questions.child(questionID);

                    questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Question question = dataSnapshot.getValue(Question.class);
                            questionArray.remove(question);
                            questionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.");
                        }
                    });
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            mDatabaseHelper.users.child(authHelper.thisUserID).child("discoverQuestions").addChildEventListener(allQuestionsListener);
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

    /*
        For friend in friends list

     */




    // This listener listens to the outgoing questions portion of this user's database portion
    // TODO if developing further, this is a very inefficient way to get and sort the feed
    private void attachYourQuestionsReadListener(){
        if (yourQuestionsListener == null) { // It start out null eventually when we add authentication
            yourQuestionsListener = new ChildEventListener() {
                @Override

                // Whenever a child is added to the "questions" part of the database, add the new question to the adapter
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.d(TAG, "IN ON CHILD ADDED");
                    String questionID = dataSnapshot.getValue(String.class);
                    DatabaseReference thisQuestion = mDatabaseHelper.questions.child(questionID);
                    thisQuestion.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Question question = dataSnapshot.getValue(Question.class);

                            Log.d(TAG, question.toString());

                            if (question.answerText != null) {
                                answeredQuestionArray.add(question);
                                Collections.sort(answeredQuestionArray, new Comparator<Question>() {
                                    public int compare(Question q1, Question q2) {

                                        // Necessary because initially we had entries in database without timestamps
                                        if (q1.answerTimestamp==null || q2.answerTimestamp==null) return 1;

                                        return q2.answerTimestamp.compareTo(q1.answerTimestamp);
                                    }
                                });
                            } else {
                                unansweredQuestionArray.add(question);
                                Collections.sort(unansweredQuestionArray, new Comparator<Question>() {
                                    public int compare(Question q1, Question q2) {
                                        // Necessary because initially we had entries in database without timestamps
                                        if (q1.askTimestamp==null || q2.askTimestamp==null) return 1;

                                        return q2.askTimestamp.compareTo(q1.askTimestamp);
                                    }
                                });
                            }

                            questionArray.clear();
                            questionArray.addAll(answeredQuestionArray);
                            questionArray.addAll(unansweredQuestionArray);
                            questionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.");
                        }
                    });
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String questionID = dataSnapshot.getValue(String.class);
                    DatabaseReference thisQuestion = mDatabaseHelper.questions.child(questionID);

                    thisQuestion.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Question question = dataSnapshot.getValue(Question.class);
                            if (answeredQuestionArray.contains(question)) {
                                answeredQuestionArray.remove(question);
                            } else {
                                unansweredQuestionArray.remove(question);
                            }
                            questionArray.remove(question);
                            questionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.");
                        }
                    });
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };
            Log.d(TAG, "IN YOUR QUESTIONS LISTENER, USER IS: " + authHelper.thisUserID);
            DatabaseReference outgoingQuestionsRef = mDatabaseHelper.users.child(authHelper.thisUserID).child("outgoingQuestions");
            outgoingQuestionsRef.addChildEventListener(yourQuestionsListener);
        }
    }

    // Method to detatch the AllQuestionsReadListener (really just an example of how we would detatch such a listener)
    // Methods like this will be used once we do authentication
    private void detachYourQuestionsReadListener(){
        if (yourQuestionsListener != null) {
            unansweredQuestionArray.clear();
            answeredQuestionArray.clear();

            DatabaseReference outgoingQuestionsRef = mDatabaseHelper.users.child(authHelper.thisUserID).child("outgoingQuestions");
            outgoingQuestionsRef.removeEventListener(yourQuestionsListener);
            yourQuestionsListener = null;
        }
    }


    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
