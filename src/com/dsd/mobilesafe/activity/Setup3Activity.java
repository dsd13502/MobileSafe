package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

	protected static final String tag = "Setup3Activity";
	private EditText et_phone_number = null;
	private Button bt_select_number = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();

	}

	/**
	 * 获取ContactListActivity中选中的联系人对话框。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 一定要加容错处理，
		if (data != null) {
			// 放回当前界面的时候，接受返回方法
			String phone = data.getStringExtra("phone");
			// 将特殊符号过来掉（中划线）
			phone = phone.replace("-", "").replace(" ", "").trim();
			// 存储安全号码
			SpUtils.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
			et_phone_number.setText(phone);
		}
	}

	private void initUI() {
		// 显示电话号码的输入框
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		// 点击选择联系人的对话框
		bt_select_number = (Button) findViewById(R.id.bt_select_number);
		// 获取安全号码
		String phone = SpUtils.getString(getApplicationContext(),
				ConstantValue.CONTACT_PHONE, "");
		if (phone != null) {
			et_phone_number.setText(phone);
		}
		bt_select_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Setup3Activity.this,
						ContactListActivity.class);

				// 以返回值的形式开启新的Activity
				startActivityForResult(intent, 0);

			}
		});
	}


	@Override
	public void showPerPage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}

	@Override
	public void showNextpage() {
		// 获取电话号码，不管是手动输入的，还是手动输入的
		String phone = et_phone_number.getText().toString().trim();

		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			finish();

			// 存储安全号码
			SpUtils.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
			// 开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请填写安全号码", 0).show();
		}

	}
}
