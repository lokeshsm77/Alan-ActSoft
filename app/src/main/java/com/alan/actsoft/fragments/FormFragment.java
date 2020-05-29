package com.alan.actsoft.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.alan.actsoft.R;
import com.alan.actsoft.alan.Alan;
import com.alan.actsoft.alan.data.Msgs;
import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanState;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventOptions;
import com.alan.alansdk.events.EventParsed;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

public class FormFragment extends Fragment {

    FragmentListener listener;

    View rootView;

    EditText employeeName;
    EditText temperature;

    CheckBox fever;
    CheckBox vomiting;
    CheckBox coughing;

    Button draft;
    Button submit;


    public static final String TAG = "COVID Test";
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.covid_test_form, container, false);
        initializeView();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentListener) context;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    /**
     * This method initializes the view components and listeners
     */
    private void initializeView(){
        employeeName =  rootView.findViewById(R.id.employeeName);
        temperature =  rootView.findViewById(R.id.temperature);

        fever = rootView.findViewById(R.id.feverCheckbox);
        vomiting = rootView.findViewById(R.id.vomitingCheckbox);
        coughing = rootView.findViewById(R.id.coughCheckbox);

        draft = rootView.findViewById(R.id.covid_draft);
        submit = rootView.findViewById(R.id.covid_submit);

        draft.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                saveDraft();
            }
        });


        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });

        fever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initializeAlanListener();

        employeeName.setFocusableInTouchMode(true);
        employeeName.requestFocus();
    }

    /**
     * This method just calls the main list fragment
     */
    private void saveDraft(){
        this.listener.initializeFragment("");
    }

    /**
     * Method handles the on click submit
     */
    private void onSubmit(){
        this.listener.initializeFragment(BrainsListFragment.TAG);
        getActivity().getSupportFragmentManager().popBackStack();
    }


    /**
     * method sets the listener and visual state for the alan SDK.
     */
    private void initializeAlanListener(){
        Alan.getInstance().clearCallbacks();
        Alan.getInstance().registerCallback(new AlanCallback() {
            @Override
            public void onAlanStateChanged(@NonNull AlanState alanState) {
                super.onAlanStateChanged(alanState);
            }

            @Override
            public void onRecognizedEvent(EventRecognised eventRecognised) {
                super.onRecognizedEvent(eventRecognised);
            }

            @Override
            public void onParsedEvent(EventParsed eventParsed) {
                super.onParsedEvent(eventParsed);
            }

            @Override
            public void onOptionsReceived(EventOptions eventOptions) {
                super.onOptionsReceived(eventOptions);
            }

            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                JSONObject alanCommand = Alan.getInstance().processAlanEventCommand(eventCommand);
                handleAlanVoiceCommand(alanCommand);
            }

            @Override
            public void onTextEvent(EventText eventText) {
                super.onTextEvent(eventText);
            }

            @Override
            public void onEvent(String event, String payload) {
                super.onEvent(event, payload);
            }

            @Override
            public void onError(String error) {
                super.onError(error);
            }
        });
    }

    /**
     * Method handles the alan voice commands and process the event actions associated with the voice commands.
     *
     * @param alanCommand
     */
    public void handleAlanVoiceCommand(JSONObject alanCommand) {
        try {
            if (alanCommand != null) {
                String cmd = alanCommand.getString("command");
                switch (cmd) {
                    case "employee_name":
                        this.employeeName.setText(alanCommand.getString("name"));
                        this.temperature.setFocusableInTouchMode(true);
                        this.temperature.requestFocus();
                        break;
                    case "temperature":
                        this.temperature.setText(alanCommand.getString("temp"));
                        this.fever.setFocusableInTouchMode(true);
                        this.fever.requestFocus();
                        break;
                    case "symptoms":
                        String symptom = alanCommand.getString("symptom");

                        if(symptom.equalsIgnoreCase("fever")){
                            this.fever.setFocusableInTouchMode(true);
                            this.fever.requestFocus();
                            if(!this.fever.isChecked()) {
                                this.fever.setChecked(true);
                            } else {
                                this.fever.setChecked(false);
                            }
                        } else if(symptom.equalsIgnoreCase("coughing")){
                            this.coughing.setFocusableInTouchMode(true);
                            this.coughing.requestFocus();

                            if(!this.coughing.isChecked()){
                                this.coughing.setChecked(true);
                            } else {
                                this.coughing.setChecked(false);
                            }
                        } else if(symptom.equalsIgnoreCase("vomiting")){
                            this.vomiting.setFocusableInTouchMode(true);
                            this.vomiting.requestFocus();
                            if(!this.vomiting.isChecked()){
                                this.vomiting.setChecked(true);
                            } else {
                                this.vomiting.setChecked(false);
                            }
                        }
                        this.submit.setFocusableInTouchMode(true);
                        this.submit.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));                       this.submit.requestFocus();
                        break;
                    case "submit":
                        showSubmittedData();
                        break;
                }
            } else {
                Alan.getInstance().playText(Msgs.INVALID_RESPONSE);
            }
        } catch(Exception e){
            Alan.getInstance().playText("JSON Exception");
            e.printStackTrace();;
        }
    }

    private void showSubmittedData(){
        this.submit.performClick();
    }


    private String getEmployeeName(){
        employeeName.setFocusableInTouchMode(true);
        employeeName.requestFocus();
        return employeeName.getText().toString();
    }

    private String getTemperature(){
        temperature.setFocusableInTouchMode(true);
        temperature.requestFocus();
        return temperature.getText().toString();
    }


}
