package com.lesbobets.smartmouse;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ServerContacter {

    private Socket clientSocket;
    private PrintWriter out;
    private String myIp = "192.168.8.107";

    public ServerContacter() throws IOException {
        this("192.168.8.107", 8000);
    }

    public ServerContacter(String ipAddress, int port) throws IOException {
        clientSocket = new Socket(ipAddress, port);
        out = new PrintWriter(clientSocket.getOutputStream());
        Log.d("ServerContacter", out.toString());
    }

    public void send(final double xVelocity, final double yVelocity) {

//        final double[] velocities = new double[]{xVelocity, yVelocity};

        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println(xVelocity + "," + yVelocity);
                out.flush();
            }
        }).start();
    }
}
