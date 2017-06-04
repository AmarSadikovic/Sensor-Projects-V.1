package se.mah.af6851.project2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Runnable {

    private ConnectFragment connectFragment;
    private ShowMapFragment showMapFragment;

    private FragmentManager fm;
    //    private SendData sendData;
    private SendDataToServer sendDataToServer;
    private GetData getData;
    private int port = 7117;
    private String inetAddress = "195.178.227.53";
    private Thread thread;
    private ArrayList<String> groups;
    private String testingTheLimits = "1231";
    private Socket socket;

    private String grpID = "";
    private double longitude;
    private double latitude;
    private ArrayList<User> userList = new ArrayList<User>();

    private boolean mapActivated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectFragment = new ConnectFragment();
        thread = new Thread(this);
        thread.start();
        swapFragment(connectFragment);

    }

    public String getUsername() {
        String username = connectFragment.getUsername();
        return username;
    }

    public String getGroupname() {
        String groupname = connectFragment.getGroupname();
        return groupname;
    }

    public String getGrpID() {
        return grpID;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ArrayList<User> getUserlist() {
        return userList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
    public boolean getMapActivated(){
        return mapActivated;
    }

    public void updateUsers(JSONObject object) {
        userList = new ArrayList<User>();
        try {
            JSONArray jsonArray = object.getJSONArray("location");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("member");
                String longitude = obj.getString("longitude");
                String latitude = obj.getString("latitude");
                userList.add(new User(name, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMapInFrag() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMapFragment.updateMap();
            }
        });


    }

    public void swapToMap() {
        showMapFragment = new ShowMapFragment();
        swapFragment(showMapFragment);
        mapActivated = true;
    }
    public void swapToLogin(){
        connectFragment = new ConnectFragment();
        swapFragment(connectFragment);
        mapActivated = false;
    }

    public void swapFragment(Fragment frag) {
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl, frag);
        ft.commit();
    }

    public void setActiveGroup(JSONObject object) {
        try {
            grpID = object.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerGroup() {
        String newGroup = "{\"type\":\"register\",\"group\":\"" + getGroupname() + "\",\"member\":\"" + getUsername() + "\"}";
        sendDataToServer.writeToServer(newGroup);
    }
    public void unregister(){
        String unRegisterGrp = "{\"type\":\"unregister\",\"id\":\"" + getGrpID()+"\"}";
        sendDataToServer.writeToServer(unRegisterGrp);
    }

    public void requestMembers() {
        String requestMember = "{\"type\":\"members\",\"group\",:\"" + getGroupname() + "\"}";
        sendDataToServer.writeToServer(requestMember);
    }

    public void requestGroups() {
//        String testAddGroup = "{\"type\":\"register\",\"group\":\"KoffesGrupp2\",\"member\":\"Koffe4\"}";
//        sendDataToServer.writeToServer(testAddGroup);
        String requestString = "{\"type\":\"groups\"}";
        sendDataToServer.writeToServer(requestString);
    }

    public void sendPosition(double longitude, double latitude) {
        String sendLoc = "{\"type\":\"location\",\"id\":\"" + getGrpID() + "\",\"longitude\":\""+longitude+"\",\"latitude\":\""+latitude+"\"}";
        System.out.println("The string to send: " + sendLoc);
        sendDataToServer.writeToServer(sendLoc);
    }

    public void constructGroups(JSONObject jsonObject) {
        groups = new ArrayList<String>();
        try {
            JSONArray list = jsonObject.getJSONArray("groups");
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentGroup = list.getJSONObject(i);
                String currentGroupString = currentGroup.getString("group");
                groups.add(currentGroupString);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectFragment.refreshGroups(groups);
            }
        });
    }


    public void run() {
        System.out.println("test");
        try {
            socket = new Socket(inetAddress, port);
            getData = new GetData(socket, this);
            sendDataToServer = new SendDataToServer(socket, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
    }

}
