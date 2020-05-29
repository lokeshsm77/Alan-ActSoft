package com.alan.actsoft.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import androidx.fragment.app.ListFragment;

public class BrainsListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "ListFragment";

    View rootView;
    FragmentListener listener;
    String [] listItems;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.listItems = getActivity().getResources().getStringArray(R.array.BrainsMenuItems);
        rootView = inflater.inflate(R.layout.list_fragment, container, false);
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
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.BrainsMenuItems, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        initializeAlanListener();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        if(this.listItems.length > 0) {
            String fragmentName = this.listItems[position];
            if (fragmentName != null){
                this.listener.initializeFragment(fragmentName);
            }
        }
    }

    /**
     * method sets the listener and visual state for the alan SDK.
     */
    private void initializeAlanListener(){
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
                    case "open":
                        String screen = alanCommand.getString("screen");
                        if (screen.equals("covid_test_form")) {
                            this.listener.initializeFragment(FormFragment.TAG);
                        } else {
                            this.listener.initializeFragment(BrainsListFragment.TAG);
                        }
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

}
