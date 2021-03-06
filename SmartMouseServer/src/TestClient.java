import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by joachim on 4/14/17.
 */
public class TestClient {

    private static DatagramSocket c;

    public static void main (String[] args) throws IOException {


        new Thread(new Runnable() {
            @Override
            public void run() {
                // Find the server using UDP broadcast
                try {
                    //Open a random port to send the package
                    c = new DatagramSocket();
                    c.setBroadcast(true);

                    byte[] sendData = "DISCOVER_REQUEST".getBytes();

                    //Try the 255.255.255.255 first
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                        c.send(sendPacket);
                        System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
                    } catch (Exception e) {
                    }

                    // Broadcast the message over all the network interfaces
                    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                            continue; // Don't want to broadcast to the loopback interface
                        }

                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            if (broadcast == null) {
                                continue;
                            }

                            // Send the broadcast package!
                            try {
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                                c.send(sendPacket);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                        }
                    }

                    System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

                    //Wait for a response
                    byte[] recvBuf = new byte[1000];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    c.receive(receivePacket);

                    //We have a response
                    System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                    //Check if the message is correct
                    String message = new String(receivePacket.getData()).trim();
                    if (message.equals("DISCOVER_RESPONSE")) {
                        //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                        InetAddress serverIp = receivePacket.getAddress();
                    }

                    //Close the port!
                    c.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
