package edu.stanford.cs147.thoughtbubble_app;

/**
 * Created by bonnienortz on 11/27/17.
 */

public class User {
    // These should match the names in Firebase
    // TODO more than just names
    private String firstName;
    private String lastName;

    public User () {}

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName){ this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName){ this.lastName = lastName; }

}
