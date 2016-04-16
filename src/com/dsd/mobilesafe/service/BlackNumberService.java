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
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class BlackNumberService extends Service {

	private InnerSmsRecevier mInnerSmsRecevier;
	private InnerOutCallReceiver mInnerOutCallReceiver;
	private MyPhoneStateListener myPhoneStateListener;
	private TelephonyManager mTM;
	private BlackNumberDao mDao;
	private MyContentObserver myContentObserver;

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
		mInnerSmsRecevier = new InnerSmsRecevier();
		registerReceiver(mInnerSmsRecevier, intentFilter);

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
		//注销广播接受者
		if(mInnerSmsRecevier != null)
		{
			unregisterReceiver(mInnerSmsRecevier);	
		}
		
		//注销内容观察者
		if(myContentObserver != null)
		{
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		
		//取消对电话状态的监听
		if(myPhoneStateListener != null)
		{
			mTM.listen(myPhoneStateListener,PhoneStateListener.LISTEN_NONE);
		}
		

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
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			myContentObserver = new MyContentObserver(new Handler(),incomingNumber);
			//通过内容解析器，去注册内容观察者，通话内容观察者，观察数据库（Uri决定那张表的那个库）的变化
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), 
					true, myContentObserver);

		}
	}
	
	class MyContentObserver extends ContentObserver{

		private String phone;

		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			
			this.phone = incomingNumber;
			// TODO Auto-generated constructor stub
		}
		
		//☆☆☆☆☆最重要的方法，数据库中指定calls表发生的时候会调用方法
		@Override
		public void onChange(boolean selfChange) {
			//发现数据有变化时，删除记录
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
			
			super.onChange(selfChange);
		}
		
	}
}
