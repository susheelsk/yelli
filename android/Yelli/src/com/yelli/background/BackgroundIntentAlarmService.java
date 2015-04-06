package com.yelli.background;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.yelli.MessageType;
import com.yelli.Utils;
import com.yelli.requestpojo.UpdatePojo;

public class BackgroundIntentAlarmService extends IntentService implements LocationListener{

	private String TAG = getClass().getSimpleName();

	public BackgroundIntentAlarmService(String name) {
		super(name);
	}

	public BackgroundIntentAlarmService() {
		super("yelli");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");
		handler.post(locationRunnable);
	}
	
	final Handler handler = new Handler();
	Runnable locationRunnable = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "In LocationRunnable");
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 20,
					BackgroundIntentAlarmService.this);
		}
	};

	private void sendLocationDetails(String latitude, String longitude) {
		long currentlyRunningTrackTimelimit = getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		Log.d(TAG, "sendLocationDetails()");
		if (!Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			Log.d(TAG,
					"Killing Process because tracker not currently running. Done tracking");
			cancelAlarm();
			return;
		}

		if (BackgroundService.isIntentRunning) {
			Log.d(TAG,
					"Killing Process because background service already running");
			return;
		}
		UpdatePojo pojo = new UpdatePojo();
		pojo.type = MessageType.UPDATE;
		pojo.deviceId = Utils.getDeviceId(this);
		pojo.latitude = latitude;
		pojo.longitude = longitude;
		String message = new Gson().toJson(pojo);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", message));
		Utils.postData(Utils.yelliHttpServerPath + "/update", params);
	}

	public void cancelAlarm() {
		Intent intent = new Intent(getApplicationContext(),
				MyAlarmReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this,
				MyAlarmReceiver.REQUEST_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG,"Location updated");
		sendLocationDetails( String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
