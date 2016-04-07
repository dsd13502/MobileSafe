package com.dsd.mobilesafe.receiver;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.service.LocationService;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDpm = null;

	public SmsReceiver() {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//建立 驱动安全管理者实例  DevicePolicyManager
		mDpm  = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		//[1]判读时候开启短信保护。默认没有开启。
		boolean open_security = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
		
		//【2】获取短信内容。
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		
		//[3]循环遍历短息过程
		for(Object object :objects)
		{
			//【4】获取短息对象
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])object);
			//【5】获取短息基本信息。
			String originatingAddress = smsMessage.getOriginatingAddress();
			String messageBody = smsMessage.getMessageBody();
			//[6]判读是否包含播放音乐的关键字
			if(messageBody.equals("#*alarm*#"))
			{
				//[7]播放音乐（准备音乐，MediaPlayer)
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
				//设置循环次数，无限循环
				//mediaPlayer.setLooping(true);
				//【8】开始播放音乐
				mediaPlayer.start();
			}
			else if (messageBody.equals("#*location*#"))
			{
				//[9]开启获取位置服务
				context.startService(new Intent(context,LocationService.class));
			}
			else if(messageBody.equals("#*wipedate*#"))
			{
				//TODO 没有实现设备管理器
				//if(mDpm.isAdminActive(admin))
				
				//清除手机数据
				//mDpm.wipeData(0);
				//清除sdcard数据
				//mDpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				Toast.makeText(context, "太危险，就算了吧", 0).show();
				
			}else if(messageBody.equals("#*lockscreen*#"))
			{
				//TODO 没有实现 设备管理器
			}
		}
		
	}

}
