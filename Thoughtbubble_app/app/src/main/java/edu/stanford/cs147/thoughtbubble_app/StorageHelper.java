/**
 * Created by potsui on 12/3/17.
 */

package edu.stanford.cs147.thoughtbubble_app;

import android.util.Log;

import com.google.firebase.storage.*;

import java.util.ArrayList;

public class StorageHelper {
    // For debugging
    private static final String TAG = "DatabaseHelper";

    private static StorageHelper singleton_instance = null;
    public FirebaseStorage storage;
    public StorageReference storageReference;

    private StorageHelper() {

        // Create references to the database in various places
        this.storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }



    public static StorageHelper getInstance(){
        if (singleton_instance == null)
            singleton_instance = new StorageHelper();
        return singleton_instance;
    }

    public StorageReference getProfileImageRef(String thisUserID) {
        //TODO check if user has profile picture
        return storageReference.child("images/profile/" + thisUserID + ".jpg");
    }
}
