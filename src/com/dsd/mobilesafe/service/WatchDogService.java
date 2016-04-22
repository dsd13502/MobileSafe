package com.dsd.mobilesafe.service;

import java.util.List;

import com.dsd.mobilesafe.activity.EnterPsdActivityActivity;
import com.dsd.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

public class WatchDogService extends Service {

	protected boolean isWatch;
	private AppLockDao mDao;
	private InnerReceiver innerReceiver;
	private String sikPackageName;
	private List<String> mPackageNameList;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// 维护看门狗的死循环，让其时刻检测现在开启的应用，是否为程序锁中要去拦截的应用
		mDao = AppLockDao.getInstance(this);
		isWatch = true;
		watch();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SIK_APP");
		
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver,intentFilter);
		super.onCreate();
		
		getContentResolver().registerContentObserver(Uri.parse("content://appLock/change"), 
				true,new MyContentObserver(new Handler()));
	}

	private class MyContentObserver extends ContentObserver
	{

		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			if(mPackageNameList != null)
			{
				mPackageNameList.clear();
			}
			mPackageNameList = mDao.findAll();
			
		}
		
	}
	private class InnerReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			sikPackageName = intent.getStringExtra("SikPackageName");
		}
		
	}
	/**
	 * 看门旺
	 */
	private void watch() {

		new Thread()
		{
			

			@SuppressWarnings("deprecation")
			public void run() {
				super.run();
				
				while(isWatch)
				{
					//监视现在正在运行的应用，使用任务栈
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					//获取刚刚打开的那个任务栈(数据结构最后一个)
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo info = runningTasks.get(0);
					//获取任务栈中的第一个（栈顶）的Activity
					String packageName = info.topActivity.getPackageName();
					if(mPackageNameList != null)
					{
						mPackageNameList.clear();
					}
					
					mPackageNameList = mDao.findAll();
					
					if(mPackageNameList.contains(packageName))
					{
						if(!packageName.equals(sikPackageName))
						{
							Intent intent = new Intent(getApplicationContext(),EnterPsdActivityActivity.class);
							intent.putExtra("packageName", packageName);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
						
					//减少循环次数
					SystemClock.sleep(500);
					
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(innerReceiver);
		isWatch = false;
	}

}
