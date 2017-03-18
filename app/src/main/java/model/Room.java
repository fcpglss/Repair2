package model;

import java.io.Serializable;

public class Room implements Serializable{

	private static final long serialVersionUID=62659734L;

	private int id;
	private String roomNumber;
	private int flies;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public int getFlies() {
		return flies;
	}
	public void setFlies(int flies) {
		this.flies = flies;
	}
	
}
