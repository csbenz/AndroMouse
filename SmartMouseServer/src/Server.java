import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Server {

    public static void main(String[] args) {
        //while (true) {
            // We launch the service for discovering
            new Thread(DiscoveryThread.getInstance()).start();

            // And also the service for packet transmitting
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tcpPacketsExchanges();
                }
            }).start();
        //}
    }

    private static void tcpPacketsExchanges () {
        final DatagramSocket socket;

        try {
            socket = new DatagramSocket(7777);
            boolean looping = true;

            while (looping) {
                byte[] recvBuf = new byte[1000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                System.out.println(">>>Server waiting for a communication");
                socket.receive(packet);

                String msg = new String(packet.getData()).trim();
                    System.out.println("Client : " + msg);
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

                        case "QUIT":
                            looping = false;
                            break;

                        default:
                            System.err.println("Problem, default value should not be reached !");

                    }
            }
            // We close the socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateCursor(double vx, double vy) {
        double alpha = 0.015; /// This is a factor of speed we could be changing (User speed)

        try {
            Robot robot = new Robot();

            /// Get current mouse position
            Point pos = MouseInfo.getPointerInfo().getLocation();
            // Move
            robot.mouseMove((int)(pos.x + alpha*vx), (int)(pos.y + alpha*vy));

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void leftClick() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void rightClick() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void scroll(double x, double y) {
        try {
            Robot robot = new Robot();
            robot.mouseWheel((int)x);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}