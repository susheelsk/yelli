package com.yelli;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;

public class Utils {
	public static String yelliServerPath = "ws://bokwas.com:10023/yelli/track";
	public static String yelliWebPath = "http://bokwas.com/yelli";

	public static String SHARED_PREF_NAME = "yelli_prefs";
	public static String TIME_LIMIT_PREF = "time_prefs";
	public static String TRACKING_ID_PREF = "tracking_prefs";

	public static boolean isCurrentlyBeingTracked(long timeLimit) {
		if (timeLimit > System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	public static String getDeviceId(Context context) {
		String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		Log.d("Android", "Android ID : " + androidId);
		return androidId;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
