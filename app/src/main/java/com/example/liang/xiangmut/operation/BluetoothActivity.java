package com.example.liang.xiangmut.operation;

import android.Manifest;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.example.liang.xiangmut.R;

/**
 * 主页
 * 
 * @Project App_Bluetooth
 * @Package com.android.bluetooth
 * @author chenlin
 * @version 1.0
 * @Date 2013年6月2日
 */
@SuppressWarnings("deprecation")
public class BluetoothActivity extends TabActivity {
	static AnimationTabHost mTabHost;//动画tabhost
	static String BlueToothAddress;//蓝牙地址
	static Type mType = Type.NONE;//类型
	static boolean isOpen = false;

	//类型：
	enum Type {
		NONE, SERVICE, CILENT
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)//checkSelfPermission返回的结果如果是0表示同意，1表示禁止
		{
			ActivityCompat.requestPermissions(BluetoothActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);//当调用requestPermissions时，系统会自动创建一个对话框
		}else {

		}
		setContentView(R.layout.main);
		
		initTab();
	}

	private void initTab() {
		//初始化
		mTabHost = (AnimationTabHost) getTabHost();
		//添加tab
		mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("bluetooth通信", getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(this, DeviceActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("wifi通信", getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(this, WifiAvctivity.class)));
		//添加监听
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId.equals("Tab1")) {
					//TODO
				}
			}
		});
		//默认在第一个tabhost上面
		mTabHost.setCurrentTab(0);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, "address:", Toast.LENGTH_SHORT).show();
	}
	
}