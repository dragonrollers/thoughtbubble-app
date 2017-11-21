package edu.stanford.cs147.thoughtbubble_app;

/**
 * Created by Grace on 11/20/2017.
 */

class DatabaseHelper {

    private static DatabaseHelper singleton_instance = null;

    private DatabaseHelper(){
        //TODO: initialize class here by establishing a connection to the db
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
