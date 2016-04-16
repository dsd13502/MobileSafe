package com.dsd.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;



import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

/**
 * 短信备份
 * @author im_dsd
 *
 */
public class SmsBackUp {
	
	
	private static  int count;
	private static int index = 0;
	private static FileOutputStream fos;
	public static void backup(Context context,String path,CallBack callBack)
	{
		//[1]创建文件
		File file = new File(path);
		//[2]获取内容解析器，获取短信数据库中的数据
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(Uri.parse("content://sms/"), 
				new String[]{"address","date","type","body"}, 
				null, null, null);
		
		//设置进度条的全部
		count = cursor.getCount();
		if(callBack != null)
		{
			callBack.setMax(count);
		}
		
		
		//[3]获取文件的流
		try {
			fos = new FileOutputStream(file);
			//【4】序列化数据库中读取的数据，放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			newSerializer.setOutput(fos, "utf-8");
			//开始的文本
			newSerializer.startDocument("utf-8", true);
			
			//开始根节点
			newSerializer.startTag(null,"smss");
			while(cursor.moveToNext())
			{
				//address节点
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				//date节点
				newSerializer.startTag(null,"date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				//type节点
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				//body节点
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				index ++;
				if(callBack != null)
				{
					callBack.setProgress(index);		
				}
				
			}
			//结束根节点
			newSerializer.endTag(null, "smss");
			//结束的文本
			newSerializer.endDocument();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			SystemClock.sleep(3000);
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cursor.close();
		}
	}

	 public  interface CallBack
	 {
		 public void setMax(int max);
		 public void setProgress(int progress);
	 }
}


