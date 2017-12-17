package com.example.liang.xiangmut.operation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liang.xiangmut.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;


/**
 * Created by liang on 2017/11/24.
 */

public class UdpWifiChatActivity extends Activity implements View.OnClickListener {
    static final int SocketServerPORT = 8080;
    private static final int SHARE_PICTURE = 2;
    private static final int REQUEST_PATH = 1;
    private boolean xunhuan=true;
    private TextView infoPort;
    private EditText edittext;
    private WifiManager wifi;
    private DatagramSocket serverSocket = null;
    //private ServerSocket fileServerSocket;
    // private FileReceiverThread fileReceiverThread;
    private DatagramSocket clientSocket;
    private String clientIpAddress;
    private PrintWriter outp = null;
    private BufferedReader inp = null;
    private ArrayList<String> recQue;
    private String[] values;
    private ArrayAdapter adapter;
    private ListView listView;
    private String username;
    private Button connectClientButton;
    private Thread startClient;
    private Button send;
    private boolean panduan;
    private int i=0;
    private Button lianxu;
    private int suiji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udp_activity_private_chat);
        username = (String) getIntent().getExtras().get("name");
        clientIpAddress = (String) getIntent().getExtras().get("ipAddress");
//        TextView userNm = (TextView) findViewById(R.id.usrName);
//        userNm.setText(username);


//        TextView infoIp = (TextView) findViewById(R.id.infoip);
//

        lianxu= (Button) findViewById(R.id.lianxu);
        lianxu.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        recQue = new ArrayList<>();
//
        send = (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(this);
//        Button shareButton = (Button) findViewById(R.id.buttonshare);
//        shareButton.setOnClickListener(this);
//
//        Button selectButton = (Button) findViewById(R.id.buttonSelect);
//        selectButton.setOnClickListener(this);
//        edittext = (EditText) findViewById(R.id.editText);

//            connectClientButton = (Button) findViewById(R.id.connect);
//            connectClientButton.setOnClickListener(this);

//        infoIp.setText("Local Address : " + getIpAddress());

        Thread serverThread = new Thread(new chatReceiver());
        serverThread.start();

//        fileReceiverThread = new FileReceiverThread();
//        fileReceiverThread.start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend:
                EditText text = (EditText) findViewById(R.id.editText2);
                String textMsg = text.getText().toString();
                text.setText("");
                recQue.add("我:"+textMsg);
                //updateUIToast(message);
                updateListView(textMsg);
                sendMessage sendMessage = new sendMessage(textMsg);
                new Thread(sendMessage).start();
                break;
            case R.id.lianxu:
                lianxu.setText("停止发送");
                if(i==0){
                    panduan=true;
                }else if(i!=0){
                    lianxu.setText("测试完毕");
                    panduan=false;
                    i=0;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        i=1;
                        Random random=new Random();
                        while (panduan)
                        {
                            suiji=random.nextInt(100);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            recQue.add("我:"+suiji);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateListView(String.valueOf(suiji));
                                }
                            });
                            new sendMessage(String.valueOf(suiji)).start();
                        }
                    }
                }).start();
                break;
//
//                case R.id.connect:
//
//                    Thread startClient = new Thread(new chatSender());
//                    startClient.start();
//                    break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (serverSocket != null) {
            serverSocket.close();
        }
//        if (clientSocket != null) {
//            clientSocket.close();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    private void updateUI(final String serverIp, final String clientIp) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //infoPort.setText("Sender: " + serverIp + " | Receiver : " + clientIp);
            }
        });
    }

    private void updateListView(final String message) {

        values = new String[recQue.size()];
        for (int x = 0; x < recQue.size(); x++) {
            values[x] = recQue.get(x);
        }
        //Toast.makeText(getApplicationContext(), message + " : " + recQue.size(), Toast.LENGTH_SHORT).show();
        adapter = new ArrayAdapter<>(UdpWifiChatActivity.this, R.layout.list_white_text, R.id.list_content, values);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount() - 1);
        Log.d("cSharing", "Send : " + message);

    }

    private void updateUIToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                String curFileName = data.getStringExtra("GetFileName");
                String curFilePath = data.getStringExtra("GetPath");
                edittext.setText(curFilePath + curFileName);
            }
        }
    }

