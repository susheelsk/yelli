package com.yelli.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.yelli.responsepojo.LocationPojo;

public class Room {

	private String trackId;
	private List<Session> sessionList;
	private long createdTimestamp;
	private long lastUpdatedTimestamp;
	private String latitude;
	private String longitude;

	public Room(String trackId) {
		this.trackId = trackId;
		this.sessionList = new ArrayList<Session>();
		this.createdTimestamp = System.currentTimeMillis();
	}

	public Room() {
		this.trackId = getRandomTrackId();
		this.sessionList = new ArrayList<Session>();
		this.createdTimestamp = System.currentTimeMillis();
	}
	
	public void sendMessage(String latitude,String longitude) throws IOException {
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastUpdatedTimestamp = System.currentTimeMillis();
		LocationPojo locationPojo = new LocationPojo();
		locationPojo.type = MessageType.LOCATION;
		locationPojo.isSuccess = true;
		locationPojo.latitude = this.latitude;
		locationPojo.longitude = this.longitude;
		locationPojo.timestamp = this.lastUpdatedTimestamp;
		String message = new Gson().toJson(locationPojo);
		for(Session session : sessionList) {
			if(session.isOpen()) {
				session.getAsyncRemote().sendText(message);
			}
		}
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	
	public String getLongitude() {
		return this.longitude;
	}
	
	public long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public long getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}


	public String getTrackId() {
		return this.trackId;
	}

	public List<Session> getSessionList() {
		return this.sessionList;
	}

	public void addSession(Session session) {
		this.sessionList.add(session);
	}

	public void removeSession(Session removeSession) {
		Session tempSession = null;
		for (Session session : sessionList) {
			if (session.getId().equals(removeSession.getId())) {
				tempSession = session;
			}
		}
		if (tempSession != null) {
			sessionList.remove(tempSession);
		}
	}

	public static String getRandomTrackId() {
		Random r = new Random();
		int randomNumber = r.nextInt(10000000 - 1) + 1;
		return String.valueOf(randomNumber);
//		UUID uuid = UUID.fromString(String.valueOf(randomNumber));
//		return uuid.toString();
	}

}
