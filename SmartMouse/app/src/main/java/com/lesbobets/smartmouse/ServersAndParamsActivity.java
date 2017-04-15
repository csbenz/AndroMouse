package com.lesbobets.smartmouse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ServersAndParamsActivity extends AppCompatActivity {

    private static final String TAG = "ServersAndParamsActivity";

    private SetAdapter<String> listAdapter;
    private ListView serversList;
    private static DatagramSocket c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_and_params);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listAdapter = new SetAdapter<>(this, android.R.layout.simple_list_item_1);

        serversList = (ListView)findViewById(R.id.servers_list);
        serversList.setAdapter(listAdapter);
        serversList.setItemsCanFocus(true);
        serversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (serversList.getItemAtPosition(position)).toString();
                final InetAddress ip = listAdapter.getValue(text);

                // Send a packet in another thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            byte[] sendData;
                            sendData = ("START_REQUEST").getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, MainActivity.PORT_DISCOVERY);
                            c.send(sendPacket);

                            //Close the port!
                            c.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String ipString = ip.toString().substring(1);
                Log.d("<<<<<", "IP is " + ipString);
                intent.putExtra("IP", ipString);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshComputerList();
            }
        });

        /// Refresh the list once at start
        refreshComputerList();
    }

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
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), MainActivity.PORT_DISCOVERY);
                        c.send(sendPacket);
                        Log.d("BROADCAST", getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
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
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, MainActivity.PORT_DISCOVERY);
                                c.send(sendPacket);
                            } catch (Exception e) {
                            }

                            Log.d("BROADCAST", getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                        }
                    }

//                    Log.d("BROADCAST", getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

                    long time = System.currentTimeMillis();
                    long TIMER = 200;
                    while (System.currentTimeMillis() < time + TIMER) {
                        //Wait for a response
                        byte[] recvBuf = new byte[1000];
                        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                        c.receive(receivePacket);

                        //We have a response
//                        Log.d("BROADCAST", getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                        //Check if the message is correct
                        final String message = new String(receivePacket.getData()).trim();
                        final String[] messages = message.split(", ");
                        if (messages[0].equals("DISCOVER_RESPONSE")) {
                            final InetAddress serverIp = receivePacket.getAddress();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.add(messages[1], serverIp);
                                }
                            });
                        }
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }


    public class SetAdapter<T> extends ArrayAdapter<T> {
        private Map<T, InetAddress> mMap;

        public SetAdapter(@NonNull Context context, @LayoutRes int resource, Map<T, InetAddress> map) {
            super(context, resource);
            mMap = map;
        }

        public SetAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            mMap = new HashMap<>();
        }

        public void add(@Nullable T object, InetAddress ip) {
            mMap.put(object, ip);
            super.clear();
            super.addAll(mMap.keySet());
        }

        public InetAddress getValue(T key) {
            return mMap.get(key);
        }
    }

}
