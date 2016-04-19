package com.dsd.mobilesafe.receiver;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.engine.ProcessInfoProvider;
import com.dsd.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class KillBackgroundProcess extends BroadcastReceiver {

	private static final String tag = "KillBackgroundProcess";


	@Override
	public void onReceive(Context context, Intent intent) {
		
		ProcessInfoProvider.killAllProcess(context);
		Log.i(tag, "widget的一键清理生效了");
		
		AppWidgetManager aWM = AppWidgetManager.getInstance(context);
		
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.process_widget);
		// 设置正在运行的软件个数
		rv.setTextViewText(R.id.tv_process_count, "正在运行的软件："
				+ ProcessInfoProvider.getProcessCount(context.getApplicationContext())
				+ "个");
		// 设置可用内存大小
		long memoryAvailSize = ProcessInfoProvider
				.getMemoryAvailSize(context.getApplicationContext());
		String avialSpace = Formatter.formatFileSize(context.getApplicationContext(),
				memoryAvailSize);

		rv.setTextViewText(R.id.tv_process_memory, "可用内存：" + avialSpace);
		// 获取widget的字节码文件
		ComponentName provider = new ComponentName(context.getApplicationContext(),
				MyAppWidgetProvider.class);
		aWM.updateAppWidget(provider, rv);
	}

}
