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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;

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
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
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

        // Show your friend's feed first
        loadFriendsContent();

        // Attach a listener to the adapter to populate it with the questions in the DB
        mDatabaseHelper = DatabaseHelper.getInstance();
        //attachAllQuestionsReadListener();


        // Authentication
        authHelper = AuthenticationHelper.getInstance();
        auth = authHelper.auth;
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            // This firebaseAuth contains whether or not user is signed in or not
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "USER ALREADY SIGNED IN: " + user.getDisplayName());
                    onSignedInInitialize();

                } else {
                    // user is signed out
                    Log.d(TAG, "USER NOT SIGNED IN");
                    onSignedOutCleanup();

                    startActivityForResult(authHelper.getAuthUIInstance(), RC_SIGN_IN);
                }
            }
        };
        auth.addAuthStateListener(authListener);


    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        System.out.println(index);
        Question question = questionArray.get(index-1);
        Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);
        //TODO: add the question id as an int to the intent
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
    /* private void attachAllQuestionsReadListener(){
        if (allQuestionsListener == null) { // It start out null eventually when we add authentication
            allQuestionsListener = new ChildEventListener() {
                @Override

                // Whenever a child is added to the "questions" part of the database, add the new question to the adapter
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Question question = dataSnapshot.getValue(Question.class);

                    // TODO store more than just question text in question array
                    questionAdapter.add(question);
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
    } */


    // Auth
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Sign-in canceled", Toast.LENGTH_SHORT).show();
                finish();
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
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        //detachAllQuestionsReadListener();
        questionAdapter.clear();

    }

    private void onSignedInInitialize(){
        // Handle the case where this is a new user
        authHelper.handleNewUserCreation();
        authHelper.setThisUserID();

        //attachAllQuestionsReadListener();

    }

    private void onSignedOutCleanup(){
        questionAdapter.clear();
        //detachAllQuestionsReadListener();

    }

    private void loadYourContent(){

        yourfeed = false;

        // TODO : PLEASE FILL IN THE QUESTIONARRAY WITH A REAL DATA
        questionArray = new ArrayList<Question>();
        questionArray.add(new Question("YQ1", "YA1", "Critique1", "10:43", "Grace", "Bonnie"));
        questionArray.add(new Question("YQ2", "YA2", "Critique2", "10:13", "Jenny", "Bonnie"));

        questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questionArray);


        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setOnItemClickListener(this);
        listView1.setAdapter(questionAdapter);
        switchToYourHeader();
    }

    private void loadFriendsContent(){

        yourfeed = true;
        // TODO : PLEASE FILL IN THE QUESTIONARRAY WITH A REAL DATA
        questionArray = new ArrayList<Question>();
        questionArray.add(new Question("Q1", "A1", "Critique1", "10:43", "Jenny", "Bonnie"));
        questionArray.add(new Question("Q2", "A2", "Critique2", "10:13", "Po", "Grace"));

        questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questionArray);


        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setOnItemClickListener(this);
        listView1.setAdapter(questionAdapter);
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
