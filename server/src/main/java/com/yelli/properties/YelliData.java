package com.yelli.properties;

import java.util.HashMap;
import java.util.Map;

public class YelliData {

	private static YelliData instance;
	private Map<String, Room> roomMap;

	public Room createRoom(String deviceId) {
		Room room = new Room();
		roomMap.put(deviceId, room);
		return room;
	}

	public Room getRoom(String trackId) {
		for (Map.Entry<String, Room> entry : roomMap.entrySet()) {
			Room room = entry.getValue();
			if(room.getTrackId().equals(trackId)) {
				return room;
			}
		}
		return null;
	}
	
	public boolean containsRoom(String deviceId) {
		return roomMap.containsKey(deviceId);
	}
	
	public void removeRoom(String deviceId) {
		roomMap.remove(deviceId);
	}

	public Room getRoomFromDeviceId(String deviceId) {
		return roomMap.get(deviceId);
	}
	
	private YelliData() {
		roomMap = new HashMap<String, Room>();
	}

	public static YelliData getYelliData() {
		if (instance == null) {
			return instance = new YelliData();
		}
		return instance;
	}

}
