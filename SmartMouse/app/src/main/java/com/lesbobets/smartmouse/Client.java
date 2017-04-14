package com.lesbobets.smartmouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private Socket clientSocket;
    private PrintWriter out;

    public Client() throws IOException {
        this("127.0.0.1", 8000);
    }

    public Client(String ipAddress, int port) throws IOException {
        clientSocket = new Socket(ipAddress, port);
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    public void send(final double xVelocity, final double yVelocity) {

        final double[] velocities = new double[]{xVelocity, yVelocity};

        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println(Arrays.toString(velocities));
                out.flush();
            }
        }).start();
    }
}
