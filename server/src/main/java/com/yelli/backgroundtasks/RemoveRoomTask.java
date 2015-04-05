package com.yelli.backgroundtasks;

import java.util.TimerTask;
import java.util.logging.Logger;

import com.yelli.properties.YelliData;

public class RemoveRoomTask extends TimerTask {
	private String deviceId;
	
	Logger logger = Logger.getLogger(getClass().getSimpleName());

	public RemoveRoomTask(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public void run() {
		logger.info("Removing room");
		YelliData.getYelliData().removeRoom(deviceId);
	}

}
