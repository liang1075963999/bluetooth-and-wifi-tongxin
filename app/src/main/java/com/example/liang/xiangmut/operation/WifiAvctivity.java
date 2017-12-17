package com.example.liang.xiangmut.operation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.liang.xiangmut.R;
import com.example.liang.xiangmut.operation.wifimanager.ClientScanResult;
import com.example.liang.xiangmut.operation.wifimanager.WifiApiManager;

import java.util.ArrayList;

/**
 * Created by liang on 2017/11/21.
 */

public class WifiAvctivity extends Activity {

    private String username;
    private Button button;
    private WifiApiManager wifiApManager;
    //private String[] values;
    private ListView listView;
    //private ArrayAdapter<String> adapter;
    private ArrayList<WifiDevicebean> arrayList;
    private WifiDeviceListAdapter wifiDeviceListAdapter;
    private Intent i;
    private ClientScanResult clientScanResult;
    ;
    private ArrayList<ClientScanResult> clients;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clients = msg.getData().getParcelableArrayList("zhi");
            if(msg.what==1)
            button.setText("开始搜索");
            else if(msg.what==2)
                button.setText("重新搜索");
        }
    };
    //-------------- Client
/*
    TextView textPort;

    static final int SocketServerPORT = 8080;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing);
        wifiApManager = new WifiApiManager(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                clients = wifiApManager.getClientList(false);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("zhi", clients);
                Message message = new Message();
                message.what=1;
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
        button = (Button) findViewById(R.id.start_seach);

        listView = (ListView) findViewById(R.id.listView2);
        arrayList = new ArrayList<>();
        // username = (String) getIntent().getExtras().get("name");
        username = " ";
/*        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);*/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText("正在搜索");
                try {
                   // clients.clear();
                    arrayList.clear();
                } catch (Exception e) {

                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clients = wifiApManager.getClientList(false);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("zhi", clients);
                        Message message = new Message();
                        message.what=2;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }).start();
//                sendImage= new selectClient();
//                sendImage.execute((Void) null);

                //values = new String[clients.size()];
                try {
                    for (int i = 0; i < clients.size(); i++) {
                        clientScanResult = clients.get(i);
                        arrayList.add(new WifiDevicebean(clientScanResult.getIpAddress()));
                        //values[i] = "IpAddress: " + clientScanResult.getIpAddress();

                    }
                    //adapter = new ArrayAdapter<>(WifiAvctivity.this, R.layout.list_white_text, R.id.list_content, values);
                    //listView.setAdapter(adapter);
                    wifiDeviceListAdapter = new WifiDeviceListAdapter(WifiAvctivity.this, arrayList);
                    wifiDeviceListAdapter.notifyDataSetChanged();
                    listView.setAdapter(wifiDeviceListAdapter);
                } catch (Exception e) {

                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                          /*  Toast.makeText(getApplicationContext(), clients.get(position).getIpAddress(), Toast.LENGTH_SHORT).show();

                            i = new Intent(WifiAvctivity.this, WifiChatActivity.class);
                            i.putExtra("ipAddress",clients.get(position).getIpAddress());
                            i.putExtra("name", username);
                            startActivity(i);
*/
                        WifiDevicebean bean = arrayList.get(position);
                        String info = bean.message;
                        AlertDialog.Builder stopDialog = new AlertDialog.Builder(WifiAvctivity.this);
                        stopDialog.setTitle("连接");//标题
                        stopDialog.setMessage(bean.message);
                        stopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                button.setText("重新搜索");
                                AlertDialog.Builder xuanze=new AlertDialog.Builder(WifiAvctivity.this)
                                        .setTitle("选择协议")
                                        .setItems(new CharSequence[]{"TCP", "UDP"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(which==0)
                                                {
                                                    i = new Intent(WifiAvctivity.this, WifiChatActivity.class);
                                                    i.putExtra("ipAddress", clients.get(position).getIpAddress());
                                                    i.putExtra("name", username);
                                                    startActivity(i);
                                                    dialog.cancel();
                                                }else if(which==1)
                                                {
                                                    i = new Intent(WifiAvctivity.this, UdpWifiChatActivity.class);
                                                    i.putExtra("ipAddress", clients.get(position).getIpAddress());
                                                    i.putExtra("name", username);
                                                    startActivity(i);
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                xuanze.show();
                                //sendImage.cancel(true);
//                                if(sendImage!=null)
//                                {
//                                    Log.i("xinxi","mei");
//
//
//                                    button.setEnabled(true);
//                                }



                            }
                        });
                        stopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        stopDialog.show();
                    }

                });

            }
        });
    }
}