/*        private class chatSender implements Runnable {

            public void run() {
                updateUIToast("正在连接客户端...");

                try {
                    clientSocket = new Socket(clientIpAddress, SocketServerPORT + 1);
                    clientSocket.setReuseAddress(true);
                    //clientSocket.bind(new InetSocketAddress(SocketServerPORT + 1));

                    updateUI(getIpAddress(), clientSocket.toString());

                    outp = new PrintWriter(clientSocket.getOutputStream(), true);
                    inp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // System.out.println(clientSocket.getInetAddress().getHostAddress() + " is ther server");
                    updateUIToast("连上"+clientSocket.getInetAddress().getHostAddress() + "了");
                } catch (IOException e) {
                    e.printStackTrace();
                    // updateUIToast(e.toString());

                }

                if (clientSocket != null) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectClientButton.setVisibility(View.GONE);
                        }
                    });
                }


            }
        }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        xunhuan=false;
    }

    private class chatReceiver implements Runnable {

        public void run() {
            byte[] message = new byte[1024];
            int nreq = 1;
            final DatagramPacket datagramPacket = new DatagramPacket(message, message.length);
            try {
                serverSocket = new DatagramSocket(SocketServerPORT + 2);
            } catch (SocketException e) {
                e.printStackTrace();
            }
               /* try {

                    serverSocket = new DatagramSocket(SocketServerPORT + 2);
                    serverSocket.setReuseAddress(true);
                    //serverSocket.bind(new InetSocketAddress(SocketServerPORT + 1));

                   // final ServerSocket finalServerSocket = serverSocket;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //infoPort.setText("I'm waiting here: " + finalServerSocket.getLocalPort());
                            updateUIToast("创建服务端");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            while (xunhuan) {
                try {
                    serverSocket.receive(datagramPacket);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Toast.makeText(UdpWifiChatActivity.this, "" + datagramPacket.getAddress() + "," + datagramPacket.getPort() + "," + datagramPacket.getSocketAddress() + "," + URLDecoder.decode(new String(datagramPacket.getData(), "gb2312")).trim(), Toast.LENGTH_SHORT).show();
                                recQue.add(clientIpAddress + URLDecoder.decode(new String(datagramPacket.getData(), "gb2312")).trim());
                                updateListView(URLDecoder.decode(new String(datagramPacket.getData(), "gb2312")).trim());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Log.i("xinxi", "" + datagramPacket.getAddress() + "," + datagramPacket.getPort() + "," + datagramPacket.getSocketAddress() + "," + URLDecoder.decode(new String(datagramPacket.getData(), "gb2312")).trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
/*                    try {
                        assert serverSocket != null;
                        serverSocket.
                        serverSocket.

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("Creating thread ...");

                    if (socket != null) {
                        updateUI(getIpAddress(), socket.getInetAddress().getHostName());
                        Thread t = new chatReceiveHandler(socket, nreq);
                        t.start();
                    }*/
            }
        }
    }

 /*   private class chatReceiveHandler extends Thread {
        int n;

        chatReceiveHandler(int v) {
            n = v;
        }


        public void run() {
            try {
                BufferedReader inp = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
                boolean more_data = true;
                String line;

                while (more_data) {
                    line = inp.readLine();

                    if (line == null) {
                        //updateUIToast("line = null");
                        more_data = false;
                    } else {
                        //updateUIToast("Message '" + line + "' from " + clientIpAddress);
                        recQue.add(clientIpAddress + line);
                        updateListView(line);
                    }
                }
                newSocket.close();
                updateUIToast("连接断开");
            } catch (Exception e) {
                //updateUIToast("IO error " + e);
            }
        }
    }*/

