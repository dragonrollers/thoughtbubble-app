package edu.stanford.cs147.thoughtbubble_app;

/**
 * Created by Grace on 11/23/2017.
 */

public class Question {
    public String questionText;
    public String answerText;
    public String critiqueText;
    public String timestamp;
    public int answererID;
    public int questionerID;
    public int questionID;

    /**
     * CONSTRUCTOR: question
     * ---------------------
     * This constructor is intended for Questions which have been pulled from the
     * database. Thus, all fields should be able to be filled. If there is no
     * critique text, then a null string can be passed in instead.
     */
    public Question(String questionText, String answerText, String critiqueText, String timestamp, int answererID, int questionerID, int questionID) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.critiqueText = critiqueText;
        this.timestamp = timestamp;
        this.answererID = answererID;
        this.questionerID = questionerID;
        this.questionID = questionID;
    }
}
