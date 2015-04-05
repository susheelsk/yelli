package com.yelli.actions;

import java.util.logging.Logger;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.yelli.properties.Room;
import com.yelli.properties.YelliData;
import com.yelli.requestpojo.UpdatePojo;

public class UpdateTrackerAction implements OnAction {
	
	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Override
	public void onMessage(String message, Session session) {
		logger.info("UpdateTrackerAction : " + message);
		try {
			UpdatePojo pojo = new Gson().fromJson(message, UpdatePojo.class);
			Room room = YelliData.getYelliData().getRoomFromDeviceId(pojo.deviceId);
			room.sendMessage(pojo.latitude, pojo.longitude);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
