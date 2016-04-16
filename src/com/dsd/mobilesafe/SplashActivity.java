package com.dsd.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.AlteredCharSequence;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsd.mobilesafe.activity.HomeActivity;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.utils.StreamUtil;
import com.dsd.mobilesafe.utils.ToastUtils;
import com.dsd.mobilesafe.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 初始界面
 * 
 * @author im_dsd
 * 
 */
public class SplashActivity extends Activity {

	protected static final String tag = "SplashActivity";
	/**
	 * 更新版本的状态码
	 */
	protected static final int UPDATE_VERSION = 0;
	/**
	 * URL异常
	 */
	protected static final int URL_ERROR = 1;
	/**
	 * io异常
	 */
	protected static final int IO_ERROR = 2;
	/**
	 * json解析异常
	 */
	protected static final int JSON_ERROR = 3;
	/**
	 * 进入home界面
	 */
	protected static final int ENTER_HOME = 4;

	private Message mMessage;
	private TextView tv_versionName;
	private PackageManager pm;
	private PackageInfo packageInfo;
	private int mLocalversionCode;
	private String mVersionName;
	private String mVersionDes;
	private String mVersionCode;
	private String mDownloadUrl;
	private RelativeLayout rv_splash_root;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				// 弹出对话框提示用户
				showUpdateDialog();
				break;
			case URL_ERROR:
				// 提示用户 Toast
				ToastUtils.show(SplashActivity.this, "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtils.show(SplashActivity.this, "读取异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtils.show(SplashActivity.this, "json解析异常");
				enterHome();
				break;
			case ENTER_HOME:
				enterHome();
				break;
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// [1]第一种方式去头
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// 初始化UI
		initUI();
		//添加动画
		initAnimation();
		//初始化数据
		initDate();
		//初始化数据库
		initDB();
		
		
		
	}

	/**
	 * 初始化数据库方法
	 */
	private void initDB() {
		// 1.归属地数据拷贝过程
		initAddressDB("address.db");
		
	}

