package com.dsd.mobilesafe;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dsd.mobilesafe.db.dao.AntiVriusDao;
import com.dsd.mobilesafe.db.daomain.VriusBean;
import com.dsd.mobilesafe.utils.Md5Util;

public class AntiVirusActivity extends Activity {

	protected static final int SCANNING = 100;
	protected static final int SCANNING_FINISH = -100;
	
	private TextView tv_status;
	private ImageView iv_scanning;
	private ProgressBar pb_anti_virus;
	private List<VriusBean> mVriusList;
	private LinearLayout ll_container;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TextView view = new TextView(getApplicationContext());
			
			switch (msg.what) {
			case SCANNING:
				VriusBean vriusBean = (VriusBean)msg.obj;
				tv_status.setText("正在扫描"+ vriusBean.getName());
				
				//判读是否为病毒
				if(vriusBean.isVrius())
				{
					//是病毒
					view.setTextColor(Color.RED);
					view.setText("发现病毒:"+vriusBean.getName()+":"+vriusBean.getDesc());
					
				}
				else
				{
					//不是病毒
					view.setTextColor(Color.BLACK);
					view.setText("扫描安全:"+vriusBean.getName());
				}
				
				ll_container.addView(view,0);
				break;
				
			case SCANNING_FINISH:
				//清除动画
				iv_scanning.clearAnimation();	
				tv_status.setText("扫描完成");
				
				break;

			}
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		
		initUI();
		initRotateAnimation();
		initData();
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		
		new Thread()
		{
			public void run() {
				super.run();
			
				
				//获取手机中安装了的，或者已经卸载的软件签名
				PackageManager pm =getPackageManager();
				//
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES + 
						PackageManager.GET_SIGNATURES);
				
				//设置进度条的最大值
				pb_anti_virus.setMax(installedPackages.size());
				//设置当前进度
				int progress = 0;
				//开始遍历
				for (PackageInfo packageInfo : installedPackages)
				{
					VriusBean virus = new VriusBean();
					Signature s = packageInfo.signatures[0];
					String name = (String) packageInfo.applicationInfo.loadLabel(pm);
					String md5 = Md5Util.encoder(s.toCharsString());
					//获取描述信息
					String decs= AntiVriusDao.isVrius(md5);
					if(decs != null)
					{
						//发现病毒
						virus.setVrius(true);	
					}
					else
					{
						virus.setVrius(false);		
					}
					virus.setDesc(decs);
					virus.setName(name);
					
					Message msg = Message.obtain();
					progress ++;
					//设置当前进度
					pb_anti_virus.setProgress(progress);
					msg.obj = virus;
					msg.what = SCANNING;
					mHandler.sendMessage(msg);
					SystemClock.sleep(50+new Random().nextInt(100));
				}
				//扫描完成，发送最后一个消息
				Message msg = Message.obtain();
				msg.what = SCANNING_FINISH;
				mHandler.sendMessage(msg);
				
			};
		}.start();
	}

	/**
	 * 初始化View控件
	 */
	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_status = (TextView) findViewById(R.id.tv_status);
		pb_anti_virus = (ProgressBar) findViewById(R.id.pb_anti_virus);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		
		
	}
	/**
	 * 圆形旋转动画
	 */
	private void initRotateAnimation()
	{
		RotateAnimation rotateAnimation = new RotateAnimation(
				0, 360,
				Animation.RELATIVE_TO_SELF,0.5f,
				Animation.RELATIVE_TO_SELF,0.5f);
		//两秒执行完成
		rotateAnimation.setDuration(2000);
		//这是重复次数，一直循环
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		
		iv_scanning.setAnimation(rotateAnimation);
		
	}
	
	
}
