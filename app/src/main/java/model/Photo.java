package model;

import java.io.Serializable;

public class Photo implements Serializable{

	private static final long serialVersionUID=874L;

	private String repairID;
	private String photo;


	public String getRepairID() {
		return repairID;
	}
	public void setRepairID(String repairID) {
		this.repairID = repairID;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
