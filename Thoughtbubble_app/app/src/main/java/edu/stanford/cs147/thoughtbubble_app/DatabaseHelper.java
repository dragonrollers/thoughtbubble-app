package edu.stanford.cs147.thoughtbubble_app;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class DatabaseHelper {

    // For debugging
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper singleton_instance = null;
    public FirebaseDatabase database;
    public DatabaseReference databaseReference;
    public DatabaseReference questions;
    public DatabaseReference users;
    public DatabaseReference boards;


    private DatabaseHelper() {

        // Create references to the database in various places
        this.database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference();
        questions = database.getReference().child("questions");
        users = database.getReference().child("users");
        boards = database.getReference().child("boards");
    }



    public static DatabaseHelper getInstance(){
        if (singleton_instance == null)
            singleton_instance = new DatabaseHelper();
        return singleton_instance;
    }

    public void writeFirstName(String thisUserID, String firstName) {
        DatabaseReference userRef = users.child(thisUserID);
        userRef.child("firstName").setValue(firstName);
    }

    public void writeLastName(String thisUserID, String lastName) {
        DatabaseReference userRef = users.child(thisUserID);
        userRef.child("lastName").setValue(lastName);
    }

    public void writeFullName(String thisUserID, String fullName){
        DatabaseReference userRef = users.child(thisUserID);
        userRef.child("fullName").setValue(fullName);
    }

    public void writeNewInterest(final String thisUserID, final String interest) {
        final DatabaseReference ref = users.child(thisUserID).child("topics");
        ValueEventListener topicsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long index = dataSnapshot.getChildrenCount();
                ref.child(String.valueOf(index)).setValue(interest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(topicsListener);
    }

    public void removeInterest(String thisUserID, final int index) {
        Log.d(TAG, "index=" + index);
        final DatabaseReference ref = users.child(thisUserID);
        ValueEventListener topicsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currUser = dataSnapshot.getValue(User.class);
                ArrayList<String> topics = currUser.getTopics();
                topics.remove(index);
                ref.child("topics").setValue(topics);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(topicsListener);
    }

    public String addBoard(String thisUserID, String boardName) {
        Board newBoard = new Board(boardName);
        DatabaseReference newBoardRef = boards.push();
        String newBoardKey = newBoardRef.getKey();

        // Create data to update
        Map updatedData = new HashMap();
        updatedData.put("boards/" + newBoardKey, newBoard);
        updatedData.put("users/" + thisUserID + "/boards/" + newBoardKey, newBoardKey);

        // Do the update
        databaseReference.updateChildren(updatedData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Problem writing to database: " + databaseError.toString());
                }
            }
        });
        return newBoardKey;
    }


    public void addQuestionToBoard(String thisUserID, String boardID, String questionID, String thought) {
        Map updatedData = new HashMap();
        updatedData.put("users/" + thisUserID + "/savedQuestions/" + questionID, thought);
        updatedData.put("boards/" + boardID + "/questions/" + questionID, thought);

        databaseReference.updateChildren(updatedData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Problem writing to database: " + databaseError.toString());
                }
            }
        });

    }

    public void writeAskToDatabase(String questionText, String thisUserID, String sendToID) {

        // Create Question object from given data
        Question newQuestion = new Question();
        newQuestion.setQuestionText(questionText);
        newQuestion.setAnswererID(sendToID);
        newQuestion.setQuestionerID(thisUserID);

        Long tsLong = System.currentTimeMillis()/1000;
        String currentDateTimeString = tsLong.toString();
        newQuestion.setAskTimestamp(currentDateTimeString);



        // Update all relevant parts of the database atomically
        // Generate new push ID for the new question
        DatabaseReference newQuestionRef = questions.push();
        String newQuestionKey = newQuestionRef.getKey();



        // Create data to update
        Map updatedData = new HashMap();

        // For the question itself
        String questionDataPath = "questions/" + newQuestionKey;
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


    public void writeAnswerToDatabase(String questionID, String revisedQuestionText, String answerText, ArrayList<String> friendsIDList){
        // Create data to update
        Map updatedData = new HashMap();

        // TODO if we were being robust about this we'd do error checking making sure this questionID actually exists in the DB
        // For the question
        String questionDataPath = "questions/" + questionID;

        String questionRevisionPath = questionDataPath + "/" + "critiqueText";
        updatedData.put(questionRevisionPath, revisedQuestionText);

        String questionAnswerPath = questionDataPath + "/" + "answerText";
        updatedData.put(questionAnswerPath, answerText);

        String questionTimestampPath = questionDataPath + "/" + "answerTimestamp";
        Long tsLong = System.currentTimeMillis()/1000;
        String currentDateTimeString = tsLong.toString();
        updatedData.put(questionTimestampPath, currentDateTimeString);

//        try {
//            Thread.sleep(10000);
//        } catch(Exception e){}

        Log.d(TAG, friendsIDList.toString());
        for (String friendID : friendsIDList){
            String updatePath = "users/" + friendID + "/discoverQuestions/" + questionID;
            updatedData.put(updatePath, true);
        }



        // TODO potentially implement part where we change asker and answerer parts of database to say question is changed
        // (above requires refactoring the database slightly)

        Log.d(TAG, "About to update database");
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
