package com.alan.actsoft;

import android.os.Bundle;

import com.alan.actsoft.alan.Alan;
import com.alan.alansdk.button.AlanButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private AlanButton alanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configAlanVoice();
    }



    /**
     * Method configures the alan voice command UI with application, initializes the Alan command
     * and voice command listener with Alan SDK.
     */
    private void configAlanVoice(){
        if(this.alanButton == null) {
            alanButton = findViewById(R.id.alan_button);
            Alan.getInstance().setAlanButton(alanButton);
            alanButton.setButtonAlign(AlanButton.BUTTON_RIGHT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
