package model;

import java.io.Serializable;

public class Flies implements Serializable{

	private static final long serialVersionUID=75453134L;

	private int id;
	private String flies;
	private int aFloor;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlies() {
		return flies;
	}
	public void setFlies(String flies) {
		this.flies = flies;
	}
	public int getaFloor() {
		return aFloor;
	}
	public void setaFloor(int aFloor) {
		this.aFloor = aFloor;
	}
}
