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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // For debugging
    private static final String TAG = "MainActivity";

    // Firebase database
    private DatabaseHelper mDatabaseHelper;
    private ChildEventListener allQuestionsListener;

    // Firebase auth
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    ArrayList<String> questionArray;
    private ArrayAdapter<String> questionAdapter;

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


        // Authentication
        auth = FirebaseAuth.getInstance();
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

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                            // Uncomment line below for Facebook
                                            //new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                            )
                                    )
                                    .build(),
                            RC_SIGN_IN);

                }

            }
        };

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

    // Sign-out menu
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
        detachAllQuestionsReadListener();
        questionAdapter.clear();

    }

    private void onSignedInInitialize(){
        // Can pass in something from authentication and et any variables that need to be set
        attachAllQuestionsReadListener();

    }

    private void onSignedOutCleanup(){
        questionAdapter.clear();
        detachAllQuestionsReadListener();

    }



}
