package com.massage.util;


/**
 * 应用场景：用在一些标志性的数据，用户名和密码，
 * 使用分为三部:
 * 创建对象
 * 使用editor添加数据
 * 提交数据
 */
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoUtil {
	
	//保存用户名密码
	public static boolean saveUserInfo_android(Context context,String username, String password) {

		try{
			
			
			//1.通过Context对象创建一个SharedPreference对象
			//name:sharedpreference文件的名称    mode:文件的操作模式
//			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			
			SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
			//2.通过sharedPreferences对象获取一个Editor对象
			Editor editor = sharedPreferences.edit();
			//3.往Editor中添加数据
			editor.putString("username", username);
			editor.putString("password", password);
			//4.提交Editor对象
			editor.commit();

			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	//获取用户名密码
	public static Map<String ,String> getUserInfo_android(Context context){
		
		try{

			//1.通过Context对象创建一个SharedPreference对象
			SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
			
//			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			//2.通过sharedPreference获取存放的数据
			//key:存放数据时的key   defValue: 默认值,根据业务需求来写
			String username = sharedPreferences.getString("username", "admin");
			String password = sharedPreferences.getString("password", "admin");
			
			
			HashMap<String, String> hashMap = new HashMap<String ,String>();
			hashMap.put("username",username);
			hashMap.put("password", password);
			return hashMap;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	

}
