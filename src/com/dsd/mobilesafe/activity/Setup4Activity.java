package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {

		cb_box = (CheckBox) findViewById(R.id.cb_box);

		// [1]获取数据
		boolean open_security = SpUtils.getBoolean(this,
				ConstantValue.OPEN_SECURITY, false);
		// [2]数据回显
		if (open_security) {
			cb_box.setText("安全设置以开启");
			cb_box.setChecked(true);
		} else {
			cb_box.setText("安全设置以关闭");
			cb_box.setChecked(false);
		}

		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			// isChecked 就代表着点击后的状态的了
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 【3】点击过程中，checkbox状态的切换，
				cb_box.setChecked(isChecked);
				// [4]以及切换后状态的存储
				SpUtils.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);

				// [5]根据状态去修改状态。
				if (isChecked) {
					cb_box.setText("安全设置以开启");
				} else {
					cb_box.setText("安全设置以关闭");
				}

			}
		});
	}

	@Override
	public void showPerPage() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		// 开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
		finish();

	}

	@Override
	public void showNextpage() {
		if (cb_box.isChecked()) {
			Intent intent = new Intent(this, SetupOverActivity.class);
			startActivity(intent);

			finish();
			SpUtils.putBoolean(getApplicationContext(),
					ConstantValue.SETUP_OVER, true);
			// 开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请勾选开启", 0).show();
		}

	}
}
