package com.dsd.mobilesafe.db.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dsd.mobilesafe.db.daomain.VriusBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class AntiVriusDao {

	private static String path = "data/data/com.dsd.mobilesafe/files/antivirus.db";
	private static SQLiteDatabase mDB;

	/**
	 * 查询数据库，获取病毒软件签名的MD5码，以及病毒描述信息
	 * @param context
	 * @return 包含MD5码以及对应的描述
	 */
	public static List<VriusBean> findAll(Context context) {

		if (mDB == null) {
			mDB = SQLiteDatabase.openDatabase(path, null, 0);
		}

		Cursor cursor = mDB.query("datable", new String[] { "md5", "desc" },
				null, null, null, null, null);
		List<VriusBean> mList = new ArrayList<VriusBean>();
				
		while (cursor.moveToNext()) 
		{
			VriusBean info = new VriusBean();

			String md5 = cursor.getString(0);
			String desc = cursor.getString(1);
			
			info.setMd5(md5);
			info.setDesc(desc);
			
			mList.add(info);
		}

		cursor.close();
		return mList;
		
		
	}
	
	/**
	 * 判读是否是病毒，是病毒返描述
	 * @param md5String
	 * @return 是病毒返回描述，不是病毒返回null
	 */
	public static String isVrius(String md5String)
	{
		if (mDB == null) {
			mDB = SQLiteDatabase.openDatabase(path, null, 0);
		}

		Cursor cursor = mDB.query("datable", new String[] { "md5", "desc" },
				null, null, null, null, null);
		
		while (cursor.moveToNext()) 
		{
			if(cursor.getString(0).equals(md5String))
			{
				return cursor.getString(1);
			}
		
		}

		cursor.close();
		return null;
	}
}
