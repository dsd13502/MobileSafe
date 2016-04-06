package com.dsd.mobilesafe.receiver;

import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String tag = "BootReceiver";

	public BootReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取开机广播，
		Log.i(tag, "接收到了开关机广播");
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String  sim_number= SpUtils.getString(context, ConstantValue.SIM_NUMBER, "");
		
		String simSerialNumber = tm.getSimSerialNumber();
		
		if(sim_number != simSerialNumber)
		{
			String safePhone = SpUtils.getString(context,ConstantValue.CONTACT_PHONE, "");
			
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safePhone, null, "sim change", null, null);
			
		}
		
		
	}

}
