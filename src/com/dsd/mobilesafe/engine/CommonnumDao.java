package com.dsd.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommonnumDao {

	private static String path;

	public  List<Group> getCommonNumber(Context ctx) {
		if (path == null) {
			path = ctx.getFilesDir().getAbsolutePath() + File.separator
					+ "commonnum.db";
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor cursor = db.query("classlist", new String[] { "name", "idx" },
				null, null, null, null, null, null);
		List<Group> groupList = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			Group group = new Group();
			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getChild(ctx,group.idx);
			groupList.add(group);
		}
		cursor.close();
		db.close();
		return groupList;

	}

	// 获取每一个组中孩子节点的数据
	public List<Child> getChild(Context ctx,String idx) {
		if (path == null) {
			path = ctx.getFilesDir().getAbsolutePath() + File.separator
					+ "commonnum.db";
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// Cursor cursor = db.query("table"+idx, new String[]{"name","idx"},
		// null, null, null,null,null, null);
		Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
		List<Child> childList = new ArrayList<Child>();
		while (cursor.moveToNext()) {
			Child child = new Child();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}

	public class Group {
		public String name;
		public String idx;
		public List<Child> childList;
	}

	public class Child {
		public String _id;
		public String number;
		public String name;
	}
}
