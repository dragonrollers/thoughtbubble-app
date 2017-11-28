package edu.stanford.cs147.thoughtbubble_app;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Grace on 11/20/2017.
 */

class DatabaseHelper {

    // For debugging
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper singleton_instance = null;
    public FirebaseDatabase database;
    public DatabaseReference questions;
    public DatabaseReference users;

    private ArrayList<Question> questionArray = new ArrayList<Question>();

    private DatabaseHelper() {
        Log.d(TAG, "BEGIN DATABASE HELPER");

        //TODO: initialize class here by establishing a connection to the db
        // Write a message to the database
        this.database = FirebaseDatabase.getInstance();


        Log.d(TAG, "BEGIN DATABASE HELPER2");

        questions = database.getReference().child("questions");
        users = database.getReference().child("users");
        Log.d(TAG, "END DATABASE HELPER");
    }



    public static DatabaseHelper getInstance(){
        if (singleton_instance == null)
            singleton_instance = new DatabaseHelper();
        return singleton_instance;
    }

    public void writeAskToDatabase(String message, String sendTo) {
        //Todo: write this to the database
    }



}
