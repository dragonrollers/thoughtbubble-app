package edu.stanford.cs147.thoughtbubble_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AskWriteActivity extends AppCompatActivity {

    private DatabaseHelper DBH;


    // sendTo[0] is ID, sendTo[1] is full name
    private String[] sendTo;

    //These request codes help identify which activity returned to this current activity
    private int SELECT_CONT_CODE = 111; //Select activity returned, continue to write question
    private int SELECT_END_CODE = 222; //Select activity returned, send question and exit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_write);

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
                    //TODO: error checking, don't allow sending of blank messages
                    DBH.writeAskToDatabase(getInputText(), sendTo[0]);
                    Toast.makeText(AskWriteActivity.this, "Ask Sent!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /**
     * METHOD: getInputText
     * --------------------
     * Helper function that retrieves whatever the user answered into the input text box
     *
     * @return
     */
    private String getInputText() {
        EditText askText = (EditText) findViewById(R.id.ask_input_text);
        return askText.getText().toString();
    }

    /**
     * METHOD: onActivityResult
     * ------------------------
     * This method will be called when an activity called with startActivityForResult returns.
     * It's used to handle the two different cases of return from the select person activity
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == SELECT_CONT_CODE || requestCode == SELECT_END_CODE) {
            //Came back from select activity, load results into sendTo variable
            sendTo = intent.getStringArrayExtra("sendTo");
            //TODO: remove this, the toast text is for testing
            Button selectButton = (Button) findViewById(R.id.ask_select_button);
            selectButton.setText(sendTo[1]);
            Toast.makeText(this, "Send To: " + sendTo[1], Toast.LENGTH_SHORT).show();
        }
        if (requestCode == SELECT_END_CODE) {
            //was reached by hitting send, then launching the select person activity so the current
            //activity should just write to database and return
            DBH.writeAskToDatabase(getInputText(), sendTo[0]);
            Toast.makeText(this, "Ask Sent!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
