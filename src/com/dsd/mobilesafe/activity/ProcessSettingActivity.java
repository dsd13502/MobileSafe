package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.id;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.receiver.LockScreenService;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {

	private CheckBox cb_system_process;
	private CheckBox cb_clear_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		initUI();
	}

	private void initUI() {

		cb_system_process = (CheckBox) findViewById(R.id.cb_system_process);
		cb_clear_close = (CheckBox) findViewById(R.id.cb_clear_close);

		boolean isChecked = SpUtils.getBoolean(getApplicationContext(),
				ConstantValue.SHOW_SYSTEM_PROCESS, false);
		cb_clear_close.setChecked(isChecked);
		if(isChecked)
		{
			cb_system_process.setText("显示系统进程");
		}
		else
		{
			cb_system_process.setText("不显示系统进程");
		}

		cb_system_process
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							cb_system_process.setText("显示系统进程");
						} else {
							cb_system_process.setText("不显示系统进程");
						}
						SpUtils.putBoolean(getApplicationContext(),
								ConstantValue.SHOW_SYSTEM_PROCESS, isChecked);

					}
				});

	}

	/**
	 * 锁屏杀死进程
	 */
	private void initLockScreenClear() {

		cb_clear_close
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_clear_close.setText("锁屏清理以开启");
							// 开启服务
							startService(new Intent(getApplicationContext(),
									LockScreenService.class));
						} else {
							cb_clear_close.setText("锁屏清理以关闭");
						}
					}
				});

	}
}
