package com.im_dsd.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * @author im_dsd
 * 
 */
public class SplashActivity extends Activity {

	private TextView tv_versionName;
	private PackageManager  pm;
	private PackageInfo packageInfo;

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
		tv_versionName.setText(getVersionName());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