/*    public class FileReceiverThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;
            try {
                fileServerSocket = new ServerSocket(SocketServerPORT);
                fileServerSocket.setReuseAddress(true);
                //fileServerSocket.bind(new InetSocketAddress(SocketServerPORT));


                while (true) {
                    socket = fileServerSocket.accept();

                    //---------------------------------
                    FileReceiveHandler fileReceiveHandler = new FileReceiveHandler(socket);
                    fileReceiveHandler.start();
                    //----------------------------------------
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }*/

//    private class FileReceiveHandler extends Thread {
//        Socket socket = null;
//
//
//        FileReceiveHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//
//        @Override
//        public void run() {
//
//            File file;
//            ObjectInputStream ois;
//            ois = null;
//            InputStream in = null;
//            byte[] bytes;
//            FileOutputStream fos = null;
//
//
//            File theDir = new File(Environment.getExternalStorageDirectory() + "/cSharing");
//
//
//            // if the directory does not exist, create it
//            if (!theDir.exists()) {
//                System.out.println("creating directory: " + "cSharing");
//                boolean result = false;
//
//                try {
//                    theDir.mkdir();
//                    result = true;
//                } catch (SecurityException ignored) {
//                }
//                if (result) {
//                    System.out.println("DIR created");
//                }
//            }
//            int length = new File(Environment.getExternalStorageDirectory() + "/cSharing").listFiles().length;
//            String fileName = "test" + (length + 1) + ".png";
//
//
//            try {
//                in = socket.getInputStream();
//            } catch (IOException ex) {
//                System.out.println("Can't get socket input stream. ");
//            }
//            try {
//                ois = new ObjectInputStream(in);
//            } catch (IOException e1) {
//                System.out.println("Can't get Object Input Stream. ");
//                e1.printStackTrace();
//            }
//
//            try {
//                assert ois != null;
//                fileName = ois.readUTF();
//            } catch (IOException e) {
//                System.out.println("Can't get file name. ");
//                e.printStackTrace();
//            }
//            file = new File(Environment.getExternalStorageDirectory() + "/cSharing", fileName);
//            try {
//                bytes = (byte[]) ois.readObject();
//            } catch (ClassNotFoundException | IOException e) {
//                System.out.println("Can't read Object . ");
//                bytes = new byte[0];
//                e.printStackTrace();
//            }
//
//            try {
//                fos = new FileOutputStream(file);
//            } catch (FileNotFoundException e1) {
//               // System.out.println("Can't get file output stream . ");
//                e1.printStackTrace();
//            }
//
//
//            try {
//                assert fos != null;
//                fos.write(bytes);
//                WifiChatActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(WifiChatActivity.this, "Finished", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                recQue.add(fileName);
//                updateListView(fileName);
//            } catch (IOException e1) {
//                //System.out.println("Can't file output stream write . ");
//                e1.printStackTrace();
//            } finally {
//                if (fos != null) {
//
//                    try {
//                        fos.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private class sendMessage extends Thread {
        int length;
        String message;
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;

        sendMessage(String message) {
            this.message = username + " : " + message;
        }

        @Override
        public void run() {
            try {
                datagramSocket = new DatagramSocket();
                length = message.length();
                byte[] bytes = message.getBytes("gb2312");
                datagramPacket = new DatagramPacket(bytes, length, InetAddress.getByName(clientIpAddress), SocketServerPORT + 2);
                datagramSocket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        @Override
//        protected Boolean doInBackground(Void... params) {
//
////            if (message != null) {
////                if (outp != null) {
////                    outp.println(message);
////                    recQue.add("我" + message);
////                    //updateUIToast(message);
////                    updateListView(message);
////                } else {
////                    updateUIToast("不能连接到用户");
////                }
////            } else {
////                //updateUI("", "Problem in connection..!");
////            }
//
//            return true;
//        }
    }


}
