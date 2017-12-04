package edu.stanford.cs147.thoughtbubble_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionAdapter extends ArrayAdapter<Question> {

    Context context;
    int layoutResourceId;
    ArrayList<Question> data = null;

    public QuestionAdapter(Context context, int layoutResourceId, ArrayList<Question> data) {
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
            holder.answerContent = (TextView)row.findViewById(R.id.answerContent);
            holder.userName = (TextView)row.findViewById(R.id.userName);
            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }


        Question question = data.get(position);
        holder.userName.setText(question.answererID);
        holder.questionContent.setText(question.questionText);
        holder.answerContent.setText(question.answerText);

        return row;
    }

    static class QuestionHolder
    {
        TextView userName;
        TextView questionContent;
        TextView answerContent;
    }
}