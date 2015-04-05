package com.yelli;

import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.yelli.actions.CreateTrackerAction;
import com.yelli.actions.OnAction;
import com.yelli.actions.PingTrackerAction;
import com.yelli.actions.SubscribeTrackerAction;
import com.yelli.actions.UpdateTrackerAction;
import com.yelli.requestpojo.RequestPojo;

@ServerEndpoint(value = "/track")
public class YelliWebSocketApi {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected with sessionId " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		RequestPojo requestPojo = new Gson().fromJson(message, RequestPojo.class);
		OnAction onAction = null;
		switch (requestPojo.type) {
		case CREATE:
			logger.info("Create Tracker with sessionId : " + session.getId());
			onAction = new CreateTrackerAction();
			break;
		case SUBSCRIBE:
			logger.info("Subscribe Tracker with sessionId : " + session.getId());
			onAction = new SubscribeTrackerAction();
			break;
		case UPDATE:
			logger.info("Update Tracker with sessionId : " + session.getId());
			onAction = new UpdateTrackerAction();
			break;
		case PING:
			logger.info("Ping from sessionId : " + session.getId());
			onAction = new PingTrackerAction();
			break;
		default:
			logger.info("Unsupported action. Weird! Call the guy who wrote this");
			return;
		}
		onAction.onMessage(message, session);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("Disconnected from sessionId : "+session.getId());
	}

}
