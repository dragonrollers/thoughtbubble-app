package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PublicProfilePage extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile_page);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);
        profile.setBackgroundColor(selected);
        ask.setBackgroundColor(unselected);
        answer.setBackgroundColor(unselected);
        discover.setBackgroundColor(unselected);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        fillUserInformation();
    }

    private void fillUserInformation(){
        // TODO: Here, please let us read the firebase data!

        // user profile image
        mImageView = (ImageView) findViewById(R.id.profile_image);
        mImageView.setImageResource(R.drawable.elsa);


    }

    public void gotoPrivateProfile(View view) {
        // placeholder
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }


    public void gotoPublicProfile(View view) {
        // placeholder for now
    }
}