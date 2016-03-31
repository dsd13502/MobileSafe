package com.im_dsd.mobilesafe;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.im_dsd.mobilesafe.utils.StreamUtil;

/**
 * @author im_dsd
 * 
 */

public class SplashActivity extends Activity {

	protected static final String TAG ="SplashActivity" ;
	private TextView tv_versionName;
	private PackageManager  pm;
	private PackageInfo packageInfo;
	private int mLocalversionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// [1]第一种方式去头
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		System.out.println("怎么了这是");
		setContentView(R.layout.activity_splash);

		//
		initUI();
		initDate();
		checkVersion();
	}

	/**
	 * 初始化UI方法
	 */
	public void initUI() {

		tv_versionName = (TextView) findViewById(R.id.tv_version_name);
	}

	/**
	 * 初始化数据
	 */
	public void initDate() {
		//[1]更新应用版本名称
		tv_versionName.setText("版本号："+getVersionName());
		//[2]检测（本地版本号和服务版本号比对）是否有更新，如果有更新，提示用户下载（member）
		mLocalversionCode = getVersionCode();//快捷键，上下一起改变量名称：shift + alt + r
		//[3]获取服务器版本号（客户端发送请求，服务器响应）
		/*
		 * 1. 从 url 返回200,请求成功，流的方式将数据读取下来
		 * 2.json中应该包含的信息
		 *   更新版本的版本名称
		 *   服务器上的版本号
		 *   新版本描述
		 *   下载新apk
		 */
	}

	/**
	 * 获取清单文件中的版本信息
	 */
	public String getVersionName() {
		// [1]获取包的管理者，packageManger()
		 pm = getPackageManager();
		// [2]从报的管理者对象中，获取指定包名的基本信息（版本名称，版本号），传递 0 代表获取基本信息
		 try {
			packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// [3]获取版本名称，
			 return packageInfo.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 获取版本号
	 * @return 返回版本好，异常时返回 0
	 */
	public int getVersionCode()
	{
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 检测版本号
	 */
	public void checkVersion()
	{
		//[1]开线程的方法1
		new Thread()
		{
	
			@Override
			public void run() {
				//发送请求 获取数据，参数则为请求json的链接地址
				try {
					//【1】封装url
					URL url = new URL("http://192.168.1.106:8080/update.json");
					//【2】开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					
					//【3】设置常见请求参数
					//【3.1】请求超时
					connection.setConnectTimeout(2000);
					//【3.2】读取超时
					connection.setReadTimeout(2000);
					//【3.3】设置请求方式 GET
					connection.setRequestMethod("GET");
				
					//【4】获取响应码，请求成功的响应码 
					if(connection.getResponseCode() == 200)
					{
						//【5】获取数据流
						InputStream inputStream = connection.getInputStream();
						
						new StreamUtil();
						//【6】将流封装成字符串返回回来。
						String string = StreamUtil.stream2String(inputStream);
						
						Log.i(TAG, string);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}.start();
		
		
		//开启线程，方法 2
		/*
		 new Thread(new Runnable() {
		
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}).start();
		 */
		
	}
}
