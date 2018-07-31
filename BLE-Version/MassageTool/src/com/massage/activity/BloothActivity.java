package com.massage.activity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

import com.massage.activity.ui.ToggleView;
import com.massage.activity.ui.ToggleView.OnSwitchStateUpdateListener;
import com.massage.adapter.DeviceAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class BloothActivity extends Activity{
	

    private ToggleView toggleViewStatus,toggleViewSearch;
    private ListView bloothList = null;
    
    private BluetoothAdapter bluetoothAdapter = null;
    private OutputStream mOutputStream;
    private DeviceAdapter mAdapter = null;
    Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();//扫描设备列表
    /**
     *广播接收扫描蓝牙信息结果
     */
    private BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				mDevices.add(device);
				mAdapter.notifyDataSetChanged();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "开始扫描", Toast.LENGTH_SHORT).show();

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(getApplicationContext(), "停止扫描", Toast.LENGTH_SHORT).show();
			} 
		}
	};

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
            Toast.makeText(this, "连接不成功", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
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
				        Intent intent1 = new Intent(BloothActivity.this,
								HomeActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //mLeDeviceListAdapter.clear();
               // scanLeDevice(true);
                //mDevListAdapter.;
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_stop:
              //  scanLeDevice(false);
                break;
        }
        return true;
    }	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBlueToothReceiver);//解除注册
	}

	private void connectDevice( final BluetoothDevice bluetoothDevice) {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
				//链接蓝牙
				try {
					//UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb")
//					byte uuid = (byte) 0x88;
//					String vid_str =String.format("%02x", uuid );
					BluetoothSocket bSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
				//	BluetoothSocket bSocket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
	          
							//bluetoothDevice.getUuids()[0].getUuid());
					bSocket.connect();//建立连接
					mOutputStream = bSocket.getOutputStream();//获取输出流，向蓝牙服务端写指令
					runOnUiThread(new Runnable() {
						public void run() {
							Log.d("test", "连接成功击了");
							System.out.println("lainjeichegng");
							Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_LONG).show();
						}
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
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
