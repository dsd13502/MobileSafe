package com.dsd.mobilesafe.receiver;

import com.dsd.mobilesafe.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;

public class MyAppWidgetProvider extends AppWidgetProvider {
	private static final String tag = "MyAppWidgetProvider";

	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收广播
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context) {
		// 当第一次创建widget的时候用
		Log.i(tag, "onEnabled 创建第一个窗体小部件调用方法");
		super.onEnabled(context);
		// 开启服务
		context.startService(new Intent(context, UpdateWidgetService.class));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// 在第一次创建完成后，再次创建widget的时候调用
		Log.i(tag, "onUpdate 创建多一个窗体小部件调用方法");
		// 开启服务
		context.startService(new Intent(context, UpdateWidgetService.class));
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// 当窗体小部件宽高发生改变的时候调用方法,创建小部件的时候,也调用此方法
		Log.i(tag, "onAppWidgetOptionsChanged 创建多一个窗体小部件调用方法");
		
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// 删除一个widget的时候调用
		Log.i(tag, "onDeleted 删除一个窗体小部件调用方法");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// 当widget全部销毁时调用
		super.onDisabled(context);
		// 停止服务
		Log.i(tag, "onDisabled 删除最后一个窗体小部件调用方法");
		context.stopService(new Intent(context, UpdateWidgetService.class));
	}

}
