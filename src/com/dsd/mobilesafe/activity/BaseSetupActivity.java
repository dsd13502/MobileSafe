package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Setup的基类，用于实现手势响应，和上下页切换
 * 
 * @author im_dsd
 * 
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_setup);

		// 创建手势管理对象，用作管理onTouchEvent（event）传递过来的手势动作
		// SimpleOnGestureListener是内部类所以要 通过 GestureDetector 获取
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						// 监听手势的移动。
						if (e1.getX() - e2.getX() > 0) {// 说明是从左向右滑动，移动到下一页
							// 调用子类的下一页方法
							showNextpage();
						}
						if (e1.getX() - e2.getX() < 0) {// 说明是从右向左滑动，移动到上一页
							// 调用子类的上一页方法
							showPerPage();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	/*
	 * 1.监听屏幕上相应的事件的类型（按下（1次），移动（多次），抬起（1次））
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 通过手势处理类，接受多种类型的事件，用于处理
		gestureDetector.onTouchEvent(event);

		return super.onTouchEvent(event);
	}

	/**
	 * 抽象方法，定义跳转到下一页的抽象方法
	 */
	public abstract void showPerPage();

	/**
	 * 抽象方法，定义跳转到上一页的抽象方法
	 */
	public abstract void showNextpage();

	// 统一处理跳转指令
	private void nextPage(View view) {
		showNextpage();
	}

	public void perPage(View view) {
		showPerPage();
	}
}
