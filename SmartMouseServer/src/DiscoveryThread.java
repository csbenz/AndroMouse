import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by joachim on 4/14/17.
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
                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets !");
                byte[] recvBuf = new byte[1000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                /// Packet received
                System.out.println(getClass().getName() + ">>>Discovery packets received from : " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received! data : " + new String(packet.getData()));

                /// See if the packet holds the right command
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_REQUEST")) {
                    byte[] sendData = ("DISCOVER_RESPONSE, " + InetAddress.getLocalHost().getHostName()).getBytes();

                    /// Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
                if (message.equals("START_REQUEST")) {
                    System.out.println(">>>>Received a start request !!");
                    // We can just stop waiting for broadcasts message
                    break;
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
}
