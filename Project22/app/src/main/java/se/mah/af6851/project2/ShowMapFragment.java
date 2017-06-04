package se.mah.af6851.project2;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MainActivity main;
    private Button btnUnRegister;
    private Button btnRandomCord;
    private TextView tvGroupName;
    //Google maps stuff
    private LocationManager locationManager;
    private locList locationListener;
    //----------------
    private ArrayList<User> userList;

    public ShowMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_map, container, false);
        main = ((MainActivity) getActivity());
        locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new locList();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
//            System.out.println("ASKING FOR PERMISSION");
            Log.d("PERMISSION ASK", "Asking for permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
        }
        btnUnRegister = (Button) view.findViewById(R.id.btnUnRegister);
        btnRandomCord = (Button) view.findViewById(R.id.btnRandomCord);
        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnUnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.unregister();
                main.swapToLogin();
//                setMyLocation(55, 12);
            }
        });
        btnRandomCord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int max = 40;
                int min = 10;
                double randomLongitude = rand.nextInt(max + 1 - min) + min;
                double randomLatitude = rand.nextInt(max + 1 - min) + min;
                Toast.makeText(getActivity(), "Latitude: "+randomLatitude+" Longitude: "+randomLongitude, Toast.LENGTH_SHORT).show();
                main.sendPosition(randomLongitude, randomLatitude);


            }
        });
        tvGroupName = (TextView) view.findViewById(R.id.tvGroupname);
        tvGroupName.setText(main.getGroupname());
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng sydney = new LatLng(-33.852, 151.211);
//
//        MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("Random position");
//        googleMap.addMarker(markerOptions);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        checkPermission("Permission", 1, 1);
//        mMap.setMyLocationEnabled(true);
    }
    public void updateMap(){
        try {
            mMap.clear();
            userList = ((MainActivity) getActivity()).getUserList();
            for(int i = 0; i< userList.size(); i++){
                if(!userList.get(i).getLatitude().equals("NaN") && !userList.get(i).getLongitude().equals("NaN")) {
                    LatLng latLng = new LatLng(Double.parseDouble(userList.get(i).getLatitude()), Double.parseDouble(userList.get(i).getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(userList.get(i).getName()));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public void setMyLocation(double latitude, double longitude) {
//        LatLng myPos = new LatLng(latitude, longitude);
//        MarkerOptions markerOptions = new MarkerOptions().position(myPos).title("Amar Sadikovic");
//        mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
////        checkPermission("Permission", 1, 1);
//    }

    public void onPause() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            Log.d("SecurityException", "Remove listener i on pause");
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Call that works like a thread, asks for location every third second and send its to the server.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);

            List<String> providers = locationManager.getAllProviders();
            if (providers != null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    main.sendPosition(loc.getLongitude(), loc.getLatitude());
                    System.out.println("'''''"+loc.getLatitude() + ": " + loc.getLongitude());
                }
                //Hårdkodad om inte gps fungerar inomhus
//                main.sendPosition(30, 30);
            }
//            System.out.println("ASKING FOR PERMISSION");
        }
    }

    private class locList implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getActivity(), "COrdinates; "+location.getLongitude(), Toast.LENGTH_SHORT).show();
            System.out.println("Location changeeeeeeeeeeeeeeeed: " + location.getLatitude() + ": " + location.getLongitude());
            main.sendPosition(location.getLongitude(), location.getLatitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
