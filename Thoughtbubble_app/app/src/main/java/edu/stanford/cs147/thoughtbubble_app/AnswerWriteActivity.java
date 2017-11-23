package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class AnswerWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_write);

        Intent intent = getIntent();
        int questionID = intent.getIntExtra("questionID", -1);

        if (questionID == -1) {
            //ERROR: the activity was not launched with an intent. Terminate
            finish();
        }

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
        String question = "Question Dummy Text";
        questionText.setText(question);

        EditText revisedQuestionPrompt = (EditText) findViewById(R.id.answer_revisedQuestion_text);
        revisedQuestionPrompt.setHint(question);
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

        //TODO: load these fields into the database
        finish();
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
