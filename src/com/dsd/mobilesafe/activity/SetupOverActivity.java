package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.R;

public class SetupOverActivity extends Activity {


	private TextView tv_safe_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
		
		if(setup_over)
		{
			//密码输入成功，并且四个导航界面设置完成 ----》 停留在设置导航的列表
			setContentView(R.layout.activity_setup_over);
			initUI();
			
		}
		else
		{
			//密码输入成功，并且四个导航界面设置没有完成  ---》 跳转到导航界面的第一个
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			
			//跳转后没有用了，就销毁
			finish();
		}
		
		
		
	}

	private void initUI() {
		tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
		String phone = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, null);
		tv_safe_number.setText(phone);
		
		TextView tv_safe_setup = (TextView) findViewById(R.id.tv_safe_setup);
		tv_safe_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),Setup1Activity.class));
				
			}
		});
		
	}
	
	
}
