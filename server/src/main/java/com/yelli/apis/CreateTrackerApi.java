package com.yelli.apis;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.google.gson.Gson;
import com.yelli.backgroundtasks.RemoveRoomTask;
import com.yelli.properties.MessageType;
import com.yelli.properties.Room;
import com.yelli.properties.YelliData;
import com.yelli.requestpojo.CreatePojo;

public class CreateTrackerApi extends HttpHandler {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	private void sendResponse(String trackId, Response response) throws IOException {
		com.yelli.responsepojo.CreatePojo pojo = new com.yelli.responsepojo.CreatePojo();
		pojo.type = MessageType.CREATE;
		pojo.trackId = trackId;
		pojo.isSuccess = true;
		pojo.errorMessage = "SUCCESS";
		String message = new Gson().toJson(pojo);
		response.getWriter().append(message);
	}

	private void sendErrorResponse(Response response) throws IOException {
		com.yelli.responsepojo.CreatePojo pojo = new com.yelli.responsepojo.CreatePojo();
		pojo.type = MessageType.CREATE;
		pojo.isSuccess = false;
		pojo.errorMessage = "Illegal arguments";
		String message = new Gson().toJson(pojo);
		response.getWriter().append(message);
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		String message = request.getParameter("data");
		logger.info("CreateTrackerAction : " + message);
		try {
			CreatePojo pojo = new Gson().fromJson(message, CreatePojo.class);
			Room room = null;
			long timeLimit = pojo.timeLimit;
			if (timeLimit < 0) {
				sendErrorResponse(response);
				return;
			}
			if (!YelliData.getYelliData().containsRoom(pojo.deviceId)) {
				room = YelliData.getYelliData().createRoom(pojo.deviceId);
			} else {
				room = YelliData.getYelliData().getRoomFromDeviceId(pojo.deviceId);
			}
			logger.info("Tracker Created : " + room.getTrackId());
			// remove room after however long the user specifies
			RemoveRoomTask removeRoomTask = new RemoveRoomTask(pojo.deviceId);
			Timer timer = new Timer();
			timer.schedule(removeRoomTask, timeLimit);
			sendResponse(room.getTrackId(), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
