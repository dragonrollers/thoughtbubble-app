package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bonnienortz on 12/2/17.
 */

// Class to help with authentication, to de-clutter Main Activity
public class AuthenticationHelper {

    // For debugging
    private static final String TAG = "AuthHelper";


    private static AuthenticationHelper singleton_instance = null;
    public FirebaseAuth auth;
    public FirebaseAuth.AuthStateListener authListener;
    public boolean signInAlreadyStarted;


    private DatabaseHelper DBH;

    public String thisUserID;



    private AuthenticationHelper(){
        auth = FirebaseAuth.getInstance();
        DBH = DatabaseHelper.getInstance();
        signInAlreadyStarted = false;
    }

    public static AuthenticationHelper getInstance(){
        if (singleton_instance == null)
            singleton_instance = new AuthenticationHelper();
        return singleton_instance;
    }


    public Intent getAuthUIInstance(){
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        // Uncomment line below for Facebook
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                        )
                )
                .build();
    }

    public void handleNewUserCreation(){
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        DBH.users.child(uid).child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // If the user doesn't exist in the database yet
                if (!snapshot.exists()) {
                    // Add user to database
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();


                    String thisUserPath = "users/" + uid + "/";
                    Map updatedData = new HashMap();

                    // Get name
                    String[] name = user.getDisplayName().split(" ");
                    updatedData.put(thisUserPath + "firstName", name[0]);
                    if (name.length > 1){
                        updatedData.put(thisUserPath + "lastName", name[1]);
                    }

                    // Do the update
                    DBH.databaseReference.updateChildren(updatedData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(TAG, "Problem writing to database: " + databaseError.toString());
                        }
                        }
                    });



                    // TODO bring user to activity to fill out profile
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void setThisUserID(){
        thisUserID = auth.getCurrentUser().getUid();
    }


}
