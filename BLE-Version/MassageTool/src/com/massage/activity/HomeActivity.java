package com.massage.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.massage.activity.ui.NoScrollViewPager;
import com.massage.util.Constant;

import android.R.integer;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

 public class HomeActivity extends Activity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false; 
    boolean connect_status_bit=false;
    View viewPager1,viewPager2;
    NoScrollViewPager mviewPager;
    List<View> viewPagers = null;
    ContentAdapter mAdapter;
    private BluetoothLeService mBluetoothLeService;
	private RadioGroup radioGroup;
	RadioButton home,another;
	//��Ħģʽʱ��
	int time_lenth = 10;
	private CountDownTimer cmTimer;
	boolean isCounting = false;
	private StringBuffer sbValues;
	
    String dme ="";
	private EditText tx_show;
    Timer timer;
    TimerTask task1;

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("TAG", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };	
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                
                
                connect_status_bit=true;
                
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                
                updateConnectionState(R.string.disconnected);
                connect_status_bit=false;
   
                invalidateOptionsMenu();
               // clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));//�ι�ҳ������ݻش�
            }
        }
    };
    
    public void delay(int ms){
		try {
            Thread.currentThread();
			Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
	 }	    
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               //Toast.makeText(getApplicationContext(), "���ӳɹ�", Toast.LENGTH_LONG).show();
            	//tx_show.setText(text);
            }
        });
    }
    String da="";
    int len_g = 0;

    private void displayData( String data1 ) //����FFE1����͸������ͨ������
    {
    	dme += data1;
    	
    	if( data1==null)return;
    //	Toast.makeText(HomeActivity.this, data1, Toast.LENGTH_LONG).show();
    	byte[]by = mBluetoothLeService.hex2byte( data1.getBytes() );
    	if (by[0] == 0x73 && by[1] == 0x61) {//���ؿ�ʼ��ָֹͣ���
        	Toast.makeText(HomeActivity.this, data1, Toast.LENGTH_SHORT).show();
		} else if (by[0] == 0x73 && by[1] == 0x66) { //��ѯ״̬
        	Toast.makeText(HomeActivity.this, data1, Toast.LENGTH_SHORT).show();
        	switch (Constant.query_stutus) {
			case Constant.current_pressure:
				//by[3]
				break;

			default:
				break;
			}

		}  else if (by[0] == 0x73 && by[1] == 0x62) {
			Toast.makeText(HomeActivity.this, data1, Toast.LENGTH_SHORT).show();
		}  else if (by[0] == 0x73 && by[1] == 0x63) {
			Toast.makeText(HomeActivity.this, data1, Toast.LENGTH_SHORT).show();
		}

    	
    }
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )>=4 )
        {
	        if( connect_status_bit )
			  {
	        	mConnected = true;
				mBluetoothLeService.enable_JDY_ble(true,0);//IO������ܹ���
				 updateConnectionState(R.string.connected);
			  }else{
				  Toast toast = Toast.makeText(HomeActivity.this, "��ʾ�����豸��֧��", Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }
        
        
 
    }

    
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        
        setTitle( mDeviceName );
        sbValues = new StringBuffer();
		initViewPager();
		radioInit();
		
        Message message = new Message();  
        message.what = 1;  
        handler.sendMessage(message);  

        // ע������
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);  //��������
            Toast.makeText(HomeActivity.this,  "Connect request result=" + "���ӳɹ�", Toast.LENGTH_LONG).show();
        }        

        boolean sg;
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //getActionBar().setTitle( "="+BluetoothLeService );
        //mDataField.setText("="+sg );
        updateConnectionState(R.string.connecting);        
	//	
		timer1.schedule(task2, 100, 10000); // 3s��ִ��task,����3s�ٴ�ִ��

	}
	Timer timer1 = new Timer();
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
           if (msg.what == 1) {
            	if (mBluetoothLeService != null) {
                	if( mConnected==false )
                	{
                		updateConnectionState(R.string.connecting);
                		final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                   	 	Toast toast = Toast.makeText(HomeActivity.this, "Connect request result=" + result, Toast.LENGTH_SHORT); 
              		  toast.show();                 		               		
                	}
                }               
            }
            if (msg.what == 2) {//���Ͳ�ѯ״ָ̬��73 66 01-09 xx

				if (Constant.btn_stutus) {
					sendQuery(Constant.current_pressure);  //ѹ��
					delay(100);
					sendQuery(Constant.current_tempreture); //�¶�
					delay(100);
					sendQuery(Constant.run_stutus);//����״̬
				}
			}

            if (msg.what == 3) {
            	if (mConnected) {
            		sendQuery(Constant.charge_electricty);  //������ѯ
            	}
			}
         
            super.handleMessage(msg);
        }


    };
    
    /**
     * ��ѯָ�73  66 01-09 XX 
     * 01 ��ǰѹ��
		02 ��ǰ�¶�
		03 �豸����
		04 ����״̬
		05 ���õ�ѹ��
		06 ���õ�ǿ��
		07 ���õ�Ƶ��
     * @param i
     */
	private void sendQuery(int i) {
		// TODO Auto-generated method stub

		String senddataString = "";
		String by = "";
		switch (i) {
		case 0:  
			Constant.query_stutus = Constant.current_pressure;
			by = "01";	
			break;
		case 1:  
			Constant.query_stutus = Constant.current_tempreture;
			by = "02";	
			break;
		case 2:  
			Constant.query_stutus = Constant.charge_electricty;
			by = "03";	
			break;
		case 3:  
			Constant.query_stutus = Constant.run_stutus;
			by = "04";	
			break;
		case 4:  
			by = "05";	
			break;	
		case 5:  
			by = "06";	
			break;	
		case 6:  
			by = "07";	
			break;	
		case 7:  
			by = "08";	
			break;	
		case 8:  
			by = "09";	
			break;				
		default:
			break;
		}
		senddataString ="7366" + by + "00";
		mBluetoothLeService.txxx(senddataString);//�����ַ�������
	};
 //   Timer timer = new Timer();
    /**
     * ��ʱ��ѯ��������״̬
     */
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // ��Ҫ������:������Ϣ
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };    
     
    /**
     * ��ʱ��ѯ�豸����
     */
    TimerTask task2 = new TimerTask() {

        @Override
        public void run() {
            // ��Ҫ������:������Ϣ
            Message message = new Message();
            message.what = 3;
            handler.sendMessage(message);
        }
    };


    
    private void radioInit() {
		// TODO Auto-generated method stub
    	radioGroup =	(RadioGroup) findViewById(R.id.radioGroup);
    	home = (RadioButton) findViewById(R.id.home);
    	another = (RadioButton) findViewById(R.id.another);
    	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.home:
					//mBluetoothLeService.txxx("");//�����ַ�������
					mviewPager.setCurrentItem(0,false);
					break;
				case R.id.another:
					mviewPager.setCurrentItem(1,false);
					break;
				default:
					break;
				}
			}
		});
	}
    private void initViewPager() {
		// TODO Auto-generated method stub
		mviewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
		viewPagers = new ArrayList<View>();
		viewPager1 = View.inflate(HomeActivity.this,R.layout.base_pager,null);
		viewPager2 = View.inflate(HomeActivity.this,R.layout.second_pager,null);
		
		initPager1();
		initPager2();
		viewPagers.add(viewPager1);
		viewPagers.add(viewPager2);
		//mAdapter = new ContentAdapter(viewPagers);
		mviewPager.setAdapter(new ContentAdapter(viewPagers));
		//mviewPager.setCurrentItem(0);
		
		mviewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					mBluetoothLeService.txxx("73620100");//�����ַ�������
					home.setChecked(true);
					another.setChecked(false);
					break;

				case 1:
					mBluetoothLeService.txxx("73620200");//�����ַ�������					
					another.setChecked(true);
					home.setChecked(false);
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub	
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub		
			}
		});
	}

	private void initPager2() {
		// TODO Auto-generated method stub
		final TextView tv_show =  (TextView) viewPager2.findViewById(R.id.tv_show);
		final TextView tv_countTime =  (TextView) viewPager2.findViewById(R.id.tv_countTime);		
		final Spinner spinner_time_m = (Spinner)viewPager2.findViewById(R.id.spinner_time_m);
		final NumberPicker picker_frequency = (NumberPicker)viewPager2.findViewById(R.id.picker_frequency);
		final NumberPicker picker_density = (NumberPicker)viewPager2.findViewById(R.id.picker_density);
		final Button btn_click_m = (Button) viewPager2.findViewById(R.id.btn_click_m);
        ArrayList<String> data_list_m = new ArrayList<String>();
        data_list_m.add("10����");
        data_list_m.add("20����");
        data_list_m.add("30����");
        data_list_m.add("40����");
        data_list_m.add("50����");
        data_list_m.add("60����");		
        //������
        ArrayAdapter<String> arr_adapter_m= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list_m);
        //������ʽt
        arr_adapter_m.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //����������
        spinner_time_m.setAdapter(arr_adapter_m);
        //һ��Ҫ�������������Ȼ���Զ�����OnItemListener�������
        spinner_time_m.setSelection(0,true);     
        
        final String[] data_fre = {"��","��","��"};
        picker_frequency.setDisplayedValues(data_fre);
        picker_frequency.setMaxValue(data_fre.length - 1);
        picker_frequency.setValue(1);
        
        final String[] data_density = {"��","��","��"};
        picker_density.setDisplayedValues(data_density);
        picker_density.setMaxValue(data_density.length - 1);
        picker_density.setValue(1);  
        btn_click_m.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String picker_fre_value =  data_fre[picker_frequency.getValue()];
				String picker_density_value =  data_density[picker_density.getValue()];				
				tv_show.setText("��ǰƵ�ʣ�"+picker_fre_value + ",��ǰǿ�ȣ�"+picker_density_value);
				
				cmTimer = new CountDownTimer(time_lenth * 60 * 1000, 1000) {		
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						tv_countTime.setText("��ʣ"+millisUntilFinished/1000/60 +"����"+ millisUntilFinished/1000%60 +"��");
						 isCounting = true;
						 spinner_time_m.setEnabled(false);
						 picker_frequency.setEnabled(false);
						 picker_density.setEnabled(false);
						 btn_click_m.setEnabled(false);
					}
					
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						tv_show.setText("�ι޽���");
						isCounting = false;
						spinner_time_m.setEnabled(true);
						picker_frequency.setEnabled(true);
						picker_density.setEnabled(true);
			 	        btn_click_m.setEnabled(true);
					}
				};
				cmTimer.start();		
			}
		});
	}


	private void initPager1() {
		tx_show = (EditText) viewPager1.findViewById(R.id.tv_show);
		final TextView tv_countTime =  (TextView) viewPager1.findViewById(R.id.tv_countTime);
		final Spinner spinner_time_b = (Spinner)viewPager1.findViewById(R.id.spinner_time);
		final NumberPicker picker_pressure = (NumberPicker)viewPager1.findViewById(R.id.picker_pressure);
		final Button btn_click_b = (Button) viewPager1.findViewById(R.id.btn_click_b);
        final ArrayList<String> data_list = new ArrayList<String>();
        data_list.add("10����");
        data_list.add("20����");
        data_list.add("30����");
        data_list.add("40����");
        data_list.add("50����");
        data_list.add("60����");
        //������
        ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //������ʽ
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //����������
        spinner_time_b.setAdapter(arr_adapter);
        //һ��Ҫ�������������Ȼ���Զ�����OnItemListener�������
        spinner_time_b.setSelection(0, true);

        spinner_time_b.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					switch (position) {
					case 0:
						time_lenth = 10;
						break;
					case 1:
						time_lenth = 20;
						break;	
					case 2:
						time_lenth = 30;
						break;
					case 3:
						time_lenth = 40;
						break;	
					case 4:
						time_lenth = 50;
						break;
					case 5:
						time_lenth = 60;
						break;	
					default:
						break;
					}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {	// TODO Auto-generated method stub
				
			}
		});
        
        final String[] data = {"10%","20%","30%","40%","50%","60%","70%","80%","90%"};
        picker_pressure.setDisplayedValues(data);
        picker_pressure.setMaxValue(data.length - 1);
        picker_pressure.setValue(7);
        picker_pressure.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				//�ι�ѹ��73 63 01-08 00
				newVal = newVal+ 1;
				mBluetoothLeService.txxx("73630" + newVal + "00" );
			}
		});
        
        btn_click_b.setOnClickListener(new View.OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				
				Constant.btn_stutus = !(Constant.btn_stutus); 
				//����ʱ����
				cmTimer = new CountDownTimer(time_lenth * 60 * 1000, 1000) {		
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						tv_countTime.setText("��ʣ"+millisUntilFinished/1000/60 +"����"+ millisUntilFinished/1000%60 +"��");
						 isCounting = true;
					}
					
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						//tv_show.setText("��Ħʱ����ֹ");
						isCounting = false;
						spinner_time_b.setEnabled(true);
			 	        picker_pressure.setEnabled(true);
						another.setEnabled(true);						
					}
				};				
				if (mConnected) {   //�ж������Ƿ�����
					if (Constant.btn_stutus) {
						mBluetoothLeService.txxx("73620100");//���͹���ģʽ
						spinner_time_b.setEnabled(false);
			 	        picker_pressure.setEnabled(false);
						another.setEnabled(false);
					//	mBluetoothLeService.txxx("73610100");//���Ϳ�ʼָ��
						cmTimer.start();  //����ʱ��ʼ
						btn_click_b.setText("ֹͣ");
						startTimer();   
					} else {
						spinner_time_b.setEnabled(true);
			 	        picker_pressure.setEnabled(true);
						another.setEnabled(true);	
						//mBluetoothLeService.txxx("73610200");//����ָֹͣ��
						btn_click_b.setText("��ʼ");
						tv_countTime.setText("");
						if (cmTimer != null) {
							cmTimer.cancel();
							cmTimer = null;
						}

				        if (timer != null) {  
				        	timer.cancel();  
				        	timer = null;  
				        }  
				        if (task1 != null) {  
				        	task1.cancel();  
				        	task1 = null;  
				        }  
					}	
	 
				} else {
					
					Toast.makeText(HomeActivity.this, "�������������Ƿ�����", Toast.LENGTH_LONG).show();
				}			

			}
		
		});
      
		
	}
	/**
	 * ��ѯ״̬�Ŀ�ʼ����
	 */
 private void startTimer() {
		if (timer == null) {
			timer = new Timer();
		}
	if (task1 == null) {  
	   task1 = new TimerTask() {

	        @Override
	        public void run() {
	            // ��Ҫ������:������Ϣ
	            Message message = new Message();
	            message.what = 2;
	            handler.sendMessage(message);
	        }
	    };
	    if (timer!= null  && task1 != null) {
	    	timer.schedule(task1, 1000, 8000); // 3s��ִ��task,����3s�ٴ�ִ��,��ʼ�����󣬲�ѯѹ�����¶Ⱥ�����״̬	
		}
	}
 }
	/**
	 * ����ҳ���л�������
	 * @author hp
	 */
	class ContentAdapter extends PagerAdapter{
    	
        public List<View> mListViews;
         
        public ContentAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((View)object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position));
		
			return mListViews.get(position);
		}

	};

    
    
    
    protected void onResume() {
        super.onResume();
    	
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        cmTimer.cancel();
        cmTimer = null;
        timer.cancel();
        timer=null;
        timer1.cancel();
        timer1 = null;
        task.cancel();
        task = null;
        task1.cancel();
        task1 = null;
        task2.cancel();
        task2 = null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.openblueth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_conn:
//				Intent intent = new Intent(HomeActivity.this,BloothActivity.class);
//				startActivity(intent);
                mBluetoothLeService.connect(mDeviceAddress);
                break;

        }
        return true;
    }    
    
    
    
    
}
