package edu.stanford.cs147.thoughtbubble_app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by potsui on 12/6/17.
 */

public class Board {
    private String name;
    private HashMap<String, Boolean> questions;

    public Board () {}

    public Board(String name){
        this.name = name;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public HashMap<String, Boolean> getQuestions() { return questions; }

    public void setQuestions(HashMap<String, Boolean> questions) { this.questions = questions; }
}
