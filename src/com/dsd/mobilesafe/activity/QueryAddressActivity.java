package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.engine.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class QueryAddressActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
		
		AddressDao.getAdress("15500104280");
		
	}

	private void initUI() {
		// TODO Auto-generated method stub
		
	}
}
