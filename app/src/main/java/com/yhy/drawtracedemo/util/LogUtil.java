package com.yhy.drawtracedemo.util;

import android.util.Log;



public class LogUtil {
 
	public static void d(String msg){
		if(msg==null){
			return;
		}
			Log.d("yhy", msg);
	}
}
