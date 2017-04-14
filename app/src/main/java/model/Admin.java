package model;

import java.io.Serializable;

/**
 * Created by hsp on 2017/4/14.
 */

public class Admin implements Serializable{

    private static final long serialVersionUID = 1L;

    private String account;

    private String password;

    private String  name;

    private String e_tel;

    private String e_email;

    private String sex;

    private String job;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getE_tel() {
        return e_tel;
    }

    public void setE_tel(String e_tel) {
        this.e_tel = e_tel;
    }

    public String getE_email() {
        return e_email;
    }

    public void setE_email(String e_email) {
        this.e_email = e_email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}
