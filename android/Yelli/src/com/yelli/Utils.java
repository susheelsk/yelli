package com.yelli;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

public class Utils {
	public static String yelliServerPath = "ws://bokwas.com:10023/yelli";
	public static String yelliHttpServerPath = "http://bokwas.com:10024/yelli";
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

	public static void postData(final String url,final List<NameValuePair> params) {
		// Create a new HttpClient and Post Header
		new AsyncTask<String, Boolean, Void>() {

			@Override
			protected Void doInBackground(String... parameters) {
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					Log.d("BackgroundService","postData()");
					try {
						// Add your data
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
						httppost.setEntity(entity);

						// Execute HTTP Post Request
						httpclient.execute(httppost);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute("");
		
	}
}
