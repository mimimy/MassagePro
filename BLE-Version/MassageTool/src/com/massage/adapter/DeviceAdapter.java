package com.massage.adapter;

import java.util.ArrayList;

import com.massage.activity.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter  extends BaseAdapter{
	private ArrayList<BluetoothDevice> mDevice;
	private Context mContext;
	
	
	public DeviceAdapter(ArrayList<BluetoothDevice> mDevice, Context mContext) {
		super();
		this.mDevice = mDevice;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDevice.size();
	}
	public void addDevice(BluetoothDevice device) {
		if (!mDevice.contains(device)) {
			mDevice.add(device);
		}
	}
	public void clear(){
		mDevice.clear();
	}

	@Override
	public BluetoothDevice getItem(int position) {
		return mDevice.get(position);
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.mTvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			convertView.setTag(viewHolder);
		}else {
			viewHolder= (ViewHolder) convertView.getTag();
		}
		BluetoothDevice device = mDevice.get(position);
		viewHolder.mTvName.setText(device.getName());
		viewHolder.mTvAddress.setText(device.getAddress());
		return convertView;
	}
	class ViewHolder{
		TextView mTvName;
		TextView mTvAddress;
	}

}
