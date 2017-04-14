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

    public ServerContacter() {
        this("192.168.8.107", 8000);
    }

    public ServerContacter(final String ipAddress, final int port) {
//        clientSocket = new Socket(ipAddress, port);
//        out = new PrintWriter(clientSocket.getOutputStream());
//        Log.d("ServerContacter", out.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ipAddress, port);
                    out = new PrintWriter(clientSocket.getOutputStream());
                    Log.d("BLABLA", "run: Initialized Client successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("BLABLA", "run: Initialize Client FAILED");
                }
                ;
            }
        }).start();
    }

    public void send(final double xVelocity, final double yVelocity) {
        final Double[] velocities = new Double[]{xVelocity, yVelocity};
        send(velocities);
    }

    public void send(final Double[] velocities) {
//        out.println(Arrays.toString(velocities));
//        out.flush();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && out != null) {
                    out.println(Arrays.toString(velocities));
                    out.flush();
                }
            }
        }).start();
    }
}
