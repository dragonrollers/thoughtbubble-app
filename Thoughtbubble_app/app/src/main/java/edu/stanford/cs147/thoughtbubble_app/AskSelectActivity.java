package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
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

    ArrayList<String> myArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_select);

        //TODO: actually replace this with the real list
        //Tester list
        myArray = new ArrayList<>();
        myArray.add("Grace");
        myArray.add("Po");
        myArray.add("Bonnie");
        myArray.add("Jenny");

        //TODO: will have to use a different layout in order to accomodate more than one string
        friendsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, myArray
        );

        ListView list = (ListView) findViewById(R.id.ask_friends_list);
        list.setOnItemClickListener(this); //connects onItemClick function to clicking list items
        list.setAdapter(friendsAdapter); //displays the list in the xml

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
        String clickedName = myArray.get(index);
        Intent intent = new Intent();
        intent.putExtra("sendTo", clickedName);
        //TODO: should probably also send back the ID for good measure
        setResult(RESULT_OK, intent);
        finish();
    }


    // This listener listens for any change in the "friends" portion of the database for this user
    // TODO this assumes that if a user changes their name, that change is propogated to the "friends" parts of the DB
    //      ^ That might not be the best practice, so we can change if necessary!
    private void attachFriendsReadListener(){
        if (friendsListener == null) { // It start out null eventually when we add authentication
            friendsListener = new ValueEventListener() {

                @Override
                // TODO is there a better way to do this that doesn't require pulling the whole list every time?
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "IN ATTACH FRIENDS READ LISTENER");

                    GenericTypeIndicator<ArrayList<HashMap<String, Object>>> t = new GenericTypeIndicator<ArrayList<HashMap<String, Object>>>() {};
                    ArrayList<HashMap<String, Object>> friendList = dataSnapshot.getValue(t);

                    friendsAdapter.clear();
                    for (HashMap<String, Object> h : friendList){
                        friendsAdapter.add((String)h.get("friendName"));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.");
                }
            };


            DatabaseReference friends = DBH.users.child(THIS_USER_ID).child("friends");
            friends.addValueEventListener(friendsListener);


        }
    }

}
