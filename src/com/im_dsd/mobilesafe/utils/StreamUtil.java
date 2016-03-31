package com.im_dsd.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	
	/**
	 * 将流对转换成字符串
	 * @param is 输入流
	 * @return 转换的结果，异常时返回 null
	 */
	public static String stream2String(InputStream is)
	{
		//流转换成字符串的逻辑
		//【1】在读取的过程中，将读取的内容存储到缓存中，一次性的转换成字符串返回  ByteArrayOutputStream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//【2】读流，读到没有位置（循环来实现）
		//【2.1】设置缓冲区
		byte[] buffer = new byte[1024];
		//【2.2】记录读取内容的临时变量
		int len = -1;
		try {
			while((len = is.read(buffer)) != -1)
			{
				bos.write(buffer, 1, len);
			}
			return bos.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//【3】关闭各种流。
		try {
			if(bos != null)
			{
				bos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
