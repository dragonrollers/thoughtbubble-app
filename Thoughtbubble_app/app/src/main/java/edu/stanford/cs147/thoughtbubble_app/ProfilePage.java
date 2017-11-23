package edu.stanford.cs147.thoughtbubble_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        loadProfileText();
    }
    
    private void loadProfileText() {
        //TODO: load these from databases using the id of the user
        String name = "Sample Name";
        String topics = "Sample Interest 1, Sample Interest 2, Sample Interest 3";

        TextView nameField = (TextView) findViewById(R.id.profile_name);
        nameField.setText(name);

        TextView topicsField = (TextView) findViewById(R.id.profile_topics);
        topicsField.setText(topics);
    }
}
