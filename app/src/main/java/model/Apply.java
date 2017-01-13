package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 */

public  class Apply implements Serializable {

    private static final long serialVersionUID=31544L;


    private int a_id;
    private String a_no;
    private String a_name;
    private String a_tel;
    private int a_category;
    private int a_place;
    private String a_detalis;
    private String a_describe;
    private String a_status;
    private String employees;
    private String a_pingjia;
    private String a_createat;
    
   

	public String getA_createat() {
		return a_createat;
	}

	public void setA_createat(String a_createat) {
		this.a_createat = a_createat;
	}

	private List<String> a_imaes=null;

    public List<String> getA_imaes() {
		return a_imaes;
	}

	public void setA_imaes(List<String> a_imaes) {
		this.a_imaes = a_imaes;
	}

	public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }

    public String getA_no() {
        return a_no;
    }

    public void setA_no(String a_no) {
        this.a_no = a_no;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getA_tel() {
        return a_tel;
    }

    public void setA_tel(String a_tel) {
        this.a_tel = a_tel;
    }

    public int getA_category() {
        return a_category;
    }

    public void setA_category(int a_category) {
        this.a_category = a_category;
    }

    public int getA_place() {
        return a_place;
    }

    public void setA_place(int a_place) {
        this.a_place = a_place;
    }

    public String getA_detalis() {
        return a_detalis;
    }

    public void setA_detalis(String a_detalis) {
        this.a_detalis = a_detalis;
    }

    public String getA_describe() {
        return a_describe;
    }

    public void setA_describe(String a_describe) {
        this.a_describe = a_describe;
    }

    public String getA_status() {
        return a_status;
    }

    public void setA_status(String a_status) {
        this.a_status = a_status;
    }

  

   
    public String getEmployees() {
		return employees;
	}

	public void setEmployees(String employees) {
		this.employees = employees;
	}

	public String getA_pingjia() {
        return a_pingjia;
    }

    public void setA_pingjia(String a_pingjia) {
        this.a_pingjia = a_pingjia;
    }
}
