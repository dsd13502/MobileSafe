package com.dsd.mobilesafe.activity;

import java.io.File;

import javax.security.auth.callback.Callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dsd.mobilesafe.AppLockActivity;
import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.engine.CommonnumDao;
import com.dsd.mobilesafe.engine.SmsBackUp;
import com.dsd.mobilesafe.engine.SmsBackUp.CallBack;

public class AToolActivity extends Activity {

	private Button btn_quey_phone_address, btn_backup_sms;
	private Button bt_commonnumber_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_tool);

		initPhoneAddress();
		initSmsBackUp();
		initCommonnumNumberQuery();
		initAppLock();
	}

	/**
	 * 初始化软件锁
	 */
	private void initAppLock() {
		Button bt_app_lock = (Button) findViewById(R.id.bt_app_lock);
		bt_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
			}
		});
	}

	/**
	 * 初始化常用号码查询
	 */
	private void initCommonnumNumberQuery() {
		bt_commonnumber_query = (Button)findViewById(R.id.bt_commonnumber_query);
		bt_commonnumber_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
			}
		});
	}

	private void initPhoneAddress() {

		btn_quey_phone_address = (Button) findViewById(R.id.btn_quey_phone_address);
		btn_quey_phone_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(),
						QueryAddressActivity.class));

			}
		});
	}

	private void initSmsBackUp() {

		btn_backup_sms = (Button) findViewById(R.id.btn_backup_sms);
		btn_backup_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showSmsBackUpDialog();
			}
		});
	}

	private void showSmsBackUpDialog() {
		// 创建一个带有进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		// 指定进度条样式
		progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);

		progressDialog.show();

		// 备份短信
		new Thread() {
			public void run() {
				String path = Environment.getExternalStorageDirectory()
						.getAbsoluteFile().toString()
						+ File.separator + "mySms.xml";

				SmsBackUp.backup(getApplicationContext(), path, new CallBack() {

					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);

					}

					@Override
					public void setProgress(int progress) {
						progressDialog.setProgress(progress);
					}

				});
				progressDialog.dismiss();
			};
		}.start();

	}
}
