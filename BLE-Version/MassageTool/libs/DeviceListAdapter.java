/*     */ package com.example.jdy_ble;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.bluetooth.BluetoothAdapter;
/*     */ import android.bluetooth.BluetoothAdapter.LeScanCallback;
/*     */ import android.bluetooth.BluetoothDevice;
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.os.Handler;
/*     */ import android.os.Looper;
/*     */ import android.os.Message;
/*     */ import android.util.Log;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.BaseAdapter;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import com.example.jdy_type.JDY_type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ public class DeviceListAdapter extends Activity
/*     */ {
/*  31 */   int list_select_index = 0;
/*     */   private DeviceListAdapter1 list_cell_0;
/*     */   BluetoothAdapter apter;
/*     */   Context context;
/*  38 */   int scan_int = 0;
/*  39 */   int ip = 0;
/*     */ 
/*  41 */   public String ibeacon_UUID = "";
/*  42 */   public String ibeacon_MAJOR = "";
/*  43 */   public String ibeacon_MINOR = "";
/*     */   public byte sensor_temp;
/*     */   public byte sensor_humid;
/*     */   public byte sensor_batt;
/*     */   public byte[] sensor_VID;
/*     */   public JDY_type DEV_TYPE;
/*  54 */   Timer timer = new Timer();
/*  55 */   boolean stop_timer = true;
/*     */ 
/*  57 */   byte dev_VID = -120;
/*     */ 
/* 188 */   Handler handler = new Handler() {
/*     */     public void handleMessage(Message msg) {
/* 190 */       if ((msg.what == 1) && (DeviceListAdapter.this.stop_timer))
/*     */       {
/* 193 */         DeviceListAdapter.this.loop_list();
/*     */       }
/*     */ 
/* 196 */       super.handleMessage(msg);
/*     */     }
/* 188 */   };
/*     */ 
/* 200 */   TimerTask task = new TimerTask()
/*     */   {
/*     */     public void run()
/*     */     {
/* 205 */       Message message = new Message();
/* 206 */       message.what = 1;
/* 207 */       DeviceListAdapter.this.handler.sendMessage(message);
/*     */     }
/* 200 */   };
/*     */ 
/* 280 */   private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
/*     */   {
/*     */     public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
/*     */     {
/* 284 */       DeviceListAdapter.this.scan_int += 1;
/* 285 */       if (DeviceListAdapter.this.scan_int <= 1)
/*     */         return;
/* 287 */       DeviceListAdapter.this.scan_int = 0;
/* 288 */       if (Looper.myLooper() == Looper.getMainLooper())
/*     */       {
/* 290 */         JDY_type m_tyep = DeviceListAdapter.this.dv_type(scanRecord);
/* 291 */         if ((m_tyep == JDY_type.UNKW) || (m_tyep == null))
/*     */           return;
/* 293 */         DeviceListAdapter.this.list_cell_0.addDevice(device, scanRecord, Integer.valueOf(rssi), m_tyep);
/*     */ 
/* 295 */         DeviceListAdapter.this.list_cell_0.notifyDataSetChanged();
/*     */       }
/*     */       else
/*     */       {
/* 300 */         DeviceListAdapter.this.runOnUiThread(new Runnable(scanRecord, device, rssi)
/*     */         {
/*     */           public void run() {
/* 303 */             JDY_type m_tyep = DeviceListAdapter.this.dv_type(this.val$scanRecord);
/* 304 */             if ((m_tyep == JDY_type.UNKW) || (m_tyep == null))
/*     */               return;
/* 306 */             DeviceListAdapter.this.list_cell_0.addDevice(this.val$device, this.val$scanRecord, Integer.valueOf(this.val$rssi), m_tyep);
/*     */ 
/* 308 */             DeviceListAdapter.this.list_cell_0.notifyDataSetChanged();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 280 */   };
/*     */ 
/*     */   public JDY_type dv_type(byte[] p)
/*     */   {
/*  62 */     if (p.length != 62) return null;
/*     */ 
/*  81 */     byte m1 = (byte)(p[20] + 1 ^ 0x11);
/*  82 */     String str = String.format("%02x", new Object[] { Byte.valueOf(m1) });
/*     */ 
/*  85 */     byte m2 = (byte)(p[19] + 1 ^ 0x22);
/*  86 */     str = String.format("%02x", new Object[] { Byte.valueOf(m2) });
/*     */ 
/* 108 */     int ib1_major = 0;
/* 109 */     int ib1_minor = 0;
/* 110 */     if ((p[52] == -1) && 
/* 112 */       (p[53] == -1)) ib1_major = 1;
/*     */ 
/* 114 */     if ((p[54] == -1) && 
/* 116 */       (p[55] == -1)) ib1_minor = 1;
/*     */ 
/* 121 */     if ((p[5] == -32) && (p[6] == -1) && (p[11] == m1) && (p[12] == m2) && (this.dev_VID == p[13]))
/*     */     {
/* 123 */       byte[] WriteBytes = new byte[4];
/* 124 */       WriteBytes[0] = p[13];
/* 125 */       WriteBytes[1] = p[14];
/* 126 */       Log.d("out_1", "TC" + this.list_cell_0.bytesToHexString1(WriteBytes));
/*     */ 
/* 128 */       if (p[14] == -96) return JDY_type.JDY;
/* 129 */       if (p[14] == -91) return JDY_type.JDY_AMQ;
/* 130 */       if (p[14] == -79) return JDY_type.JDY_LED1;
/* 131 */       if (p[14] == -78) return JDY_type.JDY_LED2;
/* 132 */       if (p[14] == -60) return JDY_type.JDY_KG;
/*     */ 
/* 135 */       return JDY_type.JDY;
/*     */     }
/* 137 */     if ((p[44] == 16) && (p[45] == 22) && ((
/* 138 */       (ib1_major == 1) || (ib1_minor == 1))))
/*     */     {
/* 146 */       return JDY_type.sensor_temp;
/*     */     }
/* 148 */     if ((p[44] == 16) && (p[45] == 22))
/*     */     {
/* 156 */       if (p[57] == -32) return JDY_type.JDY_iBeacon;
/* 157 */       if (p[57] == -31) return JDY_type.sensor_temp;
/* 158 */       if (p[57] == -30) return JDY_type.sensor_humid;
/* 159 */       if (p[57] == -29) return JDY_type.sensor_temp_humid;
/* 160 */       if (p[57] == -28) return JDY_type.sensor_fanxiangji;
/* 161 */       if (p[57] == -27) return JDY_type.sensor_zhilanshuibiao;
/* 162 */       if (p[57] == -26) return JDY_type.sensor_dianyabiao;
/* 163 */       if (p[57] == -25) return JDY_type.sensor_dianliu;
/* 164 */       if (p[57] == -24) return JDY_type.sensor_zhonglian;
/* 165 */       if (p[57] == -23) return JDY_type.sensor_pm2_5;
/*     */ 
/* 167 */       return JDY_type.JDY_iBeacon;
/*     */     }
/*     */ 
/* 173 */     return JDY_type.UNKW;
/*     */   }
/*     */ 
/*     */   public DeviceListAdapter(BluetoothAdapter adapter, Context context1)
/*     */   {
/* 182 */     this.apter = adapter;
/* 183 */     this.context = context1;
/* 184 */     this.list_cell_0 = new DeviceListAdapter1();
/*     */ 
/* 186 */     this.timer.schedule(this.task, 1000L, 1000L);
/*     */   }
/*     */ 
/*     */   public DeviceListAdapter1 init_adapter()
/*     */   {
/* 218 */     return this.list_cell_0;
/*     */   }
/*     */ 
/*     */   public BluetoothDevice get_item_dev(int pos) {
/* 222 */     return (BluetoothDevice)this.list_cell_0.dev_ble.get(pos);
/*     */   }
/*     */ 
/*     */   public JDY_type get_item_type(int pos)
/*     */   {
/* 227 */     return (JDY_type)this.list_cell_0.dev_type.get(pos);
/*     */   }
/*     */ 
/*     */   public int get_count() {
/* 231 */     return this.list_cell_0.getCount();
/*     */   }
/*     */ 
/*     */   public String get_iBeacon_uuid(int pos)
/*     */   {
/* 237 */     return this.list_cell_0.get_ibeacon_uuid(pos);
/*     */   }
/*     */ 
/*     */   public String get_ibeacon_major(int pos) {
/* 241 */     return this.list_cell_0.get_ibeacon_major(pos);
/*     */   }
/*     */ 
/*     */   public String get_ibeacon_minor(int pos) {
/* 245 */     return this.list_cell_0.get_ibeacon_minor(pos);
/*     */   }
/*     */ 
/*     */   public String get_sensor_temp(int pos)
/*     */   {
/* 251 */     return this.list_cell_0.get_sensor_temp(pos);
/*     */   }
/*     */ 
/*     */   public String get_sensor_humid(int pos) {
/* 255 */     return this.list_cell_0.get_sensor_humid(pos);
/*     */   }
/*     */ 
/*     */   public String get_sensor_batt(int pos)
/*     */   {
/* 260 */     return this.list_cell_0.get_sensor_batt(pos);
/*     */   }
/*     */ 
/*     */   public byte get_vid(int pos)
/*     */   {
/* 265 */     return (byte)this.list_cell_0.get_vid(pos);
/*     */   }
/*     */ 
/*     */   public void set_vid(byte vid) {
/* 269 */     this.dev_VID = vid;
/*     */   }
/*     */ 
/*     */   public void loop_list()
/*     */   {
/* 275 */     this.list_cell_0.loop();
/*     */   }
/*     */ 
/*     */   public void stop_flash()
/*     */   {
/* 320 */     this.stop_timer = false;
/*     */   }
/*     */ 
/*     */   public void start_flash() {
/* 324 */     this.stop_timer = true;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 329 */     this.list_cell_0.clear();
/*     */   }
/*     */ 
/*     */   public void scan_jdy_ble(Boolean p) {
/* 333 */     if (p.booleanValue())
/*     */     {
/* 336 */       this.list_cell_0.notifyDataSetChanged();
/* 337 */       this.apter.startLeScan(this.mLeScanCallback);
/* 338 */       start_flash();
/*     */     }
/*     */     else
/*     */     {
/* 342 */       this.apter.stopLeScan(this.mLeScanCallback);
/* 343 */       stop_flash();
/*     */     }
/*     */   }
/*     */ 
/*     */   class DeviceListAdapter1 extends BaseAdapter
/*     */   {
/*     */     private List<BluetoothDevice> dev_ble;
/*     */     private List<JDY_type> dev_type;
/*     */     private List<byte[]> dev_scan_data;
/*     */     private List<Integer> dev_rssi;
/*     */     private List<Integer> remove;
/*     */     private DeviceListAdapter.ViewHolder viewHolder;
/* 359 */     int count = 0;
/* 360 */     int ip = 0;
/*     */ 
/*     */     public DeviceListAdapter1() {
/* 363 */       this.dev_ble = new ArrayList();
/* 364 */       this.dev_scan_data = new ArrayList();
/* 365 */       this.dev_rssi = new ArrayList();
/* 366 */       this.dev_type = new ArrayList();
/* 367 */       this.remove = new ArrayList();
/*     */     }
/*     */ 
/*     */     public void loop()
/*     */     {
/* 372 */       if ((this.remove == null) || (this.remove.size() <= 0) || (this.ip != 0)) {
/*     */         return;
/*     */       }
/* 375 */       if (this.count >= this.remove.size())
/*     */       {
/* 377 */         this.count = 0;
/*     */       }
/* 379 */       Integer it = (Integer)this.remove.get(this.count);
/* 380 */       if (it.intValue() >= 3)
/*     */       {
/* 382 */         this.dev_ble.remove(this.count);
/* 383 */         this.dev_scan_data.remove(this.count);
/* 384 */         this.dev_rssi.remove(this.count);
/* 385 */         this.dev_type.remove(this.count);
/* 386 */         this.remove.remove(this.count);
/* 387 */         notifyDataSetChanged();
/*     */       }
/*     */       else
/*     */       {
/* 391 */         it = Integer.valueOf(it.intValue() + 1);
/* 392 */         this.remove.add(this.count + 1, it);
/* 393 */         this.remove.remove(this.count);
/*     */       }
/* 395 */       this.count += 1;
/*     */     }
/*     */ 
/*     */     public void addDevice(BluetoothDevice device, byte[] scanRecord, Integer RSSI, JDY_type type)
/*     */     {
/* 401 */       this.ip = 1;
/* 402 */       if (!this.dev_ble.contains(device))
/*     */       {
/* 404 */         this.dev_ble.add(device);
/* 405 */         this.dev_scan_data.add(scanRecord);
/* 406 */         this.dev_rssi.add(RSSI);
/* 407 */         this.dev_type.add(type);
/* 408 */         Integer it = Integer.valueOf(0);
/* 409 */         this.remove.add(it);
/*     */       }
/*     */       else
/*     */       {
/* 413 */         for (int i = 0; i < this.dev_ble.size(); ++i)
/*     */         {
/* 415 */           String btAddress = ((BluetoothDevice)this.dev_ble.get(i)).getAddress();
/* 416 */           if (!btAddress.equals(device.getAddress())) {
/*     */             continue;
/*     */           }
/*     */ 
/* 420 */           this.dev_ble.add(i + 1, device);
/* 421 */           this.dev_ble.remove(i);
/*     */ 
/* 423 */           this.dev_scan_data.add(i + 1, scanRecord);
/* 424 */           this.dev_scan_data.remove(i);
/*     */ 
/* 426 */           this.dev_rssi.add(i + 1, RSSI);
/* 427 */           this.dev_rssi.remove(i);
/*     */ 
/* 429 */           this.dev_type.add(i + 1, type);
/* 430 */           this.dev_type.remove(i);
/*     */ 
/* 432 */           Integer it = Integer.valueOf(0);
/* 433 */           this.remove.add(i + 1, it);
/* 434 */           this.remove.remove(i);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 441 */       notifyDataSetChanged();
/* 442 */       this.ip = 0;
/*     */     }
/*     */     public void clear() {
/* 445 */       this.dev_ble.clear();
/* 446 */       this.dev_scan_data.clear();
/* 447 */       this.dev_rssi.clear();
/* 448 */       this.dev_type.clear();
/* 449 */       this.remove.clear();
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 454 */       return this.dev_ble.size();
/*     */     }
/*     */ 
/*     */     public BluetoothDevice getItem(int position)
/*     */     {
/* 459 */       return (BluetoothDevice)this.dev_ble.get(position);
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 464 */       return position;
/*     */     }
/*     */ 
/*     */     public View getView(int position, View convertView, ViewGroup parent)
/*     */     {
/* 474 */       if (position <= this.dev_ble.size())
/*     */       {
/* 476 */         JDY_type type_0 = (JDY_type)this.dev_type.get(position);
/* 477 */         if (type_0 == JDY_type.JDY)
/*     */         {
/* 482 */           convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903046, null);
/* 483 */           this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 484 */           this.viewHolder.tv_devName = ((TextView)convertView.findViewById(2131296310));
/* 485 */           this.viewHolder.tv_devAddress = ((TextView)convertView.findViewById(2131296267));
/* 486 */           this.viewHolder.device_rssi = ((TextView)convertView.findViewById(2131296311));
/* 487 */           this.viewHolder.scan_data = ((TextView)convertView.findViewById(2131296314));
/* 488 */           this.viewHolder.type0 = ((TextView)convertView.findViewById(2131296312));
/* 489 */           convertView.setTag(this.viewHolder);
/*     */ 
/* 494 */           DeviceListAdapter.this.list_select_index = 1;
/*     */ 
/* 498 */           BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/* 499 */           String devName = device.getName();
/* 500 */           devName = "Name:" + devName;
/* 501 */           if (this.viewHolder.tv_devName != null) {
/* 502 */             this.viewHolder.tv_devName.setText(devName);
/*     */           }
/* 504 */           String mac = device.getAddress();
/* 505 */           mac = "MAC:" + mac;
/* 506 */           if (this.viewHolder.tv_devAddress != null) {
/* 507 */             this.viewHolder.tv_devAddress.setText(mac);
/*     */           }
/* 509 */           String rssi_00 = this.dev_rssi.get(position);
/* 510 */           rssi_00 = "RSSI:-" + rssi_00;
/* 511 */           if (this.viewHolder.device_rssi != null) {
/* 512 */             this.viewHolder.device_rssi.setText(rssi_00);
/*     */           }
/* 514 */           String tp = null;
/* 515 */           tp = "Type:标准模式";
/* 516 */           if (this.viewHolder.type0 != null) {
/* 517 */             this.viewHolder.type0.setText(tp);
/*     */           }
/* 519 */           if (this.viewHolder.scan_data != null) {
/* 520 */             this.viewHolder.scan_data.setText("scanRecord:" + bytesToHexString1((byte[])this.dev_scan_data.get(position)));
/*     */           }
/*     */         }
/* 523 */         else if (type_0 == JDY_type.JDY_iBeacon)
/*     */         {
/* 528 */           convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903047, null);
/* 529 */           this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 530 */           this.viewHolder.ibeacon_name = ((TextView)convertView.findViewById(2131296316));
/* 531 */           this.viewHolder.ibeacon_mac = ((TextView)convertView.findViewById(2131296318));
/* 532 */           this.viewHolder.ibeacon_uuid = ((TextView)convertView.findViewById(2131296320));
/* 533 */           this.viewHolder.ibeacon_major = ((TextView)convertView.findViewById(2131296322));
/* 534 */           this.viewHolder.ibeacon_minor = ((TextView)convertView.findViewById(2131296324));
/* 535 */           this.viewHolder.ibeacon_rssi = ((TextView)convertView.findViewById(2131296326));
/* 536 */           this.viewHolder.type0 = ((TextView)convertView.findViewById(2131296312));
/* 537 */           convertView.setTag(this.viewHolder);
/*     */ 
/* 544 */           String tp = null;
/* 545 */           tp = "Type:iBeacon";
/* 546 */           this.viewHolder.type0.setText(tp);
/* 547 */           BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/*     */ 
/* 549 */           if (this.viewHolder.ibeacon_name != null)
/* 550 */             this.viewHolder.ibeacon_name.setText(device.getName());
/* 551 */           if (this.viewHolder.ibeacon_mac != null) {
/* 552 */             this.viewHolder.ibeacon_mac.setText(device.getAddress());
/*     */           }
/* 554 */           if (this.viewHolder.ibeacon_uuid != null) {
/* 555 */             this.viewHolder.ibeacon_uuid.setText(get_ibeacon_uuid(position));
/*     */           }
/* 557 */           if (this.viewHolder.ibeacon_major != null) {
/* 558 */             this.viewHolder.ibeacon_major.setText(get_ibeacon_major(position));
/*     */           }
/* 560 */           if (this.viewHolder.ibeacon_minor != null) {
/* 561 */             this.viewHolder.ibeacon_minor.setText(get_ibeacon_minor(position));
/*     */           }
/* 563 */           if (this.viewHolder.ibeacon_rssi != null) {
/* 564 */             this.viewHolder.ibeacon_rssi.setText("-" + this.dev_rssi.get(position));
/*     */           }
/* 566 */           DeviceListAdapter.this.list_select_index = 2;
/*     */         }
/* 568 */         else if (type_0 == JDY_type.sensor_temp)
/*     */         {
/* 575 */           convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(
/* 576 */             2130903050, null);
/* 577 */           this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 578 */           this.viewHolder.sensor_name = ((TextView)convertView.findViewById(2131296336));
/* 579 */           this.viewHolder.sensor_mac = ((TextView)convertView.findViewById(2131296337));
/* 580 */           this.viewHolder.sensor_rssi = ((TextView)convertView.findViewById(2131296338));
/* 581 */           this.viewHolder.sensor_type0 = ((TextView)convertView.findViewById(2131296339));
/* 582 */           this.viewHolder.sensor_temp = ((TextView)convertView.findViewById(2131296344));
/* 583 */           this.viewHolder.sensor_humid = ((TextView)convertView.findViewById(2131296346));
/* 584 */           this.viewHolder.sensor_batt = ((TextView)convertView.findViewById(2131296340));
/*     */ 
/* 587 */           convertView.setTag(this.viewHolder);
/*     */ 
/* 594 */           DeviceListAdapter.this.list_select_index = 2;
/*     */ 
/* 597 */           BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/* 598 */           String devName = device.getName();
/*     */ 
/* 600 */           if (this.viewHolder.sensor_name != null)
/*     */           {
/* 602 */             devName = "Name:" + devName;
/* 603 */             this.viewHolder.sensor_name.setText(devName);
/*     */           }
/* 605 */           if (this.viewHolder.sensor_mac != null)
/*     */           {
/* 607 */             String mac = device.getAddress();
/* 608 */             mac = "MAC:" + mac;
/* 609 */             this.viewHolder.sensor_mac.setText(mac);
/*     */           }
/* 611 */           if (this.viewHolder.sensor_rssi != null)
/*     */           {
/* 613 */             String rssi_00 = this.dev_rssi.get(position);
/* 614 */             rssi_00 = "RSSI:-" + rssi_00;
/* 615 */             this.viewHolder.sensor_rssi.setText(rssi_00);
/*     */           }
/* 617 */           String tp = null;
/* 618 */           tp = "Type:sensor";
/* 619 */           if (this.viewHolder.sensor_type0 != null) {
/* 620 */             this.viewHolder.sensor_type0.setText(tp);
/*     */           }
/* 622 */           if (this.viewHolder.sensor_temp != null)
/*     */           {
/* 624 */             this.viewHolder.sensor_temp.setText(get_sensor_temp(position));
/*     */           }
/* 626 */           if (this.viewHolder.sensor_humid != null)
/*     */           {
/* 628 */             this.viewHolder.sensor_humid.setText(get_sensor_humid(position) + "%");
/*     */           }
/* 630 */           if (this.viewHolder.sensor_batt != null)
/*     */           {
/* 632 */             this.viewHolder.sensor_batt.setText(get_sensor_batt(position));
/*     */           }
/*     */ 
/*     */         }
/* 636 */         else if (type_0 == JDY_type.JDY_LED1)
/*     */         {
/* 640 */           convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903048, null);
/* 641 */           this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 642 */           this.viewHolder.led_name = ((TextView)convertView.findViewById(2131296327));
/* 643 */           this.viewHolder.led_mac = ((TextView)convertView.findViewById(2131296328));
/* 644 */           this.viewHolder.led_rssi = ((TextView)convertView.findViewById(2131296329));
/* 645 */           this.viewHolder.led_type113 = ((TextView)convertView.findViewById(2131296331));
/*     */ 
/* 647 */           convertView.setTag(this.viewHolder);
/*     */ 
/* 650 */           BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/*     */ 
/* 652 */           if (this.viewHolder.led_name != null) {
/* 653 */             this.viewHolder.led_name.setText("Name:" + device.getName());
/*     */           }
/* 655 */           if (this.viewHolder.led_mac != null) {
/* 656 */             this.viewHolder.led_mac.setText("MAC:" + device.getAddress());
/*     */           }
/* 658 */           if (this.viewHolder.led_rssi != null)
/*     */           {
/* 660 */             String rssi_00 = this.dev_rssi.get(position);
/* 661 */             rssi_00 = "RSSI:-" + rssi_00;
/* 662 */             this.viewHolder.led_rssi.setText(rssi_00);
/*     */           }
/* 664 */           if (this.viewHolder.led_type113 != null)
/*     */           {
/* 666 */             String tp = "";
/* 667 */             tp = "Type:LED灯带";
/* 668 */             this.viewHolder.led_type113.setText(tp);
/*     */           }
/*     */         }
/* 671 */         else if (type_0 != JDY_type.JDY_LED2)
/*     */         {
/* 675 */           if (type_0 == JDY_type.JDY_AMQ)
/*     */           {
/* 679 */             convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903049, null);
/* 680 */             this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 681 */             this.viewHolder.massager_name = ((TextView)convertView.findViewById(2131296332));
/* 682 */             this.viewHolder.massager_mac = ((TextView)convertView.findViewById(2131296333));
/* 683 */             this.viewHolder.massager_rssi = ((TextView)convertView.findViewById(2131296334));
/* 684 */             this.viewHolder.massager_type113 = ((TextView)convertView.findViewById(2131296335));
/*     */ 
/* 686 */             convertView.setTag(this.viewHolder);
/*     */ 
/* 689 */             BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/*     */ 
/* 691 */             if (this.viewHolder.massager_name != null) {
/* 692 */               this.viewHolder.massager_name.setText("Name:" + device.getName());
/*     */             }
/* 694 */             if (this.viewHolder.massager_mac != null) {
/* 695 */               this.viewHolder.massager_mac.setText("MAC:" + device.getAddress());
/*     */             }
/* 697 */             if (this.viewHolder.massager_rssi != null)
/*     */             {
/* 699 */               String rssi_00 = this.dev_rssi.get(position);
/* 700 */               rssi_00 = "RSSI:-" + rssi_00;
/* 701 */               this.viewHolder.massager_rssi.setText(rssi_00);
/*     */             }
/* 703 */             if (this.viewHolder.massager_type113 != null)
/*     */             {
/* 705 */               String tp = "";
/* 706 */               tp = "Type:AV棒";
/* 707 */               this.viewHolder.massager_type113.setText(tp);
/*     */             }
/*     */           }
/* 710 */           else if (type_0 == JDY_type.JDY_KG)
/*     */           {
/* 714 */             convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903051, null);
/* 715 */             this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 716 */             this.viewHolder.switch_name = ((TextView)convertView.findViewById(2131296347));
/* 717 */             this.viewHolder.switch_mac = ((TextView)convertView.findViewById(2131296348));
/* 718 */             this.viewHolder.switch_rssi = ((TextView)convertView.findViewById(2131296349));
/* 719 */             this.viewHolder.switch_type113 = ((TextView)convertView.findViewById(2131296350));
/*     */ 
/* 721 */             convertView.setTag(this.viewHolder);
/*     */ 
/* 724 */             BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/*     */ 
/* 726 */             if (this.viewHolder.switch_name != null) {
/* 727 */               this.viewHolder.switch_name.setText(device.getName());
/*     */             }
/* 729 */             if (this.viewHolder.switch_mac != null) {
/* 730 */               this.viewHolder.switch_mac.setText(device.getAddress());
/*     */             }
/* 732 */             if (this.viewHolder.switch_rssi != null)
/*     */             {
/* 734 */               String rssi_00 = this.dev_rssi.get(position);
/* 735 */               rssi_00 = "RSSI:-" + rssi_00;
/* 736 */               this.viewHolder.switch_rssi.setText(rssi_00);
/*     */             }
/* 738 */             if (this.viewHolder.switch_type113 != null)
/*     */             {
/* 740 */               String tp = "";
/* 741 */               tp = "Type:开关控制器";
/* 742 */               this.viewHolder.switch_type113.setText(tp);
/*     */             }
/*     */ 
/*     */           }
/* 750 */           else if (type_0 == JDY_type.JDY_WMQ)
/*     */           {
/* 754 */             convertView = LayoutInflater.from(DeviceListAdapter.this.context).inflate(2130903051, null);
/* 755 */             this.viewHolder = new DeviceListAdapter.ViewHolder(DeviceListAdapter.this);
/* 756 */             this.viewHolder.switch_name = ((TextView)convertView.findViewById(2131296347));
/* 757 */             this.viewHolder.switch_mac = ((TextView)convertView.findViewById(2131296348));
/* 758 */             this.viewHolder.switch_rssi = ((TextView)convertView.findViewById(2131296349));
/* 759 */             this.viewHolder.switch_type113 = ((TextView)convertView.findViewById(2131296350));
/* 760 */             this.viewHolder.type_imageView2 = ((ImageView)convertView.findViewById(2131296351));
/* 761 */             convertView.setTag(this.viewHolder);
/*     */ 
/* 764 */             BluetoothDevice device = (BluetoothDevice)this.dev_ble.get(position);
/*     */ 
/* 766 */             if (this.viewHolder.switch_name != null) {
/* 767 */               this.viewHolder.switch_name.setText(device.getName());
/*     */             }
/* 769 */             if (this.viewHolder.switch_mac != null) {
/* 770 */               this.viewHolder.switch_mac.setText(device.getAddress());
/*     */             }
/* 772 */             if (this.viewHolder.switch_rssi != null)
/*     */             {
/* 774 */               String rssi_00 = this.dev_rssi.get(position);
/* 775 */               rssi_00 = "RSSI:-" + rssi_00;
/* 776 */               this.viewHolder.switch_rssi.setText(rssi_00);
/*     */             }
/*     */ 
/* 780 */             if (this.viewHolder.switch_type113 != null)
/*     */             {
/* 782 */               String tp = "";
/* 783 */               tp = "Type:开关控制器";
/* 784 */               this.viewHolder.switch_type113.setText(tp);
/*     */             }
/* 786 */             if (this.viewHolder.type_imageView2 == null)
/*     */               break label2535;
/* 788 */             this.viewHolder.type_imageView2.setImageDrawable(DeviceListAdapter.this.getResources().getDrawable(2130837532));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 799 */         label2535: return convertView;
/* 800 */       }return null;
/*     */     }
/*     */ 
/*     */     public String get_ibeacon_uuid(int pos)
/*     */     {
/* 807 */       String uuid = null;
/* 808 */       HashMap map = new HashMap();
/*     */ 
/* 810 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/* 811 */       if (byte1000.length < 32) return null;
/* 812 */       byte[] proximityUuidBytes = new byte[16];
/* 813 */       System.arraycopy(byte1000, 9, proximityUuidBytes, 0, 16);
/* 814 */       String Beacon_UUID = bytesToHexString(proximityUuidBytes);
/*     */ 
/* 816 */       String uuid_8 = Beacon_UUID.substring(0, 8);
/* 817 */       String uuid_4 = Beacon_UUID.substring(8, 12);
/* 818 */       String uuid_44 = Beacon_UUID.substring(12, 16);
/* 819 */       String uuid_444 = Beacon_UUID.substring(16, 20);
/* 820 */       String uuid_12 = Beacon_UUID.substring(20, 32);
/* 821 */       uuid = uuid_8 + "-" + uuid_4 + "-" + uuid_44 + "-" + uuid_444 + "-" + uuid_12;
/*     */ 
/* 823 */       return uuid;
/*     */     }
/*     */ 
/*     */     public String get_ibeacon_major(int pos) {
/* 827 */       String major = null;
/* 828 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/* 829 */       if (byte1000.length < 60) return null;
/* 830 */       byte[] result = new byte[4];
/* 831 */       result[0] = 0;
/* 832 */       result[1] = 0;
/* 833 */       result[2] = byte1000[25];
/* 834 */       result[3] = byte1000[26];
/* 835 */       int ii100 = byteArrayToInt1(result);
/* 836 */       major = String.valueOf(ii100);
/* 837 */       return major;
/*     */     }
/*     */ 
/*     */     public String get_ibeacon_minor(int pos) {
/* 841 */       String major = null;
/* 842 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/* 843 */       if (byte1000.length < 60) return null;
/* 844 */       byte[] result = new byte[4];
/* 845 */       result[0] = 0;
/* 846 */       result[1] = 0;
/* 847 */       result[2] = byte1000[27];
/* 848 */       result[3] = byte1000[28];
/* 849 */       int ii100 = byteArrayToInt1(result);
/* 850 */       major = String.valueOf(ii100);
/* 851 */       return major;
/*     */     }
/*     */ 
/*     */     public String get_sensor_temp(int pos)
/*     */     {
/* 856 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/*     */ 
/* 858 */       byte[] result = new byte[1];
/* 859 */       result[0] = byte1000[58];
/*     */ 
/* 863 */       return bytesToHexString(result);
/*     */     }
/*     */ 
/*     */     public String get_sensor_humid(int pos) {
/* 867 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/*     */ 
/* 869 */       byte[] result = new byte[1];
/* 870 */       result[0] = byte1000[59];
/*     */ 
/* 873 */       return bytesToHexString(result);
/*     */     }
/*     */ 
/*     */     public String get_sensor_batt(int pos)
/*     */     {
/* 878 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/* 879 */       byte[] result = new byte[1];
/* 880 */       result[0] = byte1000[60];
/* 881 */       return bytesToHexString(result);
/*     */     }
/*     */ 
/*     */     public int get_vid(int pos)
/*     */     {
/* 886 */       String vid = null;
/* 887 */       byte[] byte1000 = (byte[])this.dev_scan_data.get(pos);
/* 888 */       byte[] result = new byte[4];
/* 889 */       result[0] = 0;
/* 890 */       result[1] = 0;
/* 891 */       result[2] = 0;
/* 892 */       JDY_type tp = (JDY_type)this.dev_type.get(pos);
/* 893 */       if ((tp == JDY_type.JDY) || (tp == JDY_type.JDY_LED1) || (tp == JDY_type.JDY_LED2) || (tp == JDY_type.JDY_AMQ) || (tp == JDY_type.JDY_KG) || (tp == JDY_type.JDY_WMQ) || (tp == JDY_type.JDY_LOCK))
/*     */       {
/* 895 */         result[3] = byte1000[13];
/*     */       }
/*     */       else
/*     */       {
/* 899 */         result[3] = byte1000[56];
/*     */       }
/*     */ 
/* 902 */       int ii100 = byteArrayToInt1(result);
/*     */ 
/* 904 */       return ii100;
/*     */     }
/*     */ 
/*     */     public int byteArrayToInt1(byte[] bytes)
/*     */     {
/* 914 */       int value = 0;
/*     */ 
/* 916 */       for (int i = 0; i < 4; ++i) {
/* 917 */         int shift = (3 - i) * 8;
/* 918 */         value += ((bytes[i] & 0xFF) << shift);
/*     */       }
/* 920 */       return value;
/*     */     }
/*     */ 
/*     */     private String bytesToHexString(byte[] src) {
/* 924 */       StringBuilder stringBuilder = new StringBuilder(src.length);
/* 925 */       for (byte byteChar : src)
/* 926 */         stringBuilder.append(String.format("%02X", new Object[] { Byte.valueOf(byteChar) }));
/* 927 */       return stringBuilder.toString();
/*     */     }
/*     */ 
/*     */     private String bytesToHexString1(byte[] src) {
/* 931 */       StringBuilder stringBuilder = new StringBuilder(src.length);
/* 932 */       for (byte byteChar : src)
/* 933 */         stringBuilder.append(String.format(" %02X", new Object[] { Byte.valueOf(byteChar) }));
/* 934 */       return stringBuilder.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     TextView tv_devName;
/*     */     TextView tv_devAddress;
/*     */     TextView device_rssi;
/*     */     TextView type0;
/*     */     TextView scan_data;
/*     */     TextView ibeacon_name;
/*     */     TextView ibeacon_mac;
/*     */     TextView ibeacon_uuid;
/*     */     TextView ibeacon_major;
/*     */     TextView ibeacon_minor;
/*     */     TextView ibeacon_rssi;
/*     */     TextView sensor_name;
/*     */     TextView sensor_mac;
/*     */     TextView sensor_rssi;
/*     */     TextView sensor_type0;
/*     */     TextView sensor_temp;
/*     */     TextView sensor_humid;
/*     */     TextView sensor_batt;
/*     */     TextView switch_name;
/*     */     TextView switch_mac;
/*     */     TextView switch_rssi;
/*     */     TextView switch_type113;
/*     */     ImageView type_imageView2;
/*     */     TextView massager_name;
/*     */     TextView massager_mac;
/*     */     TextView massager_rssi;
/*     */     TextView massager_type113;
/*     */     TextView led_name;
/*     */     TextView led_mac;
/*     */     TextView led_rssi;
/*     */     TextView led_type113;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\3\Desktop\DeviceListAdapter\
 * Qualified Name:     com.example.jdy_ble.DeviceListAdapter
 * JD-Core Version:    0.5.4
 */