import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Singleton for the discovery thread
 */
public class DiscoveryThread implements Runnable {
    private DatagramSocket socket;
    private int port = 7776;

    @Override
    public void run() {
        try {
            /// We prepare the broadcast
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                /// Receive a packet
//                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets !");
                byte[] recvBuf = new byte[1000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                /// Packet received
                System.out.println(getClass().getName() + ">>>Discovery packets received from : " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received! data : " + new String(packet.getData()));

                /// See if the packet holds the right command
                String message = new String(packet.getData()).trim();

                String[] stringPos = message.split(",");
                String matcher = stringPos[0];
                switch (matcher) {
                    case "DISCOVER_REQUEST":
                        byte[] sendData = ("DISCOVER_RESPONSE, " + InetAddress.getLocalHost().getHostName()).getBytes();

                        /// Send a response
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);

                        System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                        break;

                    default:
                        System.err.println("Problem, default value should not be reached !");

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static DiscoveryThread getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {
        private static final DiscoveryThread INSTANCE = new DiscoveryThread();
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
