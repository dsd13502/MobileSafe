package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dsd.mobilesafe.R;

public class AToolActivity extends Activity {

	private Button btn_quey_phone_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a_tool);
		
		initPhoneAddress();
	}

	private void initPhoneAddress() {

		btn_quey_phone_address = (Button) findViewById(R.id.btn_quey_phone_address);
		btn_quey_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
				
			}
		});
	}
}
