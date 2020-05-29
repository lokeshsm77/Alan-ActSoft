package com.alan.actsoft;

import android.os.Bundle;

import com.alan.actsoft.alan.Alan;
import com.alan.actsoft.fragments.FormFragment;
import com.alan.actsoft.fragments.FragmentListener;
import com.alan.alansdk.button.AlanButton;

import com.alan.actsoft.fragments.BrainsListFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements FragmentListener {
    private AlanButton alanButton;

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        initializeFragment(BrainsListFragment.TAG);

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
            case FormFragment.TAG:
                currentFragment = new FormFragment();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
            default:
                currentFragment = new BrainsListFragment();
                ft.replace(R.id.main_view, currentFragment);
                ft.commit();
                break;
        }
    }
}
