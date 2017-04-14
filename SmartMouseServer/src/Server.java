import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {

        final ServerSocket serveurSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;

        try {
            serveurSocket = new ServerSocket(8000);
            System.out.println("Server launched !");
            clientSocket = serveurSocket.accept();
            System.out.println("A client has connected !");
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread recevoir = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        // tant que le client est connecté
                        while (msg != null) {
                            System.out.println("Client : " + msg);
                            msg = in.readLine();
                            /// We update the mouse location in the screen according to the received message
                            updateCursor(msg);
                        }
                        // sortir de la boucle si le client s'est déconnecté
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

    private static void updateCursor(String newPos) {
        double alpha = 0.01; /// This is a factor of speed we could be changing (User speed)
        String[] stringPos = newPos.split(",");
        double vx = Double.parseDouble(stringPos[0]);
        double vy = Double.parseDouble(stringPos[1]);

        try {
            /// The robot useful to move the mouse then
            Robot robot = new Robot();

            /// Get current mouse position
            Point pos = MouseInfo.getPointerInfo().getLocation();

            // Auto delay
            robot.setAutoDelay(40);
            robot.setAutoWaitForIdle(true);
            robot.mouseMove((int)(alpha*(pos.x+vx)), (int)(alpha*(pos.y+vy)));

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}