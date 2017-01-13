package model;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {

	private static final long serialVersionUID=8715451231544L;

	private int e_id;
	
	private String employeeName;
	
	private String  fireData;
	
	private String e_tel;
	
	private String e_can;
	
	private int e_age;
	private String e_company;
	private String e_idcard;
	public int getE_id() {
		return e_id;
	}
	public void setE_id(int e_id) {
		this.e_id = e_id;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getFireData() {
		return fireData;
	}
	public void setFireData(String fireData) {
		this.fireData = fireData;
	}
	public String getE_tel() {
		return e_tel;
	}
	public void setE_tel(String e_tel) {
		this.e_tel = e_tel;
	}
	public String getE_can() {
		return e_can;
	}
	public void setE_can(String e_can) {
		this.e_can = e_can;
	}
	public int getE_age() {
		return e_age;
	}
	public void setE_age(int e_age) {
		this.e_age = e_age;
	}
	public String getE_company() {
		return e_company;
	}
	public void setE_company(String e_company) {
		this.e_company = e_company;
	}
	public String getE_idcard() {
		return e_idcard;
	}
	public void setE_idcard(String e_idcard) {
		this.e_idcard = e_idcard;
	}

	

	
}