package com.dsd.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.engine.ProcessInfoProvider;
import com.dsd.mobilesafe.receiver.MyAppWidgetProvider;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private String tag = "UpdateWidgetService";
	private AppWidgetManager aWM;
	private Timer mTimer;
	private InnerReciver mInnerReciver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		aWM = AppWidgetManager.getInstance(getApplicationContext());
		startTimer();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		
		mInnerReciver = new InnerReciver();
		
		registerReceiver(mInnerReciver, intentFilter);
		
		super.onCreate();
	}

	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// ui定时刷新
				startUpdateAppWidget();
				Log.i(tag, "5秒一次的定时任务现在正在运行..........");
			}
		}, 0, 5000);
	}

	// 跟新widget
	private  void startUpdateAppWidget() {

		// 获取布局(因为widget是系统其他线程中运行的，所以要获取现在的布局只能通过这个方法)
		RemoteViews rv = new RemoteViews(getPackageName(),
				R.layout.process_widget);
		// 设置正在运行的软件个数
		rv.setTextViewText(R.id.tv_process_count, "正在运行的软件："
				+ ProcessInfoProvider.getProcessCount(getApplicationContext())
				+ "个");
		// 设置可用内存大小
		long memoryAvailSize = ProcessInfoProvider
				.getMemoryAvailSize(getApplicationContext());
		String avialSpace = Formatter.formatFileSize(getApplicationContext(),
				memoryAvailSize);

		rv.setTextViewText(R.id.tv_process_memory, "可用内存：" + avialSpace);

		// 点击widget跳转到HomeActivity
		Intent homeIntent = new Intent("android.intent.action.HOME");
		homeIntent.addCategory("android.intent.category.DEFAULT");
		PendingIntent toHonmeIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, homeIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		rv.setOnClickPendingIntent(R.id.ll_root, toHonmeIntent);

		//设置 清理内存的点击事件
		Intent clearIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent callClearReceive = PendingIntent.getBroadcast(
				getApplicationContext(), 0, clearIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		
		rv.setOnClickPendingIntent(R.id.btn_clear, callClearReceive);

		// 获取widget的字节码文件
		ComponentName provider = new ComponentName(getApplicationContext(),
				MyAppWidgetProvider.class);

		aWM.updateAppWidget(provider, rv);
	}

	public void cancelTimerTask() {
		// mTimer中cancel方法取消定时任务方法
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cancelTimerTask();
		if(mInnerReciver != null)
		{
			unregisterReceiver(mInnerReciver);		
		}
	}
	
	private class InnerReciver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				//开启定时更新任务
				startTimer();
			}else{
				//关闭定时更新任务
				cancelTimerTask();
			}
		}
		
	}
}
