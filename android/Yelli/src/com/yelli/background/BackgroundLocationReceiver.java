package com.yelli.background;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.yelli.MessageType;
import com.yelli.Utils;
import com.yelli.requestpojo.UpdatePojo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class BackgroundLocationReceiver extends BroadcastReceiver {
 
    private String TAG = getClass().getSimpleName();

	@Override
    public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Location updated");
        Location location = (Location) intent.getExtras().get(android.location.LocationManager.KEY_LOCATION_CHANGED);
        if(location!=null) {
        	sendLocationDetails(context, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }
    
    private void sendLocationDetails(Context context, String latitude, String longitude) {
		long currentlyRunningTrackTimelimit = context.getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		Log.d(TAG,"sendLocationDetails()");
		if (!Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			Log.d(TAG , "Killing Process because tracker not currently running. Done tracking");
			return;
		}
		
		if(BackgroundService.isIntentRunning) {
			Log.d(TAG , "Killing Process because background service already running");
			return;
		}
		UpdatePojo pojo = new UpdatePojo();
		pojo.type = MessageType.UPDATE;
		pojo.deviceId = Utils.getDeviceId(context);
		pojo.latitude = latitude;
		pojo.longitude = longitude;
		String message = new Gson().toJson(pojo);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", message));
		Utils.postData(Utils.yelliHttpServerPath+"/update", params);
	}

}
