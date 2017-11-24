package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> questionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionArray = new ArrayList<>();
        questionArray.add("Question 1");
        questionArray.add("Question 2");
        questionArray.add("Question 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, questionArray
        );

        ListView list = (ListView) findViewById(R.id.feed_list);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
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
}
