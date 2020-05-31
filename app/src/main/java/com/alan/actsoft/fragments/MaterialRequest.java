package com.alan.actsoft.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MaterialRequest.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MaterialRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaterialRequest extends Fragment {
    public static final String  TAG = "materials_request_form";
    public static final String  LIST_TAG = "Materials Request";


    FragmentListener listener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View rootView;

    EditText jobName;
    EditText jobNumber;
    EditText materialNeeded;
    EditText quantity;
    EditText neededBy;

    Button draft;
    Button submit;

    public MaterialRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MaterialRequest.
     */
    // TODO: Rename and change types and number of parameters
    public static MaterialRequest newInstance(String param1, String param2) {
        MaterialRequest fragment = new MaterialRequest();
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
        rootView = inflater.inflate(R.layout.fragment_material_request, container, false);;
        initializeView();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initializeView(){
         jobName = rootView.findViewById(R.id.jobName);
         jobNumber = rootView.findViewById(R.id.jobNumber);
         materialNeeded = rootView.findViewById(R.id.materialNeeded);
         quantity = rootView.findViewById(R.id.quality);
         neededBy = rootView.findViewById(R.id.neededBy);

        jobName.setFocusableInTouchMode(true);
        jobName.requestFocus();

        draft = rootView.findViewById(R.id.material_draft);
        submit = rootView.findViewById(R.id.submit_material);

        initializeAlanListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentListener) context;
            setFragmentTitle();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //sets the appbar title
    private void setFragmentTitle(){
        TextView title = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_title);
        title.setText(R.string.materials_request);

        ImageView titleBack = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_image);
        titleBack.setImageResource(R.drawable.back_arrow);

        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackClick();
            }
        });
    }

    private void handleBackClick(){
        getActivity().getSupportFragmentManager().popBackStack();
        this.listener.initializeFragment("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
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
                    case "job_name":
                        String name = alanCommand.getString("name");
                        jobName.setText(name);
                        jobNumber.setFocusableInTouchMode(true);
                        jobNumber.requestFocus();
                        break;
                    case "job_number":
                        name = alanCommand.getString("name");
                        jobNumber.setText(name);
                        materialNeeded.setFocusableInTouchMode(true);
                        materialNeeded.requestFocus();
                        break;
                    case "material_needed":
                        name = alanCommand.getString("materials");
                        materialNeeded.setText(name);
                        quantity.setFocusableInTouchMode(true);
                        quantity.requestFocus();
                        break;
                    case "quantity":
                        name = alanCommand.getString("name");
                        quantity.setText(name);
                        neededBy.setFocusableInTouchMode(true);
                        neededBy.requestFocus();
                        break;
                    case "needed_by":
                        name = alanCommand.getString("date");
                        neededBy.setText(name);
                        submit.setFocusableInTouchMode(true);
                        submit.requestFocus();
                        break;
                    case "submit":
                        this.submit.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                        this.submit.requestFocus();
                        break;
                    case "back":
                        submit.performClick();
                        handleBackClick();
                        break;

                    default:
                        //Alan.getInstance().playText("Time keeping Form " + cmd);
                        break;
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
