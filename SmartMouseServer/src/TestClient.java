import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by joachim on 4/14/17.
 */
public class TestClient {

    private static Socket clientSocket;
    private static PrintWriter out;
    private static String myIp = "192.168.8.107";

    public static void main (String[] args) throws IOException {

        clientSocket = new Socket(myIp, 8000);
        out = new PrintWriter(clientSocket.getOutputStream());


        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println("100,100");
                out.flush();
            }
        }).start();
    }
}
