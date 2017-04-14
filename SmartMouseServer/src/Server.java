import java.awt.*;
import java.awt.event.InputEvent;
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
                            String[] stringPos = msg.split(",");
                            String matcher = stringPos[0];
                            switch (matcher) {
                                case "V":
                                    double vx = Double.parseDouble(stringPos[1]);
                                    double vy = Double.parseDouble(stringPos[2]);
                                    /// We update the mouse location in the screen according to the received message
                                    updateCursor(vx, vy);
                                    break;

                                case "LC":
                                    leftClick();
                                    break;

                                case "RC":
                                    rightClick();
                                    break;

                                case "S":
                                    double x = Double.parseDouble(stringPos[1]);
                                    double y = Double.parseDouble(stringPos[2]);
                                    scroll(x, y);
                                    break;

                                default:
                                    System.err.println("Problem, default value should not be reached !");

                            }
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

    private static void updateCursor(double vx, double vy) {
        double alpha = 0.02; /// This is a factor of speed we could be changing (User speed)

        try {
            /// The robot useful to move the mouse then
            Robot robot = new Robot();

            /// Get current mouse position
            Point pos = MouseInfo.getPointerInfo().getLocation();

            // Auto delay
            robot.mouseMove((int)(pos.x + alpha*vx), (int)(pos.y + alpha*vy));

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void leftClick() {
        try {
            /// The robot useful to click
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void rightClick() {
        try {
            /// The robot useful to click
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void scroll(double x, double y) {
        try {
            /// The robot useful to click
            Robot robot = new Robot();
            robot.mouseWheel((int)x);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}