package com.example.liang.xiangmut.operation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

/**
 * Created by liang on 2017/11/21.
 */
public class WifiChatActivity extends Activity implements View.OnClickListener{
    static final int SocketServerPORT = 8080;
    private static final int SHARE_PICTURE = 2;
    private static final int REQUEST_PATH = 1;
    private boolean panduan=true;
    private TextView infoPort;
    private EditText edittext;
    private WifiManager wifi;
    private ServerSocket serverSocket = null;
    private ServerSocket fileServerSocket;
   // private FileReceiverThread fileReceiverThread;
    private Socket clientSocket;
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
    private Button lianxu;
    private boolean panduan1;
    private int i=0;
    private int suiji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        username = (String) getIntent().getExtras().get("name");
        clientIpAddress = (String) getIntent().getExtras().get("ipAddress");


//        TextView userNm = (TextView) findViewById(R.id.usrName);
//        userNm.setText(username);


//        TextView infoIp = (TextView) findViewById(R.id.infoip);
//


        listView = (ListView) findViewById(R.id.listView);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        lianxu= (Button) findViewById(R.id.lianxu);
        lianxu.setOnClickListener(this);
        recQue = new ArrayList<>();
//
        send= (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(this);
//        Button shareButton = (Button) findViewById(R.id.buttonshare);
//        shareButton.setOnClickListener(this);
//
//        Button selectButton = (Button) findViewById(R.id.buttonSelect);
//        selectButton.setOnClickListener(this);
//        edittext = (EditText) findViewById(R.id.editText);

        connectClientButton = (Button) findViewById(R.id.connect);
        connectClientButton.setOnClickListener(this);

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

                sendMessage sendMessage = new sendMessage(textMsg);
                sendMessage.execute((Void) null);

                break;
//
            case R.id.connect:

                Thread startClient = new Thread(new chatSender());
                startClient.start();
                break;
            case R.id.lianxu:
                lianxu.setText("停止发送");
                if(i==0){
                    panduan1=true;
                }else if(i!=0){
                    lianxu.setText("测试完毕");
                    panduan1=false;
                    i=0;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        i=1;
                        Random random=new Random();
                        while (panduan1)
                        {
                            suiji=random.nextInt(100);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendMessage sendMessage = new sendMessage(String.valueOf(suiji));
                            sendMessage.execute((Void) null);
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), message + " : " + recQue.size(), Toast.LENGTH_SHORT).show();
                adapter = new ArrayAdapter<>(WifiChatActivity.this, R.layout.list_white_text, R.id.list_content, values);
                listView.setAdapter(adapter);
                listView.setSelection(adapter.getCount() - 1);
            }
        });
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

    private class chatSender implements Runnable {

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
    }

    private class chatReceiver implements Runnable {

        public void run() {


            int nreq = 1;
            Socket socket = null;

            try {

                serverSocket = new ServerSocket(SocketServerPORT + 1);
                serverSocket.setReuseAddress(true);
                //serverSocket.bind(new InetSocketAddress(SocketServerPORT + 1));

                final ServerSocket finalServerSocket = serverSocket;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //infoPort.setText("I'm waiting here: " + finalServerSocket.getLocalPort());
                        updateUIToast("创建服务端");
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            while (panduan) {//!Thread.currentThread().isInterrupted()

                try {
                    assert serverSocket != null;
                    socket = serverSocket.accept();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                //System.out.println("Creating thread ...");

                if (socket != null) {
                    Thread t = new chatReceiveHandler(socket, nreq);
                        t.start();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        panduan=false;
    }

    private class chatReceiveHandler extends Thread {
        Socket newSocket;
        int n;

        chatReceiveHandler(Socket s, int v) {
            newSocket = s;
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
                        recQue.add(clientIpAddress+line);
                        updateListView(line);
                    }
                }
                newSocket.close();
                updateUIToast("连接断开");
            } catch (Exception e) {
                //updateUIToast("IO error " + e);
            }
        }
    }



    private class sendMessage extends AsyncTask<Void, Void, Boolean> {

        String message;

        sendMessage(String message) {

            this.message = username + " : " + message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (message != null) {
                if (outp != null) {
                    outp.println(message);
                    recQue.add("我"+message);
                    //updateUIToast(message);
                    updateListView(message);
                } else {
                    updateUIToast("不能连接到用户");
                }
            } else {
                //updateUI("", "Problem in connection..!");
            }

            return true;
        }
    }


}
