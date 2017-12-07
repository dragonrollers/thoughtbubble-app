package edu.stanford.cs147.thoughtbubble_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AnonQuestionAdapter extends ArrayAdapter<Question> {

    Context context;
    int layoutResourceId;
    ArrayList<Question> data = null;

    public AnonQuestionAdapter(Context context, int layoutResourceId, ArrayList<Question> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuestionHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new QuestionHolder();
            holder.questionContent = (TextView)row.findViewById(R.id.questionContent);
            holder.answerContent = (TextView) row.findViewById(R.id.answerContent);
            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }


        Question question = data.get(position);
        holder.questionContent.setText(question.questionText);

        if(question.answerText != null){
            holder.answerContent.setText(question.answerText);
        }


        return row;
    }

    static class QuestionHolder
    {
        TextView answerContent;
        TextView questionContent;
    }
}