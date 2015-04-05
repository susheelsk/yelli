package com.yelli.actions;

import java.util.Timer;
import java.util.logging.Logger;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.yelli.MessageType;
import com.yelli.backgroundtasks.RemoveRoomTask;
import com.yelli.properties.Room;
import com.yelli.properties.YelliData;
import com.yelli.requestpojo.CreatePojo;

public class CreateTrackerAction implements OnAction {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Override
	public void onMessage(String message, Session session) {
		logger.info("CreateTrackerAction : "+message);
		try {
			CreatePojo pojo = new Gson().fromJson(message, CreatePojo.class);
			Room room = null;
			long timeLimit = pojo.timeLimit;
			if (timeLimit < 0) {
				sendErrorResponse(session);
				return;
			}
			if (!YelliData.getYelliData().containsRoom(pojo.deviceId)) {
				room = YelliData.getYelliData().createRoom(pojo.deviceId);
			} else {
				room = YelliData.getYelliData().getRoomFromDeviceId(pojo.deviceId);
			}
			logger.info("Tracker Created : "+room.getTrackId());
			// remove room after however long the user specifies
			RemoveRoomTask removeRoomTask = new RemoveRoomTask(pojo.deviceId);
			Timer timer = new Timer();
			timer.schedule(removeRoomTask, timeLimit);
			sendResponse(room.getTrackId(), session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendResponse(String trackId, Session session) {
		com.yelli.responsepojo.CreatePojo pojo = new com.yelli.responsepojo.CreatePojo();
		pojo.type = MessageType.CREATE;
		pojo.trackId = trackId;
		pojo.isSuccess = true;
		pojo.errorMessage = "SUCCESS";
		String message = new Gson().toJson(pojo);
		session.getAsyncRemote().sendText(message);
	}

	public void sendErrorResponse(Session session) {
		com.yelli.responsepojo.CreatePojo pojo = new com.yelli.responsepojo.CreatePojo();
		pojo.type = MessageType.CREATE;
		pojo.isSuccess = false;
		pojo.errorMessage = "Illegal arguments";
		String message = new Gson().toJson(pojo);
		session.getAsyncRemote().sendText(message);
	}

}
