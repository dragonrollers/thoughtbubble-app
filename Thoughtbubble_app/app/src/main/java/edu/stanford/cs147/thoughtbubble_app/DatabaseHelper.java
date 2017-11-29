package edu.stanford.cs147.thoughtbubble_app;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Grace on 11/20/2017.
 */



class DatabaseHelper {

    // TODO Change this once we have authentication
    private String THIS_USER_ID = "0";

    // For debugging
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper singleton_instance = null;
    public FirebaseDatabase database;
    public DatabaseReference databaseReference;
    public DatabaseReference questions;
    public DatabaseReference users;

    private ArrayList<Question> questionArray = new ArrayList<Question>();

    private DatabaseHelper() {

        // Create references to the database in various places
        this.database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference();
        questions = database.getReference().child("questions");
        users = database.getReference().child("users");

    }



    public static DatabaseHelper getInstance(){
        if (singleton_instance == null)
            singleton_instance = new DatabaseHelper();
        return singleton_instance;
    }

    public void writeAskToDatabase(String questionText, String sendToID) {
        // TODO change once we have auth
        String thisUserID = THIS_USER_ID;

        // Create Question object from given data
        Question newQuestion = new Question();
        newQuestion.setQuestionText(questionText);
        newQuestion.setAnswererID(sendToID);
        newQuestion.setQuestionerID(thisUserID);

        // TODO eventually
        //Date timestamp = Calendar.getInstance().getTime();




        // Update all relevant parts of the database atomically
        // Generate new push ID for the new question
        DatabaseReference newQuestionRef = questions.push();
        String newQuestionKey = newQuestionRef.getKey();



        // Create data to update
        Map updatedData = new HashMap();

        // The question itself
        String questionDataPath = "questions" + newQuestionKey;
        updatedData.put(questionDataPath, newQuestion);

        // For the question asker
        String newOutgoingQuestionKey = users.child(thisUserID).push().getKey();
        String questionerDataPath = "users/" + thisUserID + "/outgoingQuestions/" + newOutgoingQuestionKey;
        updatedData.put(questionerDataPath, newQuestionKey);

        // For the question answerer
        String newIncomingQuestionKey = users.child(sendToID).push().getKey();
        String answererDataPath = "users/" + sendToID + "/incomingQuestions/" + newIncomingQuestionKey;
        updatedData.put(answererDataPath, newQuestionKey);

        // Do the update
        databaseReference.updateChildren(updatedData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Problem writing to database: " + databaseError.toString());
                }
            }
        });

    }



}
