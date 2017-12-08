package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SeeDetailedQuestion extends AppCompatActivity {

    private Intent extraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Makes status bar black and hides action bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ask_write);
        setContentView(R.layout.activity_see_detailed_question);



        extraInfo = getIntent();

        fillWithQuestionDetails(extraInfo);

        setColor(extraInfo);

    }

    private void setColor(Intent extraInfo){



        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);

        profile.setBackgroundColor(unselected);
        ask.setBackgroundColor(unselected);
        answer.setBackgroundColor(unselected);
        discover.setBackgroundColor(selected);

        if(extraInfo.getStringExtra("origin").equals("Answer")){
            answer.setBackgroundColor(selected);
            discover.setBackgroundColor(unselected);
        }
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//


    }

    private void fillWithQuestionDetails(Intent extraInfo){
        // TODO: Backend side need to retrieve the user image
        String critiqueText = extraInfo.getStringExtra("critiqueText");
        String answerText = extraInfo.getStringExtra("answerText");
        String questionText = extraInfo.getStringExtra("questionText");

        TextView questionView = (TextView) findViewById(R.id.detailedQ_question);
        questionView.setText(questionText);

        TextView answerView = (TextView) findViewById(R.id.detailedQ_answer);
        answerView.setText(answerText);

        TextView critiqueView = (TextView) findViewById(R.id.detailedQ_critique);
        critiqueView.setText(critiqueText);


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

    public void saveToBoard(View view) {
        Intent boardFullView = new Intent(this, BoardFullView.class);
        EditText reflection = (EditText) findViewById(R.id.detailedQ_thought_input);
        String critiqueText = extraInfo.getStringExtra("critiqueText");
        String answerText = extraInfo.getStringExtra("answerText");
        String questionText = extraInfo.getStringExtra("questionText");
        String answererID = extraInfo.getStringExtra("answererID");
        boardFullView.putExtra("context", "save");
        boardFullView.putExtra("questionText", questionText);
        boardFullView.putExtra("answerText", answerText);
        boardFullView.putExtra("critiqueText", critiqueText);
        boardFullView.putExtra("reflection", reflection.getText());
        boardFullView.putExtra("answererID", answererID);
        boardFullView.putExtra("origin", "SeeDetailedQuestion");
        startActivity(boardFullView);
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
