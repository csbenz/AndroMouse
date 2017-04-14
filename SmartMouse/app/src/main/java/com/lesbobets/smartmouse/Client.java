package com.lesbobets.smartmouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private PrintWriter out;
    private String myIp = "192.168.8.107";

    public Client() throws IOException {
        this("192.168.8.107", 8000);
    }

    public Client(String ipAddress, int port) throws IOException {
        clientSocket = new Socket(ipAddress, port);
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    public void send(final double[] coord) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println(coord);
                out.flush();
            }
        }).start();
    }
}
