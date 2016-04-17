package com.dsd.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.db.daomain.ProcessInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

@SuppressLint("ServiceCast")
public class ProcessInfoProvider {

	/**
	 * 获取进程总数的方法
	 * 
	 * @param context
	 *            上下文
	 * @return 进程的总数
	 */
	public static int getProcessCount(Context context) {
		// 【1】获取系统中运行的线程的总数
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 【2】获取正在运行的线程集合
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		// 【3】返回正在运行的线程的数目
		return runningAppProcesses.size();
	}

	/**
	 * 全部内存的大小，
	 * 
	 * @param context
	 *            上下文
	 * @return 可用大小，单位为 byte
	 */
	@SuppressLint("NewApi")
	public static long getMemoryTotalSize(Context context) {
		// [1]获取ActivityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// [2]获取内存信息的实例
		MemoryInfo memoryInfo = new MemoryInfo();
		// [3]将获得内存信息存到memroyInfo中，
		am.getMemoryInfo(memoryInfo);
		// 【4】获取可用大小

		return memoryInfo.totalMem;
	}

	/**
	 * 可用内存的大小，
	 * 
	 * @param context
	 *            上下文
	 * @return 可用大小，单位为 byte
	 */
	public static long getMemoryAvailSize(Context context) {
		// [1]获取ActivityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// [2]获取内存信息的实例
		MemoryInfo memoryInfo = new MemoryInfo();
		// [3]将获得内存信息存到memroyInfo中，
		am.getMemoryInfo(memoryInfo);
		// 【4】获取可用大小

		return memoryInfo.availMem;
	}

	/**
	 * 获取进程信息
	 * 
	 * @param context
	 *            上下文
	 * @return 进程
	 */
	public static List<ProcessInfo> getProcessInfo(Context context) {
		List<ProcessInfo> processList = new ArrayList<ProcessInfo>();
		// 【1】获取进程
		// [1.1]获取activityManger
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// [1.2]从am中获取正在运行的进程列表
		List<RunningAppProcessInfo> runningProcesses = am
				.getRunningAppProcesses();
		// [1.3]遍历列表获取单个进程信息
		for (RunningAppProcessInfo process : runningProcesses) {
			// [1.4]获取的ProcessInfo 的javaBean
			ProcessInfo processInfo = new ProcessInfo();
			// [1.4.1]获取进程的信息，注意这里应用的包名就是 == 进程名称
			processInfo.setPackageName(process.processName);

			// [1.4.2]获取进程所占的内存大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { process.pid });
			int totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty();
			// 此时 totalPrivateDirty 的单位为kb，我们统一单位为 byte
			processInfo.setMemSize(totalPrivateDirty * 1024);

			// 【2】获取进程对应的应用的信息（图片，应用名称等）
			PackageManager pm = context.getPackageManager();
			// 这是获取指定包名的一条的应用信息
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						process.processName, 0);
				// 获取应用图标
				processInfo.setIcon(applicationInfo.loadIcon(pm));
				// 获取应用名称
				processInfo.setName(applicationInfo.loadLabel(pm).toString());
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.setSystem(true);

				} else {
					processInfo.setSystem(false);
				}
			} catch (NameNotFoundException e) {

				processInfo.setName(process.processName);
				processInfo.setIcon(context.getResources().getDrawable(
						R.drawable.ic_launcher));
				processInfo.setSystem(true);

				e.printStackTrace();
			}

			processList.add(processInfo);

		}

		return processList;

	}
	
	/**
	 * 杀死进程
	 * @param ctx 上下文
	 * @param processInfo 
	 */
	public static void killProcess(Context ctx,ProcessInfo processInfo)
	{
		//
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//
		am.killBackgroundProcesses(processInfo.getPackageName());
	}

	public static void killAllProcess(Context ctx)
	{
		//
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//
		for (RunningAppProcessInfo processInfo : runningAppProcesses) {
			if (processInfo.processName.equals(ctx.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(processInfo.processName);
		}
		
	}
}
