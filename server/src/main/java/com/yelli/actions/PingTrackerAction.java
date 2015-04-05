package com.yelli.actions;

import javax.websocket.Session;

public class PingTrackerAction implements OnAction{

	@Override
	public void onMessage(String message, Session session) {
		try {
			session.getAsyncRemote().sendText(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
