package com.dsd.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AddressService extends Service {

	public static final String tag = "AddressService";
	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//[1]获取电话管理者
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();

		//监听电话，指明监听的事件
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}

	@Override
	public void onDestroy() {
		// 取消对电话的监听
		if(mTM != null & myPhoneStateListener != null)
		{
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
	}
	
	class MyPhoneStateListener extends PhoneStateListener
	{
		//【3】手动重写，电话状态改变是的事件
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			//电话空闲状态，没有任何活动（移除吐司）
			case TelephonyManager.CALL_STATE_IDLE:	
				
				Log.i(tag, "电话空闲状态");
				break;
			//摘机状态，至少有一个活动，拨号，或者是通话
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				Log.i(tag, "摘记状态");
				break;
			//响铃（展示吐司）
			case TelephonyManager.CALL_STATE_RINGING:
				
				Log.i(tag, "响铃状态");
				showTaost(incomingNumber);
				break;
			}
		}

	
	}
	
	/**
	 * 显示来点号码吐司
	 * @param incomingNumber
	 */
	private void showTaost(String incomingNumber) {
		Toast.makeText(getApplicationContext(), incomingNumber, 0).show();
	}
}
