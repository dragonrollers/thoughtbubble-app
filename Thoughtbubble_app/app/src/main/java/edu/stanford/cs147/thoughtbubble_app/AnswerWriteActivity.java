package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private void loadQuestionText(int questionID) {
        TextView questionText = (TextView) findViewById(R.id.answerWrite_question_text);
        //TODO: load questions somehow
        questionText.setText("Question Dummy Text");
    }
}
