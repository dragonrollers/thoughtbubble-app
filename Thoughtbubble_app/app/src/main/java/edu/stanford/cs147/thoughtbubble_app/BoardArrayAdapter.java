package edu.stanford.cs147.thoughtbubble_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BoardArrayAdapter extends ArrayAdapter<Board> {

    Context context;
    int layoutResourceId;
    ArrayList<Board> data = null;

    public BoardArrayAdapter(Context context, int layoutResourceId, ArrayList<Board> data) {
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
            holder.BoardName = (TextView)row.findViewById(R.id.board_title);
            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }

        return row;
    }

    static class QuestionHolder
    {
        TextView BoardName;
    }
}