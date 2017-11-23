package edu.stanford.cs147.thoughtbubble_app;

/**
 * Created by Grace on 11/23/2017.
 */

public class Question {
    private String questionText;
    private String answerText;
    private String answerName;
    private int questionID;

    public Question(String questionText) {
        this.questionText = questionText;
        this.answerText = null;
        this.answerName = null;
        this.questionID = -1;
    }

    public Question(String questionText, int questionID) {
        this.questionText = questionText;
        this.answerText = null;
        this.answerName = null;
        this.questionID = questionID;
    }

    public Question(String questionText, String answerText, String answerName) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.answerName = answerName;
        this.questionID = -1;
    }

    public Question(String questionText, String answerText, String answerName, int questionID) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.answerName = answerName;
        this.questionID = questionID;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public String getAnswerName() {
        return this.answerName;
    }

    public int getQuestionID() {
        return this.questionID;
    }
}
