package com.lesbobets.smartmouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class Client {

    private Socket clientSocket;
    private PrintWriter out;
    public Client() throws IOException {
        clientSocket = new Socket("127.0.0.1",8000);
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    public void send(double[] coord) {

        final double [] coordBis = coord;

        Thread envoyer = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                out.println(coordBis);
                out.flush();
            }
        });
        envoyer.start();

    }
}
