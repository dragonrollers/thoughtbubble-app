package edu.stanford.cs147.thoughtbubble_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class IndivBoardView extends AppCompatActivity {

    // I will send a board string to here.
    // we need to get all the activities related to that board string and display it here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_indiv_board_view);

        String boardName = getIntent().getStringExtra("CURRENT_BOARD");
        readBoardContent(boardName);

    }

    private void readBoardContent(String boardName){
        // TODO: given a board name, need to retrieve.
        // For now, I am just using the fillers

        TextView title = (TextView) findViewById(R.id.boardTitle);
        title.setText(boardName);

        // TODO : PLEASE FILL IN THE QUESTIONARRAY WITH A REAL DATA
        ArrayList<Question> questionArray = new ArrayList<Question>();
        questionArray.add(new Question("YQ1", "YA1", "Critique1", "Grace", "Bonnie", "1"));
        questionArray.add(new Question("YQ2", "YA2", "Critique2", "Jenny", "Bonnie", "2"));

        QuestionAdapter questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questionArray);


        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setAdapter(questionAdapter);

    }

    public void finishActivity(View view) {
        finish();
    }
}
