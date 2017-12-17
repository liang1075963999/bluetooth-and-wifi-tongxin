package com.example.liang.xiangmut.operation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liang.xiangmut.R;

import java.util.ArrayList;

/**
 * Created by liang on 2017/11/21.
 */

public class WifiDeviceListAdapter extends BaseAdapter {
    private ArrayList<WifiDevicebean> mDatas;
    private LayoutInflater mInflater;

    public WifiDeviceListAdapter(Context context, ArrayList<WifiDevicebean> datas) {
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mDatas.size();
    }

    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        WifiDeviceListAdapter.ViewHolder viewHolder = null;
        WifiDevicebean item = mDatas.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new WifiDeviceListAdapter.ViewHolder((View) convertView.findViewById(R.id.list_child),
                    (TextView) convertView.findViewById(R.id.chat_msg));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (WifiDeviceListAdapter.ViewHolder) convertView.getTag();
        }

//        if (item.isReceive) {
            viewHolder.child.setBackgroundResource(R.drawable.msgbox_rec);
//        } else {
//            viewHolder.child.setBackgroundResource(R.drawable.msgbox_send);
//        }
        viewHolder.msg.setText(item.message);

        return convertView;
    }

    class ViewHolder {
        protected View child;
        protected TextView msg;

        public ViewHolder(View child, TextView msg) {
            this.child = child;
            this.msg = msg;

        }
    }
}