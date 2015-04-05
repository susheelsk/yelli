package com.yelli.background;

import java.net.URI;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.yelli.MessageType;
import com.yelli.Utils;
import com.yelli.requestpojo.PingPojo;
import com.yelli.requestpojo.UpdatePojo;
import com.yelli.responsepojo.CreatePojo;
import com.yelli.responsepojo.RequestPojo;
import com.yelli.webscoket.WebSocketClient;
import com.yelli.webscoket.WebSocketClient.Listener;

public class BackgroundService extends NonStopIntentService implements
		LocationListener {

	protected String TAG = getClass().getSimpleName();
	private WebSocketClient client;
	private boolean isActivityLaunch = true;
	private LocationManager locationManager;
	private boolean isConnected = false;
	private final long pingInterval = 20000; // 20 second
	private long lastPingResponse;
	private Intent intent;
	private long timelimit;

	public BackgroundService(String name) {
		super(name);
	}

	public BackgroundService() {
		super("yelli");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.intent = intent;
		if (intent.getExtras() != null
				&& intent.getExtras().getBoolean("fromActivity", false)) {
			isActivityLaunch = true;
			timelimit = intent.getExtras().getLong("timelimit");
		}
		initWebSocketClient();
		client.connect();
	}

	private void initWebSocketClient() {
		long currentlyRunningTrackTimelimit = getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		if (!Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			stopSelf();
		}
		client = new WebSocketClient(URI.create(Utils.yelliServerPath),
				new Listener() {

					@Override
					public void onMessage(byte[] data) {
						Log.d(TAG, "onMessage and data in byte[]");
						String response = new String(data);
						onMessage(response);
					}

					@Override
					public void onMessage(String message) {
						Log.d(TAG, "Message : " + message);
						RequestPojo requestPojo = new Gson().fromJson(message,
								RequestPojo.class);
						switch (requestPojo.type) {
						case CREATE:
							if (requestPojo.isSuccess) {
								CreatePojo createPojo = new Gson().fromJson(
										message, CreatePojo.class);
								String trackId = createPojo.trackId;
								getSharedPreferences(Utils.SHARED_PREF_NAME,
										Context.MODE_PRIVATE)
										.edit()
										.putString(Utils.TRACKING_ID_PREF,
												trackId).commit();
								if (isActivityLaunch) {
									sendTrackingIdToActivity(trackId);
								}
								handler.postDelayed(locationRunnable, 100);
							} else {
								Log.d(TAG,
										"Something went wrong trying to create a room");
								sendErrorToActivity("Something went wrong.");
							}
							break;
						case LOCATION:
							// do nothing
							break;
						case SUBSCRIBE:
							// not gonna happen bro
							break;
						case UPDATE:
							// ain't gonna happen bro
							break;
						case PING:
							lastPingResponse = System.currentTimeMillis();
							break;
						default:
							// chance illa macha
							break;

						}
					}

					@Override
					public void onError(Exception error) {
						Log.d(TAG, "onError() : " + error.getMessage());
						isConnected = false;
						handler.postDelayed(reconnectRunnable, 60000);
						sendErrorToActivity("Unable to connect to server");
					}

					@Override
					public void onDisconnect(int code, String reason) {
						Log.d(TAG, "onDisconnect() : " + reason);
						isConnected = false;
						handler.postDelayed(reconnectRunnable, 60000);
						sendErrorToActivity("Unable to connect to server");
					}

					@Override
					public void onConnect() {
						isConnected = true;
						handler.postDelayed(pingRunable, pingInterval);
						lastPingResponse = System.currentTimeMillis();
						long currentlyRunningTrackTimelimit = getSharedPreferences(
								Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE)
								.getLong(Utils.TIME_LIMIT_PREF, 0);
						if (Utils
								.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
							handler.postDelayed(locationRunnable, 100);
						} else {
							createRoom();
						}
					}
				}, null);
	}

	private void createRoom() {
		long currentlyRunningTrackTimelimit = getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		if (!Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			stopSelf();
		}
		com.yelli.requestpojo.CreatePojo pojo = new com.yelli.requestpojo.CreatePojo();
		pojo.type = MessageType.CREATE;
		pojo.timeLimit = timelimit;
		pojo.deviceId = Utils.getDeviceId(this);
		String message = new Gson().toJson(pojo);
		client.send(message);
	}

	private void sendLocationDetails(String latitude, String longitude) {
		long currentlyRunningTrackTimelimit = getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		if (!Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			stopSelf();
		}
		UpdatePojo pojo = new UpdatePojo();
		pojo.type = MessageType.UPDATE;
		pojo.deviceId = Utils.getDeviceId(this);
		pojo.latitude = latitude;
		pojo.longitude = longitude;
		String message = new Gson().toJson(pojo);
		client.send(message);
	}

	final Handler handler = new Handler();
	Runnable pingRunable = new Runnable() {

		@Override
		public void run() {
			try {
				if (isConnected) {
					if (System.currentTimeMillis() - lastPingResponse > (2 * pingInterval)) {
						client.disconnect();
						return;
					}
					PingPojo pojo = new PingPojo();
					pojo.type = MessageType.PING;
					String message = new Gson().toJson(pojo);
					client.send(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// also call the same runnable
				handler.postDelayed(this, pingInterval);
			}
		}
	};

	Runnable reconnectRunnable = new Runnable() {

		@Override
		public void run() {
			if (Utils.isNetworkAvailable(BackgroundService.this)) {
				initWebSocketClient();
				client.connect();
			}
		}
	};

	Runnable locationRunnable = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "In LocationRunnable");
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 20,
					BackgroundService.this);
		}
	};

	protected void sendTrackingIdToActivity(String trackId) {
		getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE)
				.edit()
				.putLong(Utils.TIME_LIMIT_PREF,
						(timelimit + System.currentTimeMillis())).commit();
		ResultReceiver receiver = intent.getParcelableExtra("receiver");
		Bundle bundle = new Bundle();
		bundle.putString("trackId", trackId);
		receiver.send(23, bundle);
	}

	private void sendErrorToActivity(String errorMessage) {
		getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE)
				.edit()
				.putLong(Utils.TIME_LIMIT_PREF,
						(timelimit + System.currentTimeMillis())).commit();
		ResultReceiver receiver = intent.getParcelableExtra("receiver");
		Bundle bundle = new Bundle();
		bundle.putString("error", errorMessage);
		receiver.send(23, bundle);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		sendLocationDetails(String.valueOf(location.getLatitude()),
				String.valueOf(location.getLongitude()));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

}
