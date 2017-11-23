package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AskWriteActivity extends AppCompatActivity {

    private String sendTo;
    private int SELECT_CONT_CODE = 111;
    private int SELECT_END_CODE = 222;
    private DatabaseHelper DBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        sendTo = null;
        DBH = DatabaseHelper.getInstance();

        //create SELECT on click listener to launch select intent
        Button selectButton = (Button) findViewById(R.id.ask_select_button);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent selectIntent = new Intent(AskWriteActivity.this, AskSelectActivity.class);
                startActivityForResult(selectIntent, SELECT_CONT_CODE);
            }
        });

        //create CANCEL on click listener to close the intent without doing anything
        Button cancelButton = (Button) findViewById(R.id.ask_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final Button sendButton = (Button) findViewById(R.id.ask_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (sendTo == null) {
                    //A sendee has not been set, so we launch the select activity
                    Intent selectIntent = new Intent(AskWriteActivity.this, AskSelectActivity.class);
                    startActivityForResult(selectIntent, SELECT_END_CODE);
                } else {
                    //TODO: Database helper method is a stub
                    //TODO: error checking, don't allow sending of blank messages
                    DBH.writeAskToDatabase(getInputText(), sendTo);
                    finish();
                }
            }
        });
    }

    private String getInputText() {
        EditText askText = (EditText) findViewById(R.id.ask_input_text);
        return askText.getText().toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == SELECT_CONT_CODE || requestCode == SELECT_END_CODE) {
            //Came back from select activity
            sendTo = intent.getStringExtra("sendTo");
            //TODO: remove this, the toast text is for testing
            Button selectButton = (Button) findViewById(R.id.ask_select_button);
            selectButton.setText(sendTo);
            Toast.makeText(this, "Send To: " + sendTo, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == SELECT_END_CODE) {
            DBH.writeAskToDatabase(getInputText(), sendTo);
            finish();
        }
    }
}
