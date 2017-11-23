package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AnswerAnsweredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_answered);
    }

    /**
     * ONCLICK METHOD: gotoUnansweredActivity
     * --------------------------------------
     * Launches the unanswered activity
     *
     * @param view
     */
    public void gotoUnansweredActivity(View view) {
        Intent unansweredActivity = new Intent(this, AnswerUnansweredActivity.class);
        startActivity(unansweredActivity);
    }

    /**
     * ONCLICK METHOD: gotoAnsweredActivity
     * --------------------------------------
     * Since this is already the unanswered activity, we do not navigate
     * away, but display a short message instead
     *
     * @param view
     */
    public void gotoAnsweredActivity(View view) {
        String message = "Already viewing Answered questions";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
