package edu.stanford.cs147.thoughtbubble_app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bonnienortz on 11/27/17.
 */

public class User {
    // These should match the names in Firebase
    // TODO more than just names
    private String firstName;
    private String lastName;
    private ArrayList<String> topics;
    private HashMap<String, String> boards;
    private boolean hasProfileImage;

    public User () {}

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName){ this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName){ this.lastName = lastName; }

    public ArrayList<String> getTopics() { return topics; }

    public void setTopics(ArrayList<String> topics){ this.topics = topics; }

    public HashMap<String, String> getBoards() { return boards; }

    public void setBoards(HashMap<String, String> boards){ this.boards = boards; }

    public boolean getHasProfileImage() { return hasProfileImage; };

    public void setHasProfileImage(boolean hasProfileImage) { this.hasProfileImage = hasProfileImage; };

}
