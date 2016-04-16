package com.dsd.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dsd.mobilesafe.db.daomain.AppInfo;

/**
 * 获取安装在手机中的应用相关信息
 * 
 * @author im_dsd
 * 
 */
public class AppInfoProvider {
	private static List<AppInfo> appList = new ArrayList<AppInfo>();

	/**
	 * 获取每一个应用信息方法，返回list集合
	 * 
	 * @param context
	 *            上下文环境
	 * @return
	 */
	public static List<AppInfo> getAppInfoList(Context context) {

		// 【1】获取每一个应用的信息方法
		PackageManager pm = context.getPackageManager();

		// 【2】 ☆☆☆☆☆在当前手机中已经安装上的应用
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		// 【3】循环遍历
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo appInfo = new AppInfo();
			// 【4】循环遍历每一个应用相关信息所在的对象
			// 【5】获取包名
			String packageName = packageInfo.packageName;
			appInfo.packageName = packageName;

			// 【6】获取应用名称（通过包的信息，拿到应用信息-》拿到lable节点内容）
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			appInfo.name = name;

			// 【7】获取应用图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			appInfo.drawable = icon;

			// 【8】判读当前应用是否为系统应用，
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				appInfo.isSystem = true;
			} else {
				appInfo.isSystem = false;
			}
			// 【9】判读当前应用是否为sd卡中的存储的应用，
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				appInfo.isSdApp = true;
			} else {
				appInfo.isSdApp = false;
			}

			appList.add(appInfo);
		}

		return appList;
	}

}
