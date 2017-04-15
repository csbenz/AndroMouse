package com.lesbobets.smartmouse;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerContacter {

    private Socket clientSocket;
    private PrintWriter out;

    public ServerContacter(final String ipAddress, final int port) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep to let the server change process
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    clientSocket = new Socket(ipAddress, port);
                    out = new PrintWriter(clientSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
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
