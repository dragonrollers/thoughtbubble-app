package edu.stanford.cs147.thoughtbubble_app;

/**
 * Created by Grace on 11/23/2017.
 */

public class Question {
    // These should match the names in Firebase
    public String questionText;
    public String answerText;
    public String critiqueText;
    public String askTimestamp;
    public String answerTimestamp;
    public String answererID;
    public String questionerID;
    public String questionID;


    // Bonnie: It seems like the way Android pulls from the DB is to create an empty object and use the set methods
    //      to fill in the content, so we might not need the other constructor unless it's useful for us otherwise
    public Question (){}

    /**
     * CONSTRUCTOR: question
     * ---------------------
     * This constructor is intended for Questions which have been pulled from the
     * database. Thus, all fields should be able to be filled. If there is no
     * critique text, then a null string can be passed in instead.
     */
    public Question(String questionText, String answerText, String critiqueText, String answererID, String askerID, String questionID) {
        this.questionText = questionText;
        this.answerText = answerText;
        this.critiqueText = critiqueText;
        this.answererID = answererID;
        this.questionerID = askerID;
        this.questionID = questionID;

        // Not sure if we'll actually need this in this class, might be enough to store only in DB
        //this.questionID = questionID;
    }



    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String text) {
        this.questionText = text;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String text) {
        this.answerText = text;
    }

    public String getCritiqueText() {
        return critiqueText;
    }

    public void setCritiqueText(String text) {
        this.critiqueText = text;
    }

    public String getAskTimestamp() {
        return askTimestamp;
    }

    public void setAskTimestamp(String timestamp) {
        this.askTimestamp = timestamp;
    }

    public String getAnswerTimestamp() {
        return answerTimestamp;
    }

    public void setAnswerTimestamp(String timestamp) {
        this.answerTimestamp = timestamp;
    }

    public String getQuestionerID() {
        return questionerID;
    }

    public void setQuestionerID(String ID) {
        this.questionerID = ID;
    }

    public String getAnswererID() {
        return answererID;
    }

    public void setAnswererID(String ID) {
        this.answererID = ID;
    }


    public String toString(){
        String output = "";
        if (this.questionID != null) { output += "QuestionID: " + this.questionID + "\n"; }
        output += "Question: " + this.questionText + "\n";
        output += "Answer: " + this.answerText + "\n";
        output += "Critique: " + this.critiqueText + "\n";
        output += "Ask timestamp: " + this.askTimestamp + "\n";
        output += "Answer timestamp: " + this.answerTimestamp + "\n";
        output += "Answerer: " + this.answererID + "\n";
        output += "Questioner: " + this.questionerID + "\n";
        return output;
    }




    public static int compareByAnswerTimestamp(Question q1, Question q2){
        return q1.askTimestamp.compareTo(q2.askTimestamp);
    }

}
