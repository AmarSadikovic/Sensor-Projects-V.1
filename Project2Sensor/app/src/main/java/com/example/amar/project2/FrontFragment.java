package com.example.amar.project2;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * The mainmenu where the user can read some information from the app and choose to either
 * read the api with asynctask or volley
 */

public class FrontFragment extends Fragment {
    private Button btnAsyncTask;
    private Button btnVolley;
    private ATFrag atFrag;
    private VolleyFrag volleyFrag;
    private float temperature = 0, pressure = 0, humidity = 0;

    public FrontFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, container, false);
        atFrag = new ATFrag();
        volleyFrag = new VolleyFrag();
        btnAsyncTask = (Button) view.findViewById(R.id.btnAsyncTask);
        btnVolley = (Button) view.findViewById(R.id.btnVolley);


        btnAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFragment(atFrag, true);
            }
        });

        btnVolley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setFragment(volleyFrag, true);
            }
        });

        return view;
    }

    /**
     *
     * @param temperature sensor temperature
     * @param pressure sensor pressure
     * @param humidity sensor humidity
     * @param altitude sensor altitude
     * @param timestamp sensor timestamp
     */
    public void sensorDataAsync(float temperature, float pressure, float humidity, float altitude, String timestamp) {
        atFrag.setSensorText(temperature, pressure, humidity, altitude, timestamp);
    }
    public void sensorDataVolley(float temperature, float pressure, float humidity, float altitude, String timestamp) {
        volleyFrag.setSensorText(temperature, pressure, humidity, altitude, timestamp);
    }

}
