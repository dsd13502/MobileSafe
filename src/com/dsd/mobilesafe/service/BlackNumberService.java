package com.dsd.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.dsd.mobilesafe.db.dao.BlackNumberDao;
import com.dsd.mobilesafe.service.AddressService.InnerOutCallReceiver;
import com.dsd.mobilesafe.service.AddressService.MyPhoneStateListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class BlackNumberService extends Service {

	private InnerSmsRecevier innerSmsRecevier;
	private InnerOutCallReceiver innerOutCallReceiver;
	private MyPhoneStateListener myPhoneStateListener;
	private TelephonyManager mTM;
	private BlackNumberDao mDao;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		mDao = BlackNumberDao.getInstance(this);
		// 拦截短信
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		innerSmsRecevier = new InnerSmsRecevier();
		registerReceiver(innerSmsRecevier, intentFilter);

		// 拦截电话状态
		// [1]获取电话管理者
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		// 监听电话，指明监听的事件
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(innerSmsRecevier);

	}

	private class InnerSmsRecevier extends BroadcastReceiver {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取短信内容，获取发送短信的电话号码，如果此电话在黑名单中，并且拦截模式也为1（短信）或3（所有）,拦截短信

			// 获取所有短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");

			// 遍历短信过程
			for (Object object : objects) {
				// 获取短信
				SmsMessage smsMessage = SmsMessage
						.createFromPdu((byte[]) object);

				// 获取基本信息
				String address = smsMessage.getOriginatingAddress();
				String messageBody = smsMessage.getMessageBody();

				
				int mode = mDao.getMode(address);

				if (mode == 1 || mode == 3) {
					// 拦截短信
					abortBroadcast();
				}

			}

		}

	}

	class MyPhoneStateListener extends PhoneStateListener {
		// 【3】手动重写，电话状态改变是的事件
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			// 电话空闲状态，没有任何活动
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			// 摘机状态，至少有一个活动，拨号，或者是通话
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			// 一但响铃我们就挂断电话
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);
				break;
			}
		}

	}

	private void endCall(String incomingNumber) {
		// 那么到
		// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		int mode = mDao.getMode(incomingNumber);

		if (mode == 2 || mode == 3) {
			// 拦截电话
			// ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,所以需要反射调用
			try {
				// 【1】获取serviceManager字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				// 【2】获取方法
				Method method = clazz.getMethod("getService", String.class);
				//[3]反射调用此方法
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//[4]调用获取aidl文件对象
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
