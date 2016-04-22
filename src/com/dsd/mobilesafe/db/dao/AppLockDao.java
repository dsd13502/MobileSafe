package com.dsd.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dsd.mobilesafe.db.AppLockOpenHelder;

/**
 * 操作applock的方法类
 * 
 * @author im_dsd
 * 
 */
public class AppLockDao {

	private static AppLockDao mDao = null;
	private SQLiteDatabase mDB;
	private Context context = null;
	private AppLockDao(Context context) {
		this.context = context;
		
		AppLockOpenHelder mAppLockOpenHelder = new AppLockOpenHelder(context);
		mDB = mAppLockOpenHelder.getWritableDatabase();
	}

	public static  AppLockDao getInstance(Context context) {
		if (mDao == null) {
			mDao = new AppLockDao(context);
		}
		return mDao;
	}

	public void insert(String packagename) {
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		mDB.insert("applock", "packagename = ?", values);
		
		context.getContentResolver().notifyChange(Uri.parse("content://appLock/change"), null);
	}

	public List<String> findAll() {
		Cursor query = mDB.query("applock", null, null, null, null, null, null);
		List<String> packageNameList = new ArrayList<String>();

		while (query.moveToNext()) {
			String packageName = query.getString(1);
			packageNameList.add(packageName);
		}
		return packageNameList;
	}
	
	public void delete(String packageName)
	{
		mDB.delete("applock", "packagename = ?", new String[]{packageName});
		context.getContentResolver().notifyChange(Uri.parse("content://appLock/change"), null);
	}

}
