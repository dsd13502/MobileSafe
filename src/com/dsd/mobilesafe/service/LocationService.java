package com.dsd.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;

public class LocationService extends Service {

	public LocationService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// 以启动服务我们就去获取手机经纬度坐标
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// 获取手机的经纬度
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// [2]以最优的方式后去经纬度坐标
		Criteria criteria = new Criteria();
		// [2.1]允许花流量
		criteria.setCostAllowed(true);
		// [2.2]指定获取经纬度的精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// [2.3]获取最优的获取经纬度做表方式
		locationManager.getBestProvider(criteria, true);

		// [3]在一定时间间隔，移动一定距离后获取经纬度坐标
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new MyLocationLister());
	}

	class MyLocationLister implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// gps状态发生切换的事件监听

		}

		@Override
		public void onProviderEnabled(String provider) {
			// 开启的时候事件监听

		}

		@Override
		public void onProviderDisabled(String provider) {
			// 关闭的时候事件监听

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onLocationChanged(Location location) {
			// 经度
			double longitude = location.getLongitude();
			// 纬度
			double latitude = location.getLatitude();

			
			//发送短信
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("", null, "longitude: " + longitude + " latitude: "
					+ latitude, null, null);
			
			// 记得添加权限

			System.out.println("longitude: " + longitude + " latitude: "
					+ latitude);
		}

	}
}
