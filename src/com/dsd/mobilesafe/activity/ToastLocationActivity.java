package com.dsd.mobilesafe.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.id;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

public class ToastLocationActivity extends Activity {

	private ImageView iv_drag;
	private Button bt_top;
	private Button bt_bottom;
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	//指定点击次数
	private long mHits[] = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);

		initUI();
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		// 可拖拽双击居中的图片控件
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);

		//读取坐标
		int location_x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		int location_y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
		
		//imageView（iv_drag）在相对布局中，所以其所在位置的规则需要有相对布局提供
		android.widget.RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//设置iv_drag的左上角的坐标
		layoutParams.leftMargin = location_x;
		layoutParams.topMargin = location_y;
		iv_drag.setLayoutParams(layoutParams);
		
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		
		//设置双击事件
		iv_drag.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 使用google大神的做法
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1]  = SystemClock.uptimeMillis();
				if(mHits[mHits.length - 1] - mHits[0] < 500)
				{
					//获取屏幕中心点
					int left = mScreenWidth/2 - iv_drag.getWidth()/2;
					int top = mScreenHeight/2 - iv_drag.getHeight()/2;
					int right = mScreenWidth/2 + iv_drag.getWidth()/2;
					int bottom = mScreenHeight/2 + iv_drag.getHeight()/2;
					
					//设置iv_drag的显示位置
					iv_drag.layout(left, top, right, bottom);
					
					//存储当前位置
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
				
			}
		});
		
		// 监听某一个控件的拖拽过程（按下（1） 移动（多个） 抬起（一个）
		iv_drag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 获取动作事件
				switch (event.getAction()) {
				// 抬起
				case MotionEvent.ACTION_UP:
					//4.存储当前位置
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				case MotionEvent.ACTION_MOVE:

					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					
					int disX = endX - startX;
					int disY = endY - startY;
					
					//[1]获取当前控件所在屏幕的(四个方向)的位置
					int left = iv_drag.getLeft() + disX;
					int right = iv_drag.getRight() + disX;
					int top = iv_drag.getTop() + disY;
					int bottom = iv_drag.getBottom() + disY;
					
					//容错处理
					//左编译不能超出屏幕
					if(left < 0)
					{
						return true;
					}
					//右边缘不能超出屏幕
					if(right > mScreenWidth)
					{
						return true;
					}
					//上边缘不能超出屏幕的可见范围
					if(top < 0)
					{
						return true;
					}
					//下边缘不能超出屏幕的,但是
					if(bottom > mScreenHeight - 200)
					{
						return true;
					}
					
					if(top > mScreenHeight /2)
					{
						bt_bottom.setVisibility(View.INVISIBLE);
						bt_top.setVisibility(View.VISIBLE);
					}
					else
					{
						bt_bottom.setVisibility(View.VISIBLE);
						bt_top.setVisibility(View.INVISIBLE);
					}
					//[2]告知移动的控件，按计算出来的坐标去展示
					iv_drag.layout((int)left, (int)top, (int)right, (int)bottom);
					
					
					//【3】重置坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
				
					break;
				case MotionEvent.ACTION_DOWN:
					startX = (int)event.getRawX();
					startY = (int)event.getRawY();
					break;
				}
				//一定要返回true，false是不响应事件的！！！
				//既要响应点击事件又要响应拖拽事件，则返回结果需要修改为 false
				return false;
			}
		});

	}
}
