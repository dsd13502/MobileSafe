package com.dsd.mobilesafe.db.daomain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	public String packageName;
	public String name;
	public Drawable drawable;
	public boolean isSystem;
	public boolean isSdApp;
	
	public AppInfo(String packageName, String name, Drawable drawable,
			boolean isSystem, boolean isSdApp) {
		super();
		this.packageName = packageName;
		this.name = name;
		this.drawable = drawable;
		this.isSystem = isSystem;
		this.isSdApp = isSdApp;
	}
	
	public AppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
