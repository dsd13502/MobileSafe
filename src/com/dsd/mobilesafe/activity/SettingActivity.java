package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.service.AddressService;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.ServiceUtil;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.view.SettingItemView;
import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SettingActivity extends Activity {

	private SettingItemView siv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seting);

		initUI();
		initUpdate();
		
		initAddress();

	}

	/**
	 * 是否显示电话归属地的方法
	 */
	private void initAddress() {
		
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		
		//判读归属的服务是否开启了
				boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.dsd.mobilesafe.service.AddressService");
				siv_address.setCheck(isRunning);
		
		siv_address.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//获取点击前的状态
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				
				if(!isCheck)
				{
					startService(new Intent(getApplicationContext(),AddressService.class));
					
				}
			}
		});
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		siv = (SettingItemView) findViewById(R.id.siv_update);
	}

	/**
	 * 初始化自动更新功能
	 */
	private void initUpdate() {

		//数据回显示
		boolean openUpdate = SpUtils.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, true);
		siv.setCheck(openUpdate);

		siv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isCheck = siv.isCheck();
				//将原有数据取反
				siv.setCheck(!isCheck);
				//将取反后的状态存储到是sp中
				SpUtils.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}
	
	

}
