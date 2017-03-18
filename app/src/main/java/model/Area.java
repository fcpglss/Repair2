package model;

import java.io.Serializable;

public class Area implements Serializable{

	private static final long serialVersionUID=344512344L;


	private int id;
	private String Area;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getArea() {
		return Area;
	}
	public void setArea(String area) {
		Area = area;
	}
}
