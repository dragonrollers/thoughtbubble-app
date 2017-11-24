package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AnswerListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> questionArray;
    ArrayAdapter<String> adapter;
    boolean unansweredView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        unansweredView = true;
        loadUnansweredQuestions();
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, questionArray
        );

        ListView list = (ListView) findViewById(R.id.ask_unanswered_list);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);

        switchToUnansweredHeader();
    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        String clickedName = questionArray.get(index); //todo: use this to retrieve the question, and question id
        if (unansweredView) {
            Intent writeAnswerActivity = new Intent(this, AnswerWriteActivity.class);
            //TODO: add the question id as an int to the intent
            writeAnswerActivity.putExtra("questionID", 123);
            startActivity(writeAnswerActivity);
        } else {
            Intent seeDetailedQuestion = new Intent(this, SeeDetailedQuestion.class);
            //TODO: add the question id as an int to the intent
            seeDetailedQuestion.putExtra("questionID", 123);
            startActivity(seeDetailedQuestion);
        }

    }

    private void loadUnansweredQuestions() {
        //TODO: the next lines of array creation should be replaced by pulling the
        //actual list of unanswered questions from the database
        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("UnansweredQuestion 1");
        dummyData.add("UnansweredQuestion 2");
        dummyData.add("UnansweredQuestion 3");
        dummyData.add("UnansweredQuestion 4");
        dummyData.add("UnansweredQuestion 5");

        questionArray = dummyData;
    }

    private void loadAnsweredQuestions() {
        System.out.println("LOAD ANSWERED QUESTINOS WAS CALLED");
        //TODO: the next lines of array creation should be replaced by pulling the
        //actual list of answered questions from the database
        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("AnsweredQuestion 1");
        dummyData.add("AnsweredQuestion 2");
        dummyData.add("AnsweredQuestion 3");

        questionArray = dummyData;
    }

    private void switchToUnansweredHeader() {
        TextView unansweredHeader = (TextView) findViewById(R.id.answer_unanswered_header_text);
        TextView answeredHeader = (TextView) findViewById(R.id.answer_answered_header_text);

        unansweredHeader.setTextColor(getResources().getColor(R.color.colorBlack));
        answeredHeader.setTextColor(getResources().getColor(R.color.colorGrey));
    }

    private void switchToAnsweredHeader() {
        TextView unansweredHeader = (TextView) findViewById(R.id.answer_unanswered_header_text);
        TextView answeredHeader = (TextView) findViewById(R.id.answer_answered_header_text);

        unansweredHeader.setTextColor(getResources().getColor(R.color.colorGrey));
        answeredHeader.setTextColor(getResources().getColor(R.color.colorBlack));
    }


    private void loadUnansweredContent() {
        unansweredView = true;
        loadUnansweredQuestions();
        adapter.clear();
        adapter.addAll(questionArray);
        adapter.notifyDataSetChanged();
        switchToUnansweredHeader();
    }

    private void loadAnsweredContent() {
        unansweredView = false;
        loadAnsweredQuestions();
        adapter.clear();
        adapter.addAll(questionArray);
        adapter.notifyDataSetChanged();
        switchToAnsweredHeader();
    }

    /**
     * ONCLICK METHOD: gotoUnansweredActivity
     * --------------------------------------
     */
    public void gotoUnansweredActivity(View view) {
        if (unansweredView) {
            Toast.makeText(this, "Already viewing unanswered Questions", Toast.LENGTH_SHORT).show();
        } else {
            loadUnansweredContent();
        }
    }

    /**
     * ONCLICK METHOD: gotoAnsweredActivity
     * --------------------------------------
     * Launches the answered activity
     *
     * @param view
     */
    public void gotoAnsweredActivity(View view) {
        if (unansweredView) {
            loadAnsweredContent();
        } else {
            Toast.makeText(this, "Already viewing answered Questions", Toast.LENGTH_SHORT).show();
        }
    }
}
