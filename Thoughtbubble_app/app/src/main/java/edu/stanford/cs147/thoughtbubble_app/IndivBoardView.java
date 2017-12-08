package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class IndivBoardView extends AppCompatActivity {

    private String TAG = "IndivBoardView";

    private DatabaseHelper DBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_indiv_board_view);

        String boardName = getIntent().getStringExtra("CURR_BOARD");
        String boardID = getIntent().getStringExtra("boardID");
        Log.d(TAG, "boardName=" + boardName + " boardID=" + boardID);
        readBoardContent(boardName, boardID);

    }

    private void readBoardContent(String boardName, String boardID){
        // TODO: given a board name, need to retrieve.
        // For now, I am just using the fillers

        TextView title = (TextView) findViewById(R.id.boardTitle);
        title.setText(boardName);

        //DBH.boards.child(boardID);

        ArrayList<Question> questionArray = new ArrayList<Question>();

        QuestionAdapter questionAdapter = new QuestionAdapter(this,
                R.layout.listview_item_row, questionArray);


        ListView listView1 = (ListView)findViewById(R.id.feed_list);
        listView1.setAdapter(questionAdapter);

    }

    public void finishActivity(View view) {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
        finish();
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
