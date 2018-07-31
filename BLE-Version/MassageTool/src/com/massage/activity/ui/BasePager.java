package com.massage.activity.ui;

import java.nio.Buffer;

import com.massage.activity.R;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class BasePager {

	public TextView tv_show;
	public Activity mActivity;
	public NumberPicker picker_pressure;
	public Spinner spinner_time;
	public Button btn_click;
	public View mRootView;
	public BasePager(Activity activity) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
		mRootView = initView();
	}
	
	public View initView() {
		View view = View.inflate(mActivity, R.layout.base_pager, null);
		tv_show = (TextView) view.findViewById(R.id.tv_show);
		spinner_time = (Spinner) view.findViewById(R.id.spinner_time);
		picker_pressure = (NumberPicker) view.findViewById(R.id.picker_pressure);
		btn_click = (Button) view.findViewById(R.id.btn_click);
		return view;
		
	}
	// ��ʼ������
	public void initData() {
	}

}
