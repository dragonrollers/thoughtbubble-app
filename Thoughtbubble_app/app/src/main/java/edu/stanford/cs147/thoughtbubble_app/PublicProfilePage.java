package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PublicProfilePage extends AppCompatActivity {

    private String TAG = "PublicProfilePage";

    private ImageView mImageView;
    private DatabaseHelper DBH;
    private StorageHelper storageHelper;

    private ValueEventListener currUserListener;
    private User currUser;
    private ArrayList<String> topics;
    private Boolean fromMain = false;


    private ChildEventListener answeredQuestionsListener;
    private DatabaseReference incomingQuestions;


    // Firebase auth
    private AuthenticationHelper authHelper;
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;


    private ArrayList<Question> questionArray;
    private QuestionAdapter questionAdapter;

    //TODO Replace with userID of user we're currently viewing, loaded from previous view
    private String thisProfileUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authHelper = AuthenticationHelper.getInstance();

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);

        DBH = DatabaseHelper.getInstance();
        storageHelper = StorageHelper.getInstance();

        setContentView(R.layout.activity_public_profile_page);
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

        Intent data = getIntent();

        thisProfileUserID = data.getStringExtra("answererID");


        // Setting up places to display content
        questionArray = new ArrayList<Question>();

        questionAdapter = new QuestionAdapter(this,
                R.layout.no_profile_pic_question_row, questionArray);

        final ListView listView1 = (ListView)findViewById(R.id.feed_list);
        //listView1.setOnItemClickListener(this);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question question = questionArray.get(position);
                Intent seeDetailedQuestion = new Intent(getApplicationContext(), SeeDetailedQuestion.class);

                seeDetailedQuestion.putExtra("questionID", question.questionID);
                seeDetailedQuestion.putExtra("questionText", question.questionText);
                seeDetailedQuestion.putExtra("answerText", question.answerText);
                seeDetailedQuestion.putExtra("critiqueText", question.critiqueText);
                seeDetailedQuestion.putExtra("answererID", question.answererID);
                seeDetailedQuestion.putExtra("questionerID", question.questionerID);
                seeDetailedQuestion.putExtra("origin", "Main");
                startActivity(seeDetailedQuestion);
            }
        });
        listView1.setAdapter(questionAdapter);

        loadUserFromDatabase();

        loadAnsweredQuestions();
    }

    private void loadUserFromDatabase() {
        Log.d(TAG, "loadUserFromDatabase");
        final DatabaseReference currUserRef = DBH.users.child(thisProfileUserID);
        if (currUserListener == null) {
            currUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "getting currUser");
                    topics = currUser.getTopics();

                    loadProfileText();
                    loadProfileImage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        currUserRef.addListenerForSingleValueEvent(currUserListener);
    }

    private void loadProfileText() {
        //TODO: load these from databases using the id of the user
        String name = currUser.getFirstName() + " " + currUser.getLastName();
        String topicsText = "";
        for (int i = 0; i < topics.size(); i++) {
            topicsText += topics.get(i);
            if (i != topics.size() - 1) topicsText +=  ", ";
        }

        TextView nameField = (TextView) findViewById(R.id.personal_profile_name);
        nameField.setText(name);

        TextView topicsField = (TextView) findViewById(R.id.profile_topics);
        topicsField.setText(topicsText);
    }

    private void loadProfileImage(){
        //TODO Note this is untested because we haven't linked to this view
        mImageView = (ImageView) findViewById(R.id.profile_image);
        Log.d(TAG, "userID " + thisProfileUserID);
        // Load the image using Glide
        if (currUser.getHasProfile()) {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef(thisProfileUserID))
                    .asBitmap()
                    .into(mImageView);
        } else {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef("blank-user"))
                    .asBitmap()
                    .into(mImageView);
        }
    }



    private void loadAnsweredQuestions() {
        System.out.println("LOAD ANSWERED QUESTIONS WAS CALLED");
        // TODO if no answered questions, display something appropriate!
        questionArray.clear();

        // If there are any incoming questions, attach a listener to the adapter to populate it with the questions in the DB
        incomingQuestions = DBH.users.child(thisProfileUserID).child("incomingQuestions");
        incomingQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    attachAnsweredQuestionsReadListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Problem with to database: " + databaseError.toString());
            }
        });
    }

    // This listener listens to any changes that happen on this user's incoming questions portion of the database
    private void attachAnsweredQuestionsReadListener(){
        if (answeredQuestionsListener == null) {
            answeredQuestionsListener = new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.d(TAG, "IN ON CHILD ADDED");

                    String questionKey = dataSnapshot.getValue(String.class);

                    DatabaseReference thisQuestion = DBH.questions.child(questionKey);

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
                                questionAdapter.notifyDataSetChanged();
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
                    questionAdapter.notifyDataSetChanged();
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                public void onCancelled(DatabaseError databaseError) {}
            };

            incomingQuestions.addChildEventListener(answeredQuestionsListener);
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

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void askQuestion(View view) {
        AskPage(view);
    }







}


