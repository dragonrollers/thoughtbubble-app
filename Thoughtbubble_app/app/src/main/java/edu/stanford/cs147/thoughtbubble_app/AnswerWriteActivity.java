package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class AnswerWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        int questionID = intent.getIntExtra("questionID", -1);

        if (questionID == -1) {
            //ERROR: the activity was not launched with an intent. Terminate
            finish();
        }

        // switch that controls whether to see the revised question

        Switch switchOne = (Switch) findViewById(R.id.switch_for_revising_question);
        switchOne.setChecked(true);


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

        loadQuestionText(questionID);
    }

    /**
     * METHOD: loadQuestionText
     * ------------------------
     * Helper method that initializes the view by loading question text in to the question field
     * and as a hint to the revised question field
     *
     * @param questionID
     */
    private void loadQuestionText(int questionID) {
        TextView questionText = (TextView) findViewById(R.id.answerWrite_question_text);
        //TODO: load questions somehow using the passed in question ID
        //String question = "Question Dummy Text";
        //questionText.setText(question);

        //EditText revisedQuestionPrompt = (EditText) findViewById(R.id.answer_revisedQuestion_text);
        //revisedQuestionPrompt.setHint(question);
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
        TextView answer = (TextView) findViewById(R.id.answerWrite_question_text);

        String revisedQuestionText = revisedQuestion.getText().toString();
        String answerText = answer.getText().toString();

        Toast.makeText(this, "Answer Sent!", Toast.LENGTH_SHORT).show();
        //TODO: load these fields into the database
        finish();
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

}
