package com.dsd.mobilesafe.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

	/**
	 * 给指定字符串按照md5算法去加密
	 * @param psd 需要加密的密码
	 */
	public static String encoder(String psd) {
		//【1】指定加密算法类型
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//【2】将需要加密的字符串转换成byte类型的数组，然后进行随机哈希过程，
			byte[] bs = digest.digest(psd.getBytes());

			//【3】固定写法：遍历bs，让后让其生成32位字符串，
			//【4】拼接字符串
			StringBuffer buff = new StringBuffer();
			for(byte b : bs)
			{
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				if(hexString.length() <2)
				{
					hexString = "0"+hexString;
				}
				buff.append(hexString);		
			}
			
			return buff.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
}

