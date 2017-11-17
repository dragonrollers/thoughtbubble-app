package dragonrollers.thoughtbubble;

import android.app.ActionBar;
import android.drm.DrmStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a new actionbar (navigation bar)
        ActionBar actionBar = getActionBar();

        //Add buttons to the actionBar
        actionBar.setCustomView(R.layout.nav_ask_button);
        Button navDiscoverButton = (Button) actionBar.getCustomView().findViewById(R.id.navDiscoverButton);
        Button navAskButton = (Button) actionBar.getCustomView().findViewById(R.id.navAskButton);
        Button navAnswerButton = (Button) actionBar.getCustomView().findViewById(R.id.navAnswerButton);

        //Creates onClickListener to handle events associated with clicking navigation toolbar buttons
        navDiscoverButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //back to main?
            }
        });

        navAskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch ask activity
            }
        });

        navAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch answer activity
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM| ActionBar.DISPLAY_SHOW_HOME);
    }
}
