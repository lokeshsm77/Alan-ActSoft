package com.alan.actsoft.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeKeeping.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeKeeping#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeKeeping extends Fragment {
    public static final String  TAG = "time_keeping";
    public static final String  LIST_TAG = "Time Keeping";


    FragmentListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;

    private Map<String,List<Map<String, String>>> timeTracker = new HashMap<>();

    public static final String TRACK_PREFERENCE = "TrackerPrefs" ;
    public static final String TIME_TRACKS = "TIME_TRACKS";
    public static final String CLOCK_IN = "clock_in";
    public static final String CLOCK_OUT = "clock_out";
    public static final String START_BREAK = "break_in";
    public static final String STOP_BREAK = "break_out";
    public static final String START_LUNCH = "lunch_in";
    public static final String STOP_LUNCH = "lunch_out";

    private String currentStatus;

    public static final Map<String, String> TRAK_KEYS = new HashMap<String, String>(){
        {
            put(CLOCK_IN, "Clock in");
            put(CLOCK_OUT, "Clock out");
            put(START_BREAK, "Start break");
            put(STOP_BREAK, "Stop break");
            put(START_LUNCH, "Start lunch");
            put(STOP_LUNCH, "Stop lunch");
        }
    };


    private ListView trackList;
    private List trackData;
    private ArrayAdapter<String> adapter;

    Button checkIn;
    Button checkOut;
    Button startBreak;
    Button stopBreak;
    Button startLunch;
    Button stopLunch;

    TextView employeeTrackStatus;

    public TimeKeeping() {

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeKeeping.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeKeeping newInstance(String param1, String param2) {
        TimeKeeping fragment = new TimeKeeping();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_time_keeping, container, false);
        setDateTime();
        initializeView();

        showTimeTracks();
        return rootView;
    }

    private void initializeView(){

        checkIn = rootView.findViewById(R.id.clock_in);
        checkOut  = rootView.findViewById(R.id.clock_out);
        startBreak = rootView.findViewById(R.id.start_break);
        stopBreak = rootView.findViewById(R.id.stop_break);
        startLunch = rootView.findViewById(R.id.start_lunch);
        stopLunch = rootView.findViewById(R.id.stop_lunch);
        employeeTrackStatus = rootView.findViewById(R.id.employee_track_status);
        initializeAlanListener();
    }

    private void displayTracks(){

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentListener) context;
            setFragmentTitle();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    //sets the appbar title
    private void setFragmentTitle(){
        TextView title = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_title);
        title.setText(R.string.time_keeping);

        ImageView titleBack = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_image);
        titleBack.setImageResource(R.drawable.back_arrow);

        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackClick();
            }
        });
    }

    private void setDateTime(){
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        String formattedDate = df.format(c.getTime());

        TextView dateTime = rootView.findViewById(R.id.date_time);
        dateTime.setText(formattedDate);
    }

    private void handleBackClick(){
        getActivity().getSupportFragmentManager().popBackStack();
        this.listener.initializeFragment("");
    }

    /**
     * Method displays the time tracks for the user
     */
    private void showTimeTracks(){
        trackList = rootView.findViewById(R.id.traks_list);
        trackData = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, trackData);
        trackList.setAdapter(adapter);

        SharedPreferences pref = this.getActivity().getSharedPreferences(TRACK_PREFERENCE, Context.MODE_PRIVATE);

        Set<String> timeTracks = pref.getStringSet(TIME_TRACKS, null);

        if(timeTracks != null){
            trackData.addAll(timeTracks);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Set the time tracks based om the user inputs
     *
     * @param trackerName
     * @param value
     */
    private void saveTimeTracker(String trackerName, String value){
        SharedPreferences pref = this.getActivity().getSharedPreferences(TRACK_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Set<String> timeTracks = pref.getStringSet(TIME_TRACKS, null);

        if (timeTracks == null) {
            timeTracks = new HashSet<>();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(trackerName).append("      ").append(value);
        timeTracks.add(sb.toString());

        trackData.add(sb.toString());
        adapter.notifyDataSetChanged();

        editor.putStringSet(TIME_TRACKS, timeTracks);
        editor.commit();

        employeeTrackStatus.setText("You are currently "+ trackerName);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                    case "open":
                        String screen = alanCommand.getString("screen");
                        this.listener.initializeFragment(screen);
                        break;
                    case CLOCK_IN:
                        checkIn.setVisibility(View.GONE);
                        checkOut.setVisibility(View.VISIBLE);
                        break;
                    case CLOCK_OUT:
                        checkIn.setVisibility(View.VISIBLE);
                        checkOut.setVisibility(View.GONE);
                        break;
                    case START_BREAK:
                        startBreak.setVisibility(View.GONE);
                        stopBreak.setVisibility(View.VISIBLE);
                        break;
                    case STOP_BREAK:
                        startBreak.setVisibility(View.VISIBLE);
                        stopBreak.setVisibility(View.GONE);
                        break;
                    case START_LUNCH:
                        startLunch.setVisibility(View.GONE);
                        stopLunch.setVisibility(View.VISIBLE);
                        break;
                    case STOP_LUNCH:
                        startLunch.setVisibility(View.VISIBLE);
                        stopLunch.setVisibility(View.GONE);
                        break;
                    case "back":
                         screen = alanCommand.getString("screen");
                         this.listener.initializeFragment(screen);
                        break;
                }

                String keyName = TRAK_KEYS.get(cmd);
                if(keyName != null) {
                    String time = alanCommand.getString("time");
                    saveTimeTracker(keyName, time);
                }
            } else {
                Alan.getInstance().playText(Msgs.INVALID_RESPONSE);
            }
        } catch(Exception e){
            // Alan.getInstance().playText("JSON Exception");
            e.printStackTrace();;
        }
    }
}
