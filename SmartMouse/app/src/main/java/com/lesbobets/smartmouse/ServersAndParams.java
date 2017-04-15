package com.lesbobets.smartmouse;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServersAndParams extends AppCompatActivity {

    private ArrayAdapter<String> listAdapter;
    private ListView serversList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_and_params);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        serversList = (ListView)findViewById(R.id.servers_list);
        serversList.setAdapter(listAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private static DatagramSocket c;
    public void refreshComputerList () {
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
                            }

                            System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                        }
                    }

                    System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

                    //Wait for a response
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    c.receive(receivePacket);

                    //We have a response
                    System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                    //Check if the message is correct
                    String message = new String(receivePacket.getData()).trim();
                    if (message.equals("DISCOVER_RESPONSE")) {
                        //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                        InetAddress serverIp = receivePacket.getAddress();
                        listAdapter.add(serverIp.toString());
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
