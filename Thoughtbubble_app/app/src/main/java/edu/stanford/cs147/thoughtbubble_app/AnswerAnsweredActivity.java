package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AnswerAnsweredActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> answeredQArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_answered);

        answeredQArray = new ArrayList<>(); //TODO: actually load the answered question list

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, answeredQArray
        );

        ListView list = (ListView) findViewById(R.id.ask_answered_list);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        String clickedName = answeredQArray.get(index);
        Intent writeAnswerActivity = new Intent(this, AnswerWriteActivity.class);
        //TODO: add the question id as an int to the intent
        writeAnswerActivity.putExtra("questionID", 123);
        startActivity(writeAnswerActivity);
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
