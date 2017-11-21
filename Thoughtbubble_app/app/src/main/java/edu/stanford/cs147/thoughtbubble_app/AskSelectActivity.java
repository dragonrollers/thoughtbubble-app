package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AskSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_select);
    }

    public void SendDummyResult(View view){
        Intent intent = new Intent();
        intent.putExtra("sendTo", "Dummy");
        setResult(RESULT_OK, intent);
        finish();
    }
}
