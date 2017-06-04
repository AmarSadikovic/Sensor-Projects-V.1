package com.example.amar.project2;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Fragment that displays all the needed information
 * Uses async task to get information from the API
 */
public class ATFrag extends Fragment {

    private TextView asyncApiTtemp, asyncApiPressure, asyncApiHumidity;
    private TextView asyncSensorTemp, asyncSensorPressure, asyncSensorHumidity, asyncAltitude, asyncTime;
    private Button btnAsyncStart;
    private Button btnAsyncStop;
    private String apiPressure = "";

    public ATFrag() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_at, container, false);
        btnAsyncStart = (Button) view.findViewById(R.id.btnAsyncStart);
        btnAsyncStop = (Button) view.findViewById(R.id.btnAsyncStop);
        asyncApiTtemp = (TextView) view.findViewById(R.id.asyncApiTemp);
        asyncApiPressure = (TextView) view.findViewById(R.id.asyncApiPressure);
        asyncApiHumidity = (TextView) view.findViewById(R.id.asyncApiHumidity);
        asyncAltitude = (TextView) view.findViewById(R.id.asyncAltitude);

        asyncSensorTemp = (TextView) view.findViewById(R.id.asyncSensorTemp);
        asyncSensorPressure = (TextView) view.findViewById(R.id.asyncSensorPressure);
        asyncSensorHumidity = (TextView) view.findViewById(R.id.asyncSensorHumidity);

        asyncTime = (TextView) view.findViewById(R.id.asyncTime);


        btnAsyncStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).registerListener(1);
                setAltitude();

            }
        });
        btnAsyncStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).unregisterListener();
            }
        });


        new GetWeather().execute();
        return view;
    }

    /**
     *
     * @param temp temperature from the sensor
     * @param pressure pressure from the sensor
     * @param humidity humidity from the sensor
     * @param altitude altitude from the sensor/api
     * @param timestamp timestamp from the sensor
     */
    public void setSensorText(float temp, float pressure, float humidity, float altitude, String timestamp) {
        asyncSensorTemp.setText("" + temp + " °C");
        asyncSensorPressure.setText("" + pressure + " mbar");
        asyncSensorHumidity.setText("" + humidity + " %");
        asyncAltitude.setText("" + altitude);
        asyncTime.setText(""+timestamp);

    }

    public void setAltitude(){
        ((MainActivity) getActivity()).setAltitude(apiPressure);
    }

    /**
     * @param response api response
     */
    public void updateText(String response) {
        String apiTemperature = "";
        apiPressure = "";
        String apiHumidity = "";
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObject1 = jsonObject.getJSONObject("main");
                apiTemperature = jsonObject1.getString("temp");
                apiPressure = jsonObject1.getString("pressure");
                apiHumidity = jsonObject1.getString("humidity");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            asyncApiTtemp.setText("FAIL");
            asyncApiPressure.setText("FAIL");
            asyncApiHumidity.setText("FAIL");
            // getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, apiPressure)

        }
        asyncApiTtemp.setText("" + apiTemperature + " °C");
        asyncApiPressure.setText("" + apiPressure + " mbar");
        asyncApiHumidity.setText("" + apiHumidity + " %");

    }
    private class GetWeather extends AsyncTask<Void, Void, String> {


        private ProgressDialog pd;
        private String jsonWeather;

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE  WAS AN ERROR";
            }
            updateText(response);
        }

        /**
         *  This was a better and easier way to get the information from the api then using different
         *  classes.
         * @param voids
         * @return
         */
        @Override
        protected String doInBackground(Void... voids) {
            String mUrl = "http://api.openweathermap.org/data/2.5/weather?id=2692969&units=metric&appid=4e868b7574c8bbc38d548d30ab186370";

            try {
                URL url = new URL(mUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new
                            InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
    }
}
