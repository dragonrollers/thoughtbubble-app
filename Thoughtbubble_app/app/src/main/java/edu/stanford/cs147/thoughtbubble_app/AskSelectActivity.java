package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AskSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // For debugging
    private String TAG = "AskSelectActivity";

    private DatabaseHelper DBH;
    private ChildEventListener friendsListener;

    private AuthenticationHelper authHelper;

    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String[]> friendsArray; // For holding the ID and name of the friends displayed
    private ArrayList<String> friendNamesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBH = DatabaseHelper.getInstance();
        authHelper = AuthenticationHelper.getInstance();

        setContentView(R.layout.activity_ask_select);

        // Setting the color of the top bar -- pretty hacky -- do not touch this block//
        int unselected = Color.parseColor("#00cca3");
        int selected = Color.parseColor("#016d57");
        TextView profile = (TextView) findViewById(R.id.Profile);
        TextView ask = (TextView) findViewById(R.id.Ask);
        TextView discover = (TextView) findViewById(R.id.Discover);
        TextView answer = (TextView) findViewById(R.id.Answer);
        profile.setBackgroundColor(unselected);
        ask.setBackgroundColor(selected);
        answer.setBackgroundColor(unselected);
        discover.setBackgroundColor(unselected);
        // Setting the color of the top bar -- pretty hacky -- do not touch this block//


        //TODO: actually replace this with the real list
        //Tester list
        friendsArray = new ArrayList<>();
        friendNamesArray = new ArrayList<>();

        //TODO: will have to use a different layout in order to accomodate more than one string
        friendsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, friendNamesArray
        );

        ListView list = (ListView) findViewById(R.id.ask_friends_list);
        list.setOnItemClickListener(this); //connects onItemClick function to clicking list items
        list.setAdapter(friendsAdapter); //displays the list in the xml


        Log.d(TAG, "BEFORE LISTENER");
        // If the user has friends, attach a listener to the friends list in the DB to populate the adapter
        DBH.users.child(authHelper.thisUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("friends")) {
                    Log.d(TAG, "HAS FRIENDs");
                    attachFriendsReadListener();
                } else {
                    addNoFriendsFeedback();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.");
            }
        });



    }

    /**
     * ON CLICK METHOD: onItemClick
     * ----------------------------
     * This function is called whenever an item in the list is clicked. It retrieves the name
     * of whoever was clicked and returns this to the previous ask question intent
     *
     * @param list
     * @param row
     * @param index use this to figure out which element of the array to pull
     * @param rowID
     */
    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        // clickedFriend[0] is ID, [1] is full name
        String[] clickedFriend = friendsArray.get(index);
        Intent intent = new Intent();
        intent.putExtra("sendTo", clickedFriend);
        setResult(RESULT_OK, intent);
        finish();
    }


    // This listener listens for any change in the "friends" portion of the database for this user
    // TODO this assumes that if a user changes their name, that change is propogated to the "friends" parts of the DB
    //      ^ That might not be the best practice, so we can change if necessary!
    private void attachFriendsReadListener() {
        if (friendsListener == null) { // It start out null eventually when we add authentication
            Log.d(TAG, "ATTACHING FRIENDS LISTENER");
            friendsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final String friendID = dataSnapshot.getKey();
                    Log.d(TAG, "FRIEND ID: " + friendID);
                    DatabaseReference friendNameRef = DBH.users.child(friendID).child("fullName");
                    friendNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            friendsAdapter.add(name);
                            Log.d(TAG, "FRIEND NAME: " + name);
                            String[] friend = {friendID, name};
                            friendsArray.add(friend);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.");
                        }
                    });
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // NOT IMPLEMENTING BECAUSE I DON'T KNOW WHAT WOULD HAPPEN IF DATA CHANGED AND THEN WAS REMOVED - COULD CRASH
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            // Actually attach the listener
            DatabaseReference friends = DBH.users.child(authHelper.thisUserID).child("friends");
            friends.addChildEventListener(friendsListener);


        }
    }

    private void addNoFriendsFeedback() {
        TextView feedbackTextMain = new TextView(AskSelectActivity.this);
        feedbackTextMain.setText("You currently don't have any friends...");
        feedbackTextMain.setTextSize(18);
        feedbackTextMain.setGravity(Gravity.CENTER);
        TextView feedbackTextSub = new TextView(AskSelectActivity.this);
        feedbackTextSub.setText("Why not find some new ones?");
        feedbackTextSub.setTextSize(14);
        feedbackTextSub.setGravity(Gravity.CENTER);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ask_select_main_layout);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.addView(feedbackTextMain, 0);
        mainLayout.addView(feedbackTextSub, 1);
        mainLayout.removeViewAt(2);
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


}
