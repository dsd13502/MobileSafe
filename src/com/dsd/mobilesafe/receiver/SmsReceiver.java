package com.dsd.mobilesafe.receiver;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	public SmsReceiver() {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
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
		}
		
	}

}
