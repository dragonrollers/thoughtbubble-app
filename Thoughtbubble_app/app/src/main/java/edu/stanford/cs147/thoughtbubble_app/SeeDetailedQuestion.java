package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SeeDetailedQuestion extends AppCompatActivity {

    private Intent extraInfo;
    private String TAG = "SeeDetailedQuestion";

    private DatabaseHelper DBH;
    private AuthenticationHelper authHelper;
    private StorageHelper storageHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBH = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();
        storageHelper = StorageHelper.getInstance();

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_see_detailed_question);

        extraInfo = getIntent();

        TextView answer = (TextView) findViewById(R.id.detailedQ_answer);
        answer.setMovementMethod(new ScrollingMovementMethod());

        TextView critique = (TextView) findViewById(R.id.detailedQ_critique);
        critique.setMovementMethod(new ScrollingMovementMethod());

        fillWithQuestionDetails(extraInfo);

        setColor(extraInfo);

    }

    private void setColor(Intent extraInfo){



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

        if(extraInfo.getStringExtra("origin").equals("Answer")){
            answer.setBackgroundColor(selected);
            discover.setBackgroundColor(unselected);
        }
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//


    }

    private void fillWithQuestionDetails(Intent extraInfo){
        // TODO: Backend side need to retrieve the user image

        // Fill in profile picture of answerer
        final ImageView mImageView = (ImageView) findViewById(R.id.profile_image_detailed_question);
        final String answererID = extraInfo.getStringExtra("answererID");
        DatabaseReference answererHasProfile = DBH.users.child(answererID).child("hasProfile");
        answererHasProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean hasProfile = dataSnapshot.getValue(Boolean.class);
                loadProfileImage(hasProfile, answererID, mImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        String critiqueText = extraInfo.getStringExtra("critiqueText");
        String answerText = extraInfo.getStringExtra("answerText");
        String questionText = extraInfo.getStringExtra("questionText");

        TextView questionView = (TextView) findViewById(R.id.detailedQ_question);
        questionView.setText(questionText);

        TextView answerView = (TextView) findViewById(R.id.detailedQ_answer);
        answerView.setText(answerText);

        TextView critiqueView = (TextView) findViewById(R.id.detailedQ_critique);
        critiqueView.setText(critiqueText);

        // Fill in any saved thought
        String questionID = extraInfo.getStringExtra("questionID");
        final EditText thoughtEditText = (EditText) findViewById(R.id.detailedQ_thought_input);
        DatabaseReference thisSavedQuestion = DBH.users.child(authHelper.thisUserID).child("savedQuestions").child(questionID);
        thisSavedQuestion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String reflectionThoughtThing = dataSnapshot.getValue(String.class);
                thoughtEditText.setText(reflectionThoughtThing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void loadProfileImage(Boolean hasProfile, String answererID, ImageView mImageView) {
        Log.d(TAG, "answererID " + answererID);
        // Load the image using Glide
        if (hasProfile) {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef(answererID))
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

    public void saveToBoard(View view) {
        Intent boardFullView = new Intent(this, BoardFullView.class);
        EditText reflection = (EditText) findViewById(R.id.detailedQ_thought_input);
        String critiqueText = extraInfo.getStringExtra("critiqueText");
        String answerText = extraInfo.getStringExtra("answerText");
        String questionText = extraInfo.getStringExtra("questionText");
        String answererID = extraInfo.getStringExtra("answererID");
        String questionID = extraInfo.getStringExtra("questionID");
        Log.d(TAG, "Po is wondering if questionID is null here..." + questionID);
        boardFullView.putExtra("context", "save");
        boardFullView.putExtra("questionText", questionText);
        boardFullView.putExtra("answerText", answerText);
        boardFullView.putExtra("critiqueText", critiqueText);
        boardFullView.putExtra("reflection", reflection.getText() + "");
        boardFullView.putExtra("answererID", answererID);
        boardFullView.putExtra("questionID", questionID);
        boardFullView.putExtra("origin", "SeeDetailedQuestion");
        startActivity(boardFullView);
        finish();
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void viewProfile(View view) {
        if(extraInfo.getStringExtra("origin").equals("Main")){
            String answererID = extraInfo.getStringExtra("answererID");
            Intent intent = new Intent(this, PublicProfilePage.class);
            intent.putExtra("answererID", answererID);
            startActivity(intent);
        }
    }
}
