package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class AnswerWriteActivity extends AppCompatActivity {

    private String TAG = "AnswerWriteActivity";

    private DatabaseHelper DBH;
    private String questionID;
    private AuthenticationHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);

        DBH = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();

        setContentView(R.layout.activity_answer_write);

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


        Intent intent = getIntent();
        questionID = intent.getStringExtra("questionID");
        Log.d(TAG, "QuestionID: " + questionID);

        if (questionID == "") {
            //ERROR: the activity was not launched with an intent. Terminate
            finish();
        }

        // switch that controls whether to see the revised question

        Switch switchOne = (Switch) findViewById(R.id.switch_for_revising_question);

        String switchOn = intent.getStringExtra("HIDE");

        if(switchOn.equals("YES")){
            switchOne.setChecked(true);
        } else{
            switchOne.setChecked(false);
            EditText space_for_revising_question = (EditText) findViewById(R.id.answer_revisedQuestion_text);
            space_for_revising_question.setVisibility(View.GONE);
            EditText space_for_answering_question = (EditText) findViewById(R.id.answer_answer_text);
            space_for_answering_question.setHeight(800); // magic number to control the size
        }

        switchOne.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            EditText space_for_revising_question = (EditText) findViewById(R.id.answer_revisedQuestion_text);
                            space_for_revising_question.setVisibility(View.VISIBLE);
                            EditText space_for_answering_question = (EditText) findViewById(R.id.answer_answer_text);
                            space_for_answering_question.setHeight((int) (400)); // magic number to control the size
                        } else {
                            EditText space_for_revising_question = (EditText) findViewById(R.id.answer_revisedQuestion_text);
                            space_for_revising_question.setVisibility(View.GONE);
                            EditText space_for_answering_question = (EditText) findViewById(R.id.answer_answer_text);
                            space_for_answering_question.setHeight(800); // magic number to control the size
                        }
                    }
                });

        loadQuestionText(intent);
    }

    /**
     * METHOD: loadQuestionText
     * ------------------------
     * Helper method that initializes the view by loading question text in to the question field
     * and as a hint to the revised question field
     */
    private void loadQuestionText(Intent intent) {
        String questionText = intent.getStringExtra("questionText");
        TextView question = (TextView) findViewById(R.id.answerWrite_question_text);
        question.setText(questionText);
    }

    /**
     * ON CLICK METHOD: sendAnswer
     * ---------------------------
     * This method is called when the user clicks on send from the answer page
     * It loads the inputted text into the database and redirects to the discover page
     *
     * @param view
     */
    public void sendAnswer(View view) {
        EditText revisedQuestion = (EditText) findViewById(R.id.answer_revisedQuestion_text);
        EditText answer = (EditText) findViewById(R.id.answer_answer_text);

        final String revisedQuestionText = revisedQuestion.getText().toString();
        final String answerText = answer.getText().toString();

        Log.d(TAG, "About to send answer to question " + questionID);


        // Get the friends list and once that's done write the answer to DBw
        // TODO putting the write to DB inside the listener feels so hacky, is there a way to do it with an onComplete listener?
        DatabaseReference friendsRef = DBH.users.child(authHelper.thisUserID).child("friends");

        // Define the listener
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            // Populate the ArrayList with the updated data
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> friendsIDList = new ArrayList<>();

                // Get the updated friends list
                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                };
                HashMap<String, String> friendList = dataSnapshot.getValue(t);
                Log.d(TAG, "FRIENDS MAP: " + friendList.toString());

                // Populate the ArrayList with the updated data
                //friendsIDList.clear();
                if (friendList != null) { //Make sure that user has friends
                    friendsIDList = new ArrayList(friendList.keySet());

                }

                Log.d(TAG, "FRIENDS LIST: " + friendsIDList.toString());
                DBH.writeAnswerToDatabase(questionID, revisedQuestionText, answerText, friendsIDList);

                Toast.makeText(AnswerWriteActivity.this, "Answer Sent!", Toast.LENGTH_SHORT).show();
                finish();

                // Restart the answer activity to refresh the unanswered questions list
                Intent intent = new Intent(AnswerWriteActivity.this, AnswerListActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });

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


    /**
     * ON CLICK METHOD: cancelAnswer
     * ----------------------------
     *
     * @param view
     */
    public void cancelAnswer(View view) {
        finish();
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

}
