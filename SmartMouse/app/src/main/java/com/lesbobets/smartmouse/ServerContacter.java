package com.lesbobets.smartmouse;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerContacter {

    private DatagramSocket clientSocket;
    private InetAddress serverAdress;

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

                    serverAdress = InetAddress.getByName(ipAddress);
                    Log.d("<<<<", ipAddress + " is the server ip !");
                    clientSocket = new DatagramSocket(port, serverAdress);
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
                if (clientSocket != null) {
                    byte[] toSend = ("V," + xVelocity + "," + yVelocity).getBytes();
                    DatagramPacket packet = new DatagramPacket(toSend, toSend.length, serverAdress, MainActivity.PORT_PACKETS);
                    try {
                        clientSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    out.println("V," + xVelocity + "," + yVelocity);
//                    out.flush();
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
                if (clientSocket != null) {
                    byte[] toSend = ("LC").getBytes();
                    DatagramPacket packet = new DatagramPacket(toSend, toSend.length, serverAdress, MainActivity.PORT_PACKETS);
                    try {
                        clientSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    out.println("LC");
//                    out.flush();
                }
            }
        }).start();

    }

    public void sendRightClick() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null) {
                    byte[] toSend = ("RC").getBytes();
                    DatagramPacket packet = new DatagramPacket(toSend, toSend.length, serverAdress, MainActivity.PORT_PACKETS);
                    try {
                        clientSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    out.println("RC");
//                    out.flush();
                }
            }
        }).start();

    }

    public void sendScroll(final double distanceX, final double distanceY) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientSocket != null) {
                    byte[] toSend = ("S," + distanceX + "," + distanceY).getBytes();
                    DatagramPacket packet = new DatagramPacket(toSend, toSend.length, serverAdress, MainActivity.PORT_PACKETS);
                    try {
                        clientSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    out.println("S,"+distanceX+","+distanceY);
//                    out.flush();
                }
            }
        }).start();

    }
}
