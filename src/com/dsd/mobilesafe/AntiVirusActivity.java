package com.dsd.mobilesafe;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	private TextView tv_status;
	private ImageView iv_scanning;
	private ProgressBar pb_anti_virus;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//清除动画
			iv_scanning.clearAnimation();	
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		
		initUI();
		
	}

	/**
	 * 初始化View控件
	 */
	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_status = (TextView) findViewById(R.id.tv_status);
		pb_anti_virus = (ProgressBar) findViewById(R.id.pb_anti_virus);
		
		
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
