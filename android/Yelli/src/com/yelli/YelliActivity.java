package com.yelli;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.tripadvisor.seekbar.CircularClockSeekBar;
import com.tripadvisor.seekbar.CircularClockSeekBar.OnSeekBarChangeListener;
import com.yelli.background.BackgroundService;
import com.yelli.background.ServiceResultReceiver;
import com.yelli.background.ServiceResultReceiver.Receiver;

public class YelliActivity extends ActionBarActivity implements
		OnClickListener, Receiver {

	private ButtonRectangle trackButton;
	private ButtonRectangle shareButton;
	private ButtonRectangle createNewButton;
	private CircularClockSeekBar seekBar;
	private TextView seekBarText;
	private TextView helpText;
	private String TAG = getClass().getSimpleName();
	private String shareLink;

	private long timeLimit = 0;
	private ProgressDialog pdia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yelli_main);
		trackButton = (ButtonRectangle) findViewById(R.id.submitButton);
		shareButton = (ButtonRectangle) findViewById(R.id.shareButton);
		createNewButton = (ButtonRectangle) findViewById(R.id.createNewButton);
		seekBar = (CircularClockSeekBar) findViewById(R.id.seekBar);
		seekBarText = (TextView) findViewById(R.id.seekBarText);
		helpText = (TextView) findViewById(R.id.helpText);

		trackButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		createNewButton.setOnClickListener(this);

		long currentlyRunningTrackTimelimit = getSharedPreferences(
				Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE).getLong(
				Utils.TIME_LIMIT_PREF, 0);
		if (Utils.isCurrentlyBeingTracked(currentlyRunningTrackTimelimit)) {
			showTrackId(getSharedPreferences(Utils.SHARED_PREF_NAME,
					MODE_PRIVATE).getString(Utils.TRACKING_ID_PREF, ""));
		}
		// Calendar calendar = Calendar.getInstance();
		// DateTime minTime = new DateTime(calendar.get(Calendar.YEAR),
		// calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
		// calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
		// calendar.add(Calendar.HOUR, 2);
		// DateTime maxTime = new DateTime(calendar.get(Calendar.YEAR),
		// calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
		// calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
		seekBar.setMaxProgress(120);
		seekBar.setSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(CircularClockSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(CircularClockSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(CircularClockSeekBar seekBar,
					int progress, boolean fromUser) {
				if (progress == 120) {
					progress = 0;
				}
				if (progress < 60) {
					seekBarText.setText("Allow tracking for the next "
							+ progress + " mins");
				} else {
					int mins = progress - 60;
					seekBarText
							.setText("Allow tracking for the next 1 hour and "
									+ mins + " mins");
				}
				timeLimit = progress * 60000;
				Log.d(TAG, "Date chosen : " + new Date(timeLimit).toString());
			}

			@Override
			public void onAnimationComplete(CircularClockSeekBar seekBar) {

			}
		});

	}

	private void showTrackId(String trackId) {
		seekBarText.setVisibility(View.GONE);
		seekBar.setVisibility(View.GONE);
		trackButton.setVisibility(View.GONE);
		findViewById(R.id.shareButtonLayout).setVisibility(View.VISIBLE);
		shareLink = Utils.yelliWebPath + "?trackId=" + trackId;
		helpText.setText("Sharing the link will let the chosen ones to see where you are");
	}
	
	private void createNewTrackId() {
		seekBarText.setVisibility(View.VISIBLE);
		seekBar.setVisibility(View.VISIBLE);
		trackButton.setVisibility(View.VISIBLE);
		findViewById(R.id.shareButtonLayout).setVisibility(View.GONE);
		shareLink = "";
		helpText.setText("Set the time till which you wish to be tracked by the people chosen by yourself");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.shareButton) {
			shareIntent(shareLink);
		} else if (v.getId() == R.id.submitButton) {
			if (timeLimit < (10 * 60000)) {
				Toast.makeText(this,
						"Please select a time period more than 10 minutes ",
						Toast.LENGTH_SHORT).show();
				return;
			}

			int locationMode = 0;
			try {
				locationMode = getLocationMode();
			} catch (SettingNotFoundException e) {
				e.printStackTrace();
			}
			if (locationMode != 3) {
				startActivity(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				return;
			}

			pdia = new ProgressDialog(this, "Connecting");
			pdia.setCancelable(false);
			pdia.show();
			// create intent and create tracking id
			ServiceResultReceiver receiver = new ServiceResultReceiver(
					new Handler());
			receiver.setReceiver(this);
			Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
					BackgroundService.class);
			intent.putExtra("fromActivity", true);
			intent.putExtra("timelimit", timeLimit);
			intent.putExtra("receiver", receiver);
			startService(intent);

		} else if (v.getId() == R.id.createNewButton) {
			createNewTrackId();
		}
	}

	private void shareIntent(String link) {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		// Add data to the intent, the receiving app will decide
		// what to do with it.
		share.putExtra(Intent.EXTRA_SUBJECT, "Yelli");
		share.putExtra(Intent.EXTRA_TEXT, link);

		startActivity(Intent.createChooser(share, "Share link!"));
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String errorMessage = resultData.getString("error");
		pdia.cancel();
		if (errorMessage == null || errorMessage.equals("")) {
			String trackId = resultData.getString("trackId");
			showTrackId(trackId);
		} else {
			// error. do something
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("InlinedApi")
	private int getLocationMode() throws SettingNotFoundException {
		int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		if (apiLevel < 19) {
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			boolean isGpsEnabled = manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (isGpsEnabled) {
				return 3;
			} else {
				return 0;
			}
		} else {
			return Settings.Secure.getInt(getContentResolver(),
					Settings.Secure.LOCATION_MODE);
		}

	}

}
