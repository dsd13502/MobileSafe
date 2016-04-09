package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.engine.AddressDao;

public class QueryAddressActivity extends Activity {

	protected static final String tag = "QueryAddressActivity";
	private EditText et_phone;
	private Button btn_query;
	private TextView tv_query_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);

		initUI();
	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);

		btn_query.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 String phone = et_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					queryData(phone);
				} else {
					Animation animation = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					et_phone.setAnimation(animation);

					//手机震动效果（vibrator震动）
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//添加振动时间,单位为毫秒
					//vibrator.vibrate(1000);
					//振动500毫秒，休息200ms,振动500毫秒，第二个参数表示重复次数，-1代表不重复
					vibrator.vibrate(new long[]{500,500,500,500,500,500}, -1);
				}

			}
		});

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			// 文件改变之后就查询
			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone.getText().toString().trim();
				Log.i(tag, "phone"+phone);
				queryData(phone);
				
				Log.i(tag, "查了数据库");
			}
		});

	}

	/**
	 * 查询归属地
	 */
	private void queryData(String phoneNumber) {
		final String phone = phoneNumber;
		new Thread() {
			public void run() {

				final String adress = AddressDao.getAdress(phone);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i(tag, adress);
						tv_query_result.setText(adress);
					}

				});
			}
		}.start();
	}
}
