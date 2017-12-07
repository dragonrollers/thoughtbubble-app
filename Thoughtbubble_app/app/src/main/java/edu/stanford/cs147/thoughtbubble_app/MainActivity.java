package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // For debugging
    private static final String TAG = "MainActivity";

    boolean yourfeed = false;

    // Firebase database
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener allQuestionsListener;

    // Firebase auth
    private AuthenticationHelper authHelper;
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;


    ArrayList<Question> questionArray;
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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



        // Show your friend's feed first
        loadFriendsContent();




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

                    Question question = dataSnapshot.getValue(Question.class);

                    //Log.d(TAG, question.toString());

                    questionArray.add(question);
                    questionAdapter.notifyDataSetChanged();
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);

                    questionArray.remove(question);
                    questionAdapter.notifyDataSetChanged();
                }

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


    // Auth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Handle the case where this is a new user
                authHelper.handleNewUserCreation();
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


    // Sign-out menu (hamburger)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // sign out
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out

                                // MUST BE SET TO FALSE TO ALLOW FUTURE SIGN-IN
                                authHelper.signInAlreadyStarted = false;
                                onSignedOutCleanup();
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        authHelper.auth.addAuthStateListener(authHelper.authListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (authHelper.authListener != null) {
            authHelper.auth.removeAuthStateListener(authHelper.authListener);
        }
        detachAllQuestionsReadListener();
        questionAdapter.clear();

    }

    private void onSignedInInitialize(){

        authHelper.setThisUserID();
        loadYourContent();
        attachAllQuestionsReadListener();


    }

    private void onSignedOutCleanup(){
        questionAdapter.clear();
        detachAllQuestionsReadListener();

    }

    private void loadYourContent(){

        Log.d(TAG, "IN LOAD YOUR CONTENT");

        yourfeed = false;

        questionArray.clear();
        questionAdapter.notifyDataSetChanged();

        // TODO : REMOVE DUMMY DATA WHEN FULLY IMPLEMENTED
        questionArray.add(new Question("YQ1", "YA1", "Critique1", "Grace", "Bonnie", "1"));
        questionArray.add(new Question("YQ2", "YA2", "Critique2", "Jenny", "Bonnie", "2"));

        // TODO create the correct listeners and make the correct one detach and the other one attach (shouldn't be the same one)
        detachAllQuestionsReadListener();
        attachAllQuestionsReadListener();




        switchToYourHeader();

    }

    private void loadFriendsContent(){

        Log.d(TAG, "IN LOAD FRIENDS' CONTENT");

        yourfeed = true;

        questionArray.clear();
        questionAdapter.notifyDataSetChanged();

        // TODO : REMOVE DUMMY DATA WHEN FULLY IMPLEMENTED
        questionArray.add(new Question("Q1", "A1", "Critique1", "Jenny", "Bonnie", "1"));
        questionArray.add(new Question("Q2", "A2", "Critique2", "Po", "Grace", "2"));

        // TODO create the correct listeners and make the correct one detach and the other one attach (shouldn't be the same one)
        detachAllQuestionsReadListener();
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
}
