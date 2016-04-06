package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.activity.Setup2Activity;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.view.SettingItemView;
import com.dsd.mobilesafe.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {

		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);

		// 【1】回显数据（读取已有的绑定状态用作显示，sp中是否保存了sim卡的序列号）
		String simNumber = SpUtils.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");
		// 【2】判定是否序列卡号为空，为空去取序列号，
		if (TextUtils.isEmpty(simNumber)) {
			siv_sim_bound.setCheck(false);
		} else {
			siv_sim_bound.setCheck(true);
		}

		// 【3】获取siv_sim_bound的原有状态，设置 siv_sim_bound 点击事件
		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isCheck = siv_sim_bound.isCheck();
				// 【4】将原有的数据取反，
				// 【5】设置当前条目
				siv_sim_bound.setCheck(!isCheck);
				if (!isCheck) {
					// 【6】存储序列卡号
					// 【6.1】获取sim序列号，TelephonyManager
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					// 【6.2】获取sim卡序列号
					String simSerialNumber = tm.getSimSerialNumber();
					// 【6.3】存储
					SpUtils.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, simSerialNumber);

				} else {
					// 【7】删除序列卡号，
					// 【7.1】在SpUtils中定义方法，
					SpUtils.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);
				}

			}
		});

		// 【8】记得加权限

	}

	@Override
	public void showPerPage() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		// 开启平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
		finish();

	}

	@Override
	public void showNextpage() {
		String simNumber = SpUtils.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");
		if (!TextUtils.isEmpty(simNumber)) {
			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			finish();
			// 开启平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请绑定sim卡", 0).show();
		}

	}
}
