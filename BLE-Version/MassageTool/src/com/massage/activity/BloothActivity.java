package com.massage.activity;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

import com.massage.activity.ui.ToggleView;
import com.massage.activity.ui.ToggleView.OnSwitchStateUpdateListener;
import com.massage.adapter.DeviceAdapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class BloothActivity extends Activity{
	

    private ToggleView toggleViewStatus,toggleViewSearch;
    private ListView bloothList = null;
    
    private BluetoothAdapter bluetoothAdapter = null;
    private DeviceAdapter mAdapter = null;
    Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();//扫描设备列表
//    /**
//     *广播接收扫描蓝牙信息结果
//     */
//    private BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			String action = intent.getAction();
//			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//				mDevices.add(device);
//				mAdapter.notifyDataSetChanged();
//			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//				Toast.makeText(getApplicationContext(), "开始扫描", Toast.LENGTH_SHORT).show();
//				
//
//			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//				Toast.makeText(getApplicationContext(), "停止扫描", Toast.LENGTH_SHORT).show();
//			} 
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_blooth);
		mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持连接", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "不支持设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        bluetoothAdapter.isEnabled();
		toggleViewStatus = (ToggleView) findViewById(R.id.toggleViewStatus); //蓝牙状态
		toggleViewSearch = (ToggleView) findViewById(R.id.toggleViewSearch); //蓝牙搜索
		bloothList = (ListView) findViewById(R.id.bloothList); //显示蓝牙列表
		mAdapter = new DeviceAdapter(mDevices, getApplicationContext());
	
		bloothList.setAdapter(mAdapter);
		bloothList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter.getCount() > 0) { 
					
					 BluetoothDevice device1 = mAdapter.getItem(position);
				        if (device1 == null) return;
				        Intent intent1 = new Intent(BloothActivity.this,HomeActivity.class);
				        intent1.putExtra(HomeActivity.EXTRAS_DEVICE_NAME, device1.getName());
				        intent1.putExtra(HomeActivity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
				        if (mScanning) {
				            bluetoothAdapter.stopLeScan(mLeScanCallback);
				            mScanning = false;
				        }
				        startActivity(intent1);
				}
				
			}
		});


		toggleViewStatus.setOnSwitchStateUpdateListener(new OnSwitchStateUpdateListener() {
	    @Override
		public void onStateUpdate(boolean state) {
				// TODO Auto-generated method stub
				if (state) { //打开蓝牙
					if (!bluetoothAdapter.isEnabled()) {//蓝牙不可用，则打开蓝牙
						bluetoothAdapter.enable();
					}
					
				} else { // 关闭蓝牙
					if (bluetoothAdapter.isEnabled()) {//蓝牙打开中
						bluetoothAdapter.disable();
					}
				}
			}
		});
		toggleViewSearch.setOnSwitchStateUpdateListener(new OnSwitchStateUpdateListener() {
			
			@Override
			public void onStateUpdate(boolean state) {
				// TODO Auto-generated method stub
				if (state) { //搜索蓝牙,扫描蓝牙设备
	                scanLeDevice(true);
	                //mDevListAdapter.;
	                mAdapter.clear();
	                mAdapter.notifyDataSetChanged();
					
				} else { // 关闭
					 scanLeDevice(false);
				}
				
			}
		});
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }


	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//unregisterReceiver(mBlueToothReceiver);//解除注册
	}



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    // invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.addDevice(device);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	};

}
