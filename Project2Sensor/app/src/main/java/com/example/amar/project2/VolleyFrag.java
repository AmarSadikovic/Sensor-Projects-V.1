package com.example.amar.project2;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class VolleyFrag extends Fragment {

    private TextView volleyApiTemp, volleyApiPressure, volleyApiHumidity;
    private TextView volleySensorTemp, volleySensorPressure, volleySensorHumidity, volleyAltitude, volleyTime;
    private Button btnVolleyStart, btnVolleyStop;
    private String apiPressure = "";

    public VolleyFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volley, container, false);
        volleyApiTemp = (TextView) view.findViewById(R.id.volleyApiTemp);
        volleyApiPressure = (TextView) view.findViewById(R.id.volleyApiPressure);
        volleyApiHumidity = (TextView) view.findViewById(R.id.volleyApiHumidity);
        volleySensorTemp = (TextView) view.findViewById(R.id.volleySensorTemp);
        volleySensorPressure = (TextView) view.findViewById(R.id.volleySensorPressure);
        volleySensorHumidity = (TextView) view.findViewById(R.id.volleySensorHumidity);
        volleyAltitude = (TextView) view.findViewById(R.id.volleyAltitude);
        volleyTime = (TextView) view.findViewById(R.id.volleyTime);

        btnVolleyStart = (Button)view.findViewById(R.id.btnVolleyStart);
        btnVolleyStop = (Button)view.findViewById(R.id.btnVolleyStop);

        btnVolleyStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).registerListener(2);
                setAltitude();
            }
        });
        btnVolleyStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).unregisterListener();
            }
        });

        volleyRequest();
        return view;
    }

    public void setSensorText(float temp, float pressure, float humidity, float altitude, String timestamp) {
        volleySensorTemp.setText("" + temp + " °C");
        volleySensorPressure.setText("" + pressure + " mbar");
        volleySensorHumidity.setText("" + humidity + " %");
        volleyAltitude.setText("" + altitude );
        volleyTime.setText(""+timestamp);
    }

    public void setAltitude(){
        ((MainActivity) getActivity()).setAltitude(apiPressure);
    }

    public void updateText(String response) {
        String temperature = "";

        String humidity = "";
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObject1 = jsonObject.getJSONObject("main");

                temperature = jsonObject1.getString("temp");
                apiPressure = jsonObject1.getString("pressure");
                humidity = jsonObject1.getString("humidity");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            volleyApiTemp.setText("FAIL");
            volleyApiPressure.setText("FAIL");
            volleyApiHumidity.setText("FAIL");

        }
        volleyApiTemp.setText("" + temperature);
        volleyApiPressure.setText("" + apiPressure);
        volleyApiHumidity.setText("" + humidity);

    }


    private void volleyRequest() {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://api.openweathermap.org/data/2.5/weather?id=2692969&units=metric&appid=4e868b7574c8bbc38d548d30ab186370";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.getMessage(), error);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
