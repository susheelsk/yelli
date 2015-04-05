package com.yelli.actions;

import java.util.logging.Logger;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.yelli.MessageType;
import com.yelli.properties.Room;
import com.yelli.properties.YelliData;
import com.yelli.requestpojo.SubscribePojo;
import com.yelli.responsepojo.LocationPojo;

public class SubscribeTrackerAction implements OnAction {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Override
	public void onMessage(String message, Session session) {
		logger.info("SubscribeTrackerAction : " + message);
		try {
			SubscribePojo pojo = new Gson().fromJson(message, SubscribePojo.class);
			Room room = YelliData.getYelliData().getRoom(pojo.trackId);
			if (room != null) {
				room.addSession(session);
				sendResponse(room, session);
			} else {
				sendErrorResponse(session);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(Room room, Session session) {
		LocationPojo pojo = new LocationPojo();
		pojo.type = MessageType.LOCATION;
		pojo.isSuccess = true;
		pojo.errorMessage = "SUCCESS";
		pojo.timestamp = room.getLastUpdatedTimestamp();
		pojo.latitude = room.getLatitude();
		pojo.longitude = room.getLongitude();
		String message = new Gson().toJson(pojo);
		session.getAsyncRemote().sendText(message);
	}

	public void sendErrorResponse(Session session) {
		LocationPojo pojo = new LocationPojo();
		pojo.type = MessageType.LOCATION;
		pojo.isSuccess = false;
		pojo.errorMessage = "Can't find yelli tracking id. Session has expired";
		String message = new Gson().toJson(pojo);
		session.getAsyncRemote().sendText(message);
	}

}
