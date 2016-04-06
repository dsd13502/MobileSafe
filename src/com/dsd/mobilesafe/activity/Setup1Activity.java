package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;

import com.dsd.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {

	public static final String TAG = "Setup1Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_1);
	}

	public void nextPage(View view) {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();

		// 开启平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

	@Override
	public void showPerPage() {

	}

	@Override
	public void showNextpage() {
		startActivity(new Intent(getApplicationContext(), Setup2Activity.class));
		finish();

	}

}
