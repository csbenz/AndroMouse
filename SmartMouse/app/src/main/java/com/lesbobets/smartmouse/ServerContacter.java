package com.lesbobets.smartmouse;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerContacter {

    private Socket clientSocket;
    private PrintWriter out;
    private String myIp = "192.168.8.107";

    public ServerContacter() {
        this("192.168.8.107", 8000);
    }

    public ServerContacter(final String ipAddress, final int port) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ipAddress, port);
                    out = new PrintWriter(clientSocket.getOutputStream());
//                    Log.d("BLABLA", "run: Initialized Client successfully");
                } catch (IOException e) {
                    e.printStackTrace();
//                    Log.d("BLABLA", "run: Initialize Client FAILED");
                }
            }
        }).start();
    }

    public void sendVelocities(final double xVelocity, final double yVelocity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && out != null) {
                    out.println("V," + xVelocity + "," + yVelocity);
                    out.flush();
                }
            }
        }).start();
    }

    public void sendVelocities(final Double[] velocities) {
        sendVelocities(velocities[0], velocities[1]);
    }

    public void sendLeftClick() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && out != null) {
                    out.println("LC");
                    out.flush();
                }
            }
        }).start();

    }

    public void sendRightClick() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && out != null) {
                    out.println("RC");
                    out.flush();
                }
            }
        }).start();

    }

    public void sendScroll(final double distanceX, final double distanceY) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null && out != null) {
                    out.println("S,"+distanceX+","+distanceY);
                    out.flush();
                }
            }
        }).start();

    }
}