	/**
	 * 拷贝数据库值files文件夹中
	 * @param dbName  数据库名称
	 */
	private void initAddressDB(String dbName) {
		//【1】在files文件夹下创建同名数据库文件
		File filesDir = getFilesDir();
		File file = new File(filesDir,dbName);
		if(file.exists())
		{
			Log.i(tag, "file存在了，他的路径:"+file.getAbsolutePath());
			return;
		}
		Log.i(tag, "file的路径:"+file.getAbsolutePath());
		FileOutputStream fileOutputStream = null;
		//【2】读取第三方资产目录下的文件，
		try {
			InputStream openStream = getAssets().open(dbName);
			//[3]将读取的内容写入到指定的文件夹中
			fileOutputStream = new FileOutputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while((len = openStream.read(buffer)) != -1)
			{
				fileOutputStream.write(buffer, 0, len);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(fileOutputStream != null)
			{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}

	/**
	 * 进入 home 界面
	 */
	protected void enterHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);

		// 终止开始界面
		finish();
	}

	/**
	 * 初始化UI方法
	 */
	public void initUI() {

		tv_versionName = (TextView) findViewById(R.id.tv_version_name);
		rv_splash_root = (RelativeLayout) findViewById(R.id.rv_splash_root);
	}

	/**
	 * 初始化数据
	 */
	public void initDate() {
		// [1]更新应用版本名称
		tv_versionName.setText("版本号：" + getVersionName());
		// [2]检测（本地版本号和服务版本号比对）是否有更新，如果有更新，提示用户下载（member）
		mLocalversionCode = getVersionCode();// 快捷键，上下一起改变量名称：shift + alt + r
		// [3]获取服务器版本号（客户端发送请求，服务器响应）
		/*
		 * 1. 从 url 返回200,请求成功，流的方式将数据读取下来 2.json中应该包含的信息 更新版本的版本名称 服务器上的版本号
		 * 新版本描述 下载新apk
		 */
		
		if(SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, true))
		{
			//检查版本
			checkVersion();
		}
		else
		{
			//发送进入主界面的消息：
			//发送延时消息,在消息发送4秒后执行消息
			//mHandler.sendMessageDelayed(msg, delayMillis)
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
		
	}
	
	/**
	 * 初始化Alpha动画
	 */
	public void initAnimation()
	{
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(2000);
		rv_splash_root.setAnimation(alphaAnimation);
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
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 返回版本好，异常时返回 0
	 */
	public int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 检测版本号
	 */
	public void checkVersion() {
		// [1]开线程的方法1
		new Thread() {

			private long startTime;

			@Override
			public void run() {
				try {
					startTime = System.currentTimeMillis();

					// 发送请求 获取数据，参数则为请求json的链接地址
					mMessage = Message.obtain();
					// 【1】封装url
					URL url = new URL("http://192.168.99.234:8080/update.json");
					// 【2】开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();

					// 【3】设置常见请求参数
					// 【3.1】请求超时
					connection.setConnectTimeout(2000);
					// 【3.2】读取超时
					connection.setReadTimeout(2000);
					// 【3.3】设置请求方式 GET
					connection.setRequestMethod("GET");

					// 【4】获取响应码，请求成功的响应码
					if (connection.getResponseCode() == 200) {
						// 【5】获取数据流
						InputStream inputStream = connection.getInputStream();

						new StreamUtil();
						// 【6】将流封装成字符串返回回来。
						String json = StreamUtil.stream2String(inputStream);
						Log.i(tag, json);

						// 【7】开始解析json文件，
						JSONObject jsonObject = new JSONObject(json);
						mVersionName = jsonObject.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						mVersionCode = jsonObject.getString("versionCode");
						mDownloadUrl = jsonObject.getString("downloadUrl");

						// 【8】，比对版本号，实现更新
						if (mLocalversionCode < Integer.parseInt(mVersionCode)) {
							// 提示用户，弹出对话框 ctr + shift + x 大写，ctr + shift + y，小写
							mMessage.what = UPDATE_VERSION;
						} else {

							mMessage.what = ENTER_HOME;
						}

					}
				} catch (NumberFormatException e) {

					e.printStackTrace();
				} catch (MalformedURLException e) {
					mMessage.what = URL_ERROR;
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					mMessage.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mMessage.what = JSON_ERROR;
					e.printStackTrace();
				} finally {

					// 指定睡眠时间，请求网络的时长超过4秒则不做处理
					// 少于4秒强制睡眠4秒
//					long endTime = System.currentTimeMillis();
//
//					if (endTime - startTime < 4000) {
//						SystemClock.sleep(4000 - (endTime - startTime));
//					}
					// 不管怎么样都应该把消息发回去
					mHandler.sendMessage(mMessage);
				}

			}

		}.start();

		// 开启线程，方法 2
		/*
		 * new Thread(new Runnable() {
		 * 
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * 
		 * } }).start();
		 */
	}

	/**
	 * 提示对话框
	 */
	protected void showUpdateDialog() {
		// 【1】对话框是依赖Activity存在的所以必须是this
		Builder builder = new AlertDialog.Builder(this);
		// 【2】设置图标
		builder.setIcon(R.drawable.ic_launcher);
		// 【3】设置title
		builder.setTitle("发现新版本");
		// 【4】设置描述内容
		builder.setMessage(mVersionDes);
		// 【5】积极按钮，立即更新
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 下载apk，apk链接地址
						downloadApk();
					}
				});

		// 【5.1】消极按钮
		builder.setNegativeButton("稍后更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消对话框，进入主界面
						enterHome();

					}
				});
		//【6】☆☆☆☆☆☆点击回退时的事件监听
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// 用户在点击取消的时候也要进入
				enterHome();
				dialog.dismiss();
				
			}
		});
		//【7】☆☆☆☆☆取消 安装apk 的逻辑 

		builder.show();
	}

	/**
	 * 下载apk的方法
	 */
	protected void downloadApk() {
		// TODO Auto-generated method stub
		// 【1】判读sd卡是否可用，是否挂在上，
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 【2】获取sd路径
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "mobilesafe.apk";
			// 【3】发送请求，获取apk，并存放到指定路径
			HttpUtils http = new HttpUtils();
			// 【4】发送请求，传递参数
			http.download(mDownloadUrl, path, new RequestCallBack<File>() {

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					// TODO 下载成功
					File file = responseInfo.result;
					Log.i(tag, "下载成功");
					// 提醒用户安装
					installApk(file);

				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.i(tag, "下载失败");
				}

				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// TODO Auto-generated method stub
					super.onLoading(total, current, isUploading);
					Log.i(tag, "下载中。。。。。。");
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					Log.i(tag, "开始下载");
				}
			});
		}

	}

	/**
	 * 安装对应apk
	 * 
	 * @param file
	 *            安装的文件
	 */
	protected void installApk(File file) {
		
		//使用隐式意图打开系统的安装软件过程
		Intent intent = new Intent("android.intent.action.VIEW");
		//设置Date
//		intent.setData(Uri.fromFile(file));
//		intent.setType("application/vnd.android.package-archive");
		intent.addCategory("android.intent.category.DEFAULT");
		
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		
		//startActivity(intent);
		//防止用户取消换安装apk，停留则splashActivity无法继续进Homeactivity
		startActivityForResult(intent, 0);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 再次调用，进入home页面
		enterHome();
	}
}
