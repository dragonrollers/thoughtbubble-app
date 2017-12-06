package edu.stanford.cs147.thoughtbubble_app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PublicProfilePage extends AppCompatActivity {

    private String TAG = "PublicProfilePage";

    private ImageView mImageView;
    private DatabaseHelper DBH;
    private StorageHelper storageHelper;

    private ValueEventListener currUserListener;
    private User currUser;
    private ArrayList<String> topics;

    //TODO Replace with userID of user we're currently viewing, loaded from previous view
    private String thisUserID = "2wBLMKWKNmefVO31DIW7fIFS1933";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBH = DatabaseHelper.getInstance();
        storageHelper = StorageHelper.getInstance();
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

        loadUserFromDatabase();
    }

    private void loadUserFromDatabase() {
        Log.d(TAG, "loadUserFromDatabase");
        final DatabaseReference currUserRef = DBH.users.child(thisUserID);
        if (currUserListener == null) {
            currUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "getting currUser");
                    topics = currUser.getTopics();

                    loadProfileText();
                    loadProfileImage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        currUserRef.addListenerForSingleValueEvent(currUserListener);
    }

    private void loadProfileText() {
        //TODO: load these from databases using the id of the user
        String name = currUser.getFirstName() + " " + currUser.getLastName();
        String topicsText = "";
        for (int i = 0; i < topics.size(); i++) {
            topicsText += topics.get(i);
            if (i != topics.size() - 1) topicsText +=  ", ";
        }

        TextView nameField = (TextView) findViewById(R.id.profile_name);
        nameField.setText(name);

        TextView topicsField = (TextView) findViewById(R.id.profile_topics);
        topicsField.setText(topicsText);
    }

    private void loadProfileImage(){
        //TODO Note this is untested because we haven't linked to this view
        mImageView = (ImageView) findViewById(R.id.profile_image);
        Log.d(TAG, "userID " + thisUserID);
        // Load the image using Glide
        if (currUser.getHasProfileImage()) {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef(thisUserID))
                    .asBitmap()
                    .into(mImageView);
        } else {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageHelper.getProfileImageRef("blank-user"))
                    .asBitmap()
                    .into(mImageView);
        }
    }

}