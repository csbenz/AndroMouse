import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/*
 * www.codeurjava.com
 */
public class Server {

    public static void main(String[] test) {

        final ServerSocket serveurSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            serveurSocket = new ServerSocket(8000);
            clientSocket = serveurSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread recevoir = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        //tant que le client est connecté
                        while (msg != null) {
                            System.out.println("Client : " + msg);
                            msg = in.readLine();
                        }
                        //sortir de la boucle si le client a déconecté
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            recevoir.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCursor(String newPos) {
        double alpha = 1.0; /// This is a factor of speed we could be changing
        String[] stringPos = newPos.split(",");
        double vx = Double.parseDouble(stringPos[0]);
        double vy = Double.parseDouble(stringPos[1]);

        // TODO: Robot thing but need to be separated
        try {
            Robot robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}