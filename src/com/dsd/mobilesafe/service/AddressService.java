package com.dsd.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.engine.AddressDao;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

public class AddressService extends Service {

	public static final String tag = "AddressService";
	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private WindowManager mWM;
	private String mAddress;
	private TextView tv_toast;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			tv_toast.setText(mAddress);
		};
	};
	private int mScreenHeight;
	private int mScreenWidth;
	private InnerOutCallReceiver innerOutCallReceiver;
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 获取窗体对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		// [1]获取电话管理者
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();

		// 监听电话，指明监听的事件
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//☆☆☆☆☆监听拨打电话的广播接收者
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		innerOutCallReceiver = new InnerOutCallReceiver();
		//注册这个广播接收者
		registerReceiver(innerOutCallReceiver, intentFilter);
	}
	
	class InnerOutCallReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接受拨打电话的广播就显示归属的吐司
			
			String phone = getResultData();
			showTaost(phone);
			
		}
		
		
		
	}

	@Override
	public void onDestroy() {
		// 取消对电话的监听
		if (mTM != null & myPhoneStateListener != null) {
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
		
		if(innerOutCallReceiver != null)
		{
			unregisterReceiver(innerOutCallReceiver);			
		}
	}

	/**
	 * 显示来点号码吐司
	 * 
	 * @param incomingNumber
	 */
	private void showTaost(String incomingNumber) {
		// Toast.makeText(getApplicationContext(), incomingNumber, 0).show();

		// XXX This should be changed to use a Dialog, with a Theme.Toast
		// defined that sets up the layout params appropriately.
		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //默认是能够区触摸的
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		// 在响铃的时候显示吐司，和电话类型一致
		params.type = WindowManager.LayoutParams.TYPE_PHONE;// TYPE_TOAST;
		params.setTitle("Toast");

		// 指定吐司的所在位置,左上角
		params.gravity = Gravity.LEFT + Gravity.TOP;

		// 自定义一个吐司的布局，xml -》view(吐司)将吐司挂载到windowManager窗体上
		mViewToast = View.inflate(getApplicationContext(), R.layout.toast_view,
				null);

		//设置拖拽事件
		mViewToast.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 获取动作事件
				switch (event.getAction()) {
				// 抬起
				case MotionEvent.ACTION_UP:
					
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
					SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y,params.y);
					//4.存储当前位置
					break;
				case MotionEvent.ACTION_MOVE:

					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					
					//得到位移量
					int disX = endX - startX;
					int disY = endY - startY;
					
					params.x = (int) disX;
					params.y = (int) disY;
					
					if(params.x < 0)
					{
						params.x = 0;
					}
					
					if(params.y < 0)
					{
						params.y = 0;
					}
					
					if(params.x > mScreenWidth - mViewToast.getWidth())
					{
						params.x = mScreenWidth - mViewToast.getWidth();
					}
					
					if(params.y > mScreenHeight - mViewToast.getHeight() - 100)
					{
						params.y = mScreenHeight - mViewToast.getHeight() - 100;
					}
					//告知窗体吐司需要更新位置
					mWM.updateViewLayout(mViewToast, params);
					
					
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				}
				return true;
			}
		});
		tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
		
		//读取sp中存储吐司位置x，y坐标
		params.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		params.y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
		
		//从sp中获取色值文字的索引，匹配图片，用作展示
		int[] drawbleIds = new int[]{
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		
		int toastStyleIndex = SpUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		tv_toast.setBackgroundResource(drawbleIds[toastStyleIndex]);
		
		// 在窗体上挂载一个View(权限)
		mWM.addView(mViewToast, mParams);

		// 获取到来电号码，需要做来电号码查询

		query(incomingNumber);

	}

	private void query(final String incomingNumber) {
		new Thread() {
			public void run() {
				mAddress = AddressDao.getAdress(incomingNumber);
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	class MyPhoneStateListener extends PhoneStateListener {
		// 【3】手动重写，电话状态改变是的事件
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			// 电话空闲状态，没有任何活动（移除吐司）
			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && mViewToast != null) {
					mWM.removeView(mViewToast);
				}
				Log.i(tag, "电话空闲，电话挂断了");
				break;
			// 摘机状态，至少有一个活动，拨号，或者是通话
			case TelephonyManager.CALL_STATE_OFFHOOK:

				Log.i(tag, "摘记状态");
				break;
			// 响铃（展示吐司）
			case TelephonyManager.CALL_STATE_RINGING:

				Log.i(tag, "响铃状态");
				showTaost(incomingNumber);
				break;
			}
		}

	}
}
