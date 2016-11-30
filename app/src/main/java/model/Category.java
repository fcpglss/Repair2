package model;


public class Category {
 
	private int c_id;
	
	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

	private String c_name;
	
	public String getC_priority() {
		return c_priority;
	}

	public void setC_priority(String c_priority) {
		this.c_priority = c_priority;
	}

	private String c_priority;
}
