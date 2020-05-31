package com.alan.actsoft;

import android.os.Bundle;

import com.alan.actsoft.fragments.TimeKeeping;
import com.alan.alansdk.button.AlanButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alan.actsoft.alan.Alan;
import com.alan.actsoft.fragments.COVIDTestForm;
import com.alan.actsoft.fragments.FragmentListener;
import com.alan.actsoft.fragments.Home;
import com.alan.actsoft.fragments.MaterialRequest;

public class MainActivity extends AppCompatActivity implements FragmentListener {
    private AlanButton alanButton;

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        initializeFragment(Home.TAG);

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

    @Override
    public void initializeFragment(String fragmentName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (fragmentName){
            case COVIDTestForm.TAG:
            case COVIDTestForm.LIST_TAG:
                currentFragment = new COVIDTestForm();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
            case TimeKeeping.TAG:
            case TimeKeeping.LIST_TAG:
                currentFragment = new TimeKeeping();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
            case MaterialRequest.LIST_TAG:
                currentFragment = new MaterialRequest();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
            default:
                currentFragment = new Home();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
        }
    }
}
