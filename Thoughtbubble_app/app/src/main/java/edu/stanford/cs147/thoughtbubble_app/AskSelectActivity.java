package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AskSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> myArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_select);

        //TODO: actually replace this with the real list
        myArray = new ArrayList<>();
        myArray.add("Grace");
        myArray.add("Po");
        myArray.add("Bonnie");
        myArray.add("Jenny");

        //TODO: will have to use a different layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, myArray
        );

        ListView list = (ListView) findViewById(R.id.ask_friends_list);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> list, View row, int index, long rowID) {
        String clickedName = myArray.get(index);
        Intent intent = new Intent();
        intent.putExtra("sendTo", clickedName);
        setResult(RESULT_OK, intent);
        finish();
    }

}
