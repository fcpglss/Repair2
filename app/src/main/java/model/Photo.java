package model;

import java.io.Serializable;

public class Photo implements Serializable{

	private static final long serialVersionUID=874L;
	private int id;
	private int a_id;
	private String imagesurl;

	public int getA_id() {
		return a_id;
	}
	public void setA_id(int a_id) {
		this.a_id = a_id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImagesurl() {
		return imagesurl;
	}
	public void setImagesurl(String imagesurl) {
		this.imagesurl = imagesurl;
	}
	
}
