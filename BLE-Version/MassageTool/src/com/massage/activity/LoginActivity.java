package com.massage.activity;

import java.util.Map;

import com.massage.util.UserInfoUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	/**
	 * �ؼ�����
	 */
	private EditText et_username = null;
	private EditText et_password = null;
	private CheckBox cb_rem = null;
	private Button bt_login = null;
	private Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext = this;
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		cb_rem = (CheckBox) findViewById(R.id.cb_rem);
		bt_login = (Button) findViewById(R.id.bt_login);
		//b.���ð�ť�ĵ���¼�
		bt_login.setOnClickListener(this);

		
		//f.�����û������� 
		Map<String, String> map = UserInfoUtil.getUserInfo_android(mContext);//��ȡ�û�������
		if(map != null){
			String username = map.get("username");
			String password = map.get("password");
			et_username.setText(username);//�����û���
			et_password.setText(password);
			cb_rem.setChecked(true);//���ø�ѡ��ѡ��״̬
		}
	}
	private void login(){

		//c.��onclick�����У���ȡ�û�������û���������Ƿ��ס����

			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			boolean isrem = cb_rem.isChecked();
		//d.�ж��û��������Ƿ�Ϊ�գ���Ϊ�������������ʡ�ԣ�Ĭ������ɹ���
			if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
				Toast.makeText(mContext, "�û������벻��Ϊ��", Toast.LENGTH_SHORT).show();
				return ;
			}
				
		//e.�ж��Ƿ��ס���룬�����ס�����û������뱣�汾�ء�???? 
			if(isrem){
				boolean result = UserInfoUtil.saveUserInfo_android(mContext,username,password);
				if(result){
					Toast.makeText(mContext, "�û������뱣��ɹ�", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(mContext, "�û������뱣��ʧ��", Toast.LENGTH_SHORT).show();	
				}
				
			}else{
				Toast.makeText(mContext, "���豣��", Toast.LENGTH_SHORT).show();
			}



	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_login:
			login();
		Intent	intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
			break;

		default:
			break;
		}
	}
}
