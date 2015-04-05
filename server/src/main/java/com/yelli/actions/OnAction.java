package com.yelli.actions;

import javax.websocket.Session;

public interface OnAction {
	public void onMessage(String message, Session session);
}
