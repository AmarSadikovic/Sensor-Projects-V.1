package se.mah.af6851.project2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Amar on 2017-05-22.
 */

public class SendDataToServer extends Thread {
    private MainActivity main;
    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;

    public SendDataToServer(Socket socket, MainActivity main) {
        this.socket = socket;
        this.main = main;
        initializeStreams();
    }
    public void initializeStreams(){
        try {
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToServer(String message) {
        while (dos == null) {
            System.out.println("writing to server inside loop");
        }
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
