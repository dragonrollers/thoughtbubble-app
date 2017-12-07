package edu.stanford.cs147.thoughtbubble_app;

import java.util.ArrayList;

/**
 * Created by potsui on 12/6/17.
 */

public class Board {
    private String name;
    private ArrayList<String> questions;

    public Board(String name){
        this.name = name;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
