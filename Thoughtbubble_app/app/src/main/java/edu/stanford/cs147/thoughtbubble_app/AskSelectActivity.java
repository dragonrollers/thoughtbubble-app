package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AskSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // TODO Change this once we have authentication
    private String THIS_USER_ID = "0";


    // For debugging
    private String TAG = "AskSelectActivity";

    private DatabaseHelper DBH;
    private ValueEventListener friendsListener;

    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String[]> friendsArray; // For holding the ID and name of the friends displayed

    ArrayList<String> myArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        myArray = new ArrayList<>();
        myArray.add("Grace");
        myArray.add("Po");
        myArray.add("Bonnie");
        myArray.add("Jenny");

        //TODO: will have to use a different layout in order to accomodate more than one string
        // TODO this is cleared and repopulated w/info from the db with the listener - do we need myArray anymore?
        friendsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, myArray
        );

        ListView list = (ListView) findViewById(R.id.ask_friends_list);
        list.setOnItemClickListener(this); //connects onItemClick function to clicking list items
        list.setAdapter(friendsAdapter); //displays the list in the xml


        // Attach a listener to the friends list in the DB to populate the adapter
        friendsArray = new ArrayList<String[]>();
        DBH = DatabaseHelper.getInstance();
        attachFriendsReadListener();

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
    private void attachFriendsReadListener(){
        if (friendsListener == null) { // It start out null eventually when we add authentication

            // Define the listener
            friendsListener = new ValueEventListener() {

                @Override
                // TODO is there a better way to do this that doesn't require pulling the whole list every time?
                // Whenever any data changes in the friends list, clear the adapter and populate it with the updated data
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Get the updated friends list
                    GenericTypeIndicator<ArrayList<HashMap<String, String>>> t = new GenericTypeIndicator<ArrayList<HashMap<String, String>>>() {};
                    ArrayList<HashMap<String, String>> friendList = dataSnapshot.getValue(t);

                    // Populate the adapter with the updated data
                    friendsAdapter.clear();
                    for (HashMap<String, String> h : friendList){
                        friendsAdapter.add(h.get("friendName"));
                        String[] friend = {h.get("friendID"), h.get("friendName")};
                        friendsArray.add(friend);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            };


            // Actually attach the listener
            DatabaseReference friends = DBH.users.child(THIS_USER_ID).child("friends");
            friends.addValueEventListener(friendsListener);


        }
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
