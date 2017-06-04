package se.mah.af6851.project2;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Amar on 2017-02-14.
 */

public class GetData implements Runnable {

    private Socket socket;
    private InputStream is;
    private DataInputStream dis;
    private OutputStream os;
    private DataOutputStream dos;
    private Thread thread;
    private MainActivity main;

    public GetData(Socket socket, MainActivity main) {
        this.socket = socket;
        this.main = main;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if (dis.available() > 0) {
                    String response = dis.readUTF();
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("type").equals("locations")){
                        //Location: Long and Lat might be swapped.!!! OBS!!!
                        System.out.println("Type = locations: "+response);
                        if(main.getMapActivated()) {
                            main.updateUsers(jsonObject);
                            main.updateMapInFrag();
                        }
                    }else if(jsonObject.getString("type").equals("groups")){
                        System.out.println("Type = groups: "+response);
                        main.constructGroups(jsonObject);
                    }else if(jsonObject.getString("type").equals("register")){
                        System.out.println("Type = register: "+response);
                        main.setActiveGroup(jsonObject);
                    }else if(jsonObject.getString("type").equals("members")){
                        System.out.println("Type = members: "+response);
                    }
                    //main.constructGroups(groups);
                    //String test = "Trying to fetch and print JSON: " + dis.readUTF();
                    //System.out.println(test);
                } else {
//                    System.out.println("Nothing to read");
                }
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }
}
