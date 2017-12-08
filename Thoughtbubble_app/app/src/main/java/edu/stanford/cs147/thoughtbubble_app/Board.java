package edu.stanford.cs147.thoughtbubble_app;

import java.util.ArrayList;

/**
 * Created by potsui on 12/6/17.
 */

public class Board {
    private String name;
    private String id;
    private ArrayList<String> questions;

    public Board () {}

    public Board(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String setID(String id) {return this.id; }

    public String getID(String id) {return this.id; }

    public ArrayList<String> getQuestions() { return questions; }

    public void setQuestions(ArrayList<String> questions) { this.questions = questions; }
}
