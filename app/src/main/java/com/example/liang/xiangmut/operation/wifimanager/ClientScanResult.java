package com.example.liang.xiangmut.operation.wifimanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Supun Athukorala on 9/9/2015.
 */
public class ClientScanResult implements Parcelable {

    private String IpAddr;

    private String HWAddr;

    private String Device;

    private boolean isReachable;

    public ClientScanResult(String ipAddress, String hWAddress, String device, boolean isReachable) {
        super();
        IpAddr = ipAddress;
        HWAddr = hWAddress;
        Device = device;
        this.setReachable(isReachable);
    }

    public String getIpAddress() {
        return IpAddr;
    }


    public void setReachable(boolean isReachable) {
        this.isReachable = isReachable;
    }

    public boolean isReachable() {
        return isReachable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.IpAddr);
        dest.writeString(this.HWAddr);
        dest.writeString(this.Device);
        dest.writeByte(this.isReachable ? (byte) 1 : (byte) 0);
    }

    protected ClientScanResult(Parcel in) {
        this.IpAddr = in.readString();
        this.HWAddr = in.readString();
        this.Device = in.readString();
        this.isReachable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ClientScanResult> CREATOR = new Parcelable.Creator<ClientScanResult>() {
        @Override
        public ClientScanResult createFromParcel(Parcel source) {
            return new ClientScanResult(source);
        }

        @Override
        public ClientScanResult[] newArray(int size) {
            return new ClientScanResult[size];
        }
    };
}