package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SeeDetailedQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_detailed_question);

        Intent extraInfo = getIntent();

        fillWithQuestionDetails(extraInfo);

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
        finish();
    }
}
