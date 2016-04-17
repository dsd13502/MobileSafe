package com.dsd.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	private static ActivityManager mAM = null;

	/**
	 * 判读服务时候开启
	 * @param ctx 上下文
	 * @param serviceName 判读是否正在运行
	 * @return false 默认没有运行
	 */
	public static boolean isRunning(Context ctx,String serviceName)
	{
		//【1】获取ActivityManager对象，可以区获取当前手机正在运行的所在服务
		mAM  = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//【2】获取手机正在运行的服务(多少个服务)
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(100);
		//【3】遍历获取的所有服务，拿到每一个服务的名称，和传递进来的类的名称作比对，如果一致，说明服务正在运行
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//【4】获取每一个正在运行服务的名称
			serviceName = serviceName.trim();
			if(serviceName.equals(runningServiceInfo.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
}
