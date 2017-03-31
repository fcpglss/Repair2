package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 */

public class Apply implements Serializable {

    private static final long serialVersionUID = 31544L;

    private String id;
    private String repair;
    private String tel;
    private String email;
    private String password;
    private String area;
    private String detailArea;
    private String flies;
    private String room;
    private String classs;
    private String detailClass;
    private String repairDetails;
    private int state;
    private String repairTime;
    private String logisticMan;
    private String dealTime;
    private String serverMan;
    private String material;
    private String compensation;
    private String finilTime;
    private String evaluate;
    private String evalText;

    private String addressDetail;

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    private List<String> a_imaes = null;

    public List<String> getA_imaes() {
        return a_imaes;
    }

    public void setA_imaes(List<String> a_imaes) {
        this.a_imaes = a_imaes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetailArea() {
        return detailArea;
    }

    public void setDetailArea(String detailArea) {
        this.detailArea = detailArea;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getClasss() {
        return classs;
    }

    public void setClasss(String classs) {
        this.classs = classs;
    }

    public String getDetailClass() {
        return detailClass;
    }

    public void setDetailClass(String detailClass) {
        this.detailClass = detailClass;
    }

    public String getRepairDetails() {
        return repairDetails;
    }

    public void setRepairDetails(String repairDetails) {
        this.repairDetails = repairDetails;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRepairTime() {
        return repairTime;
    }

    public String getFlies() {
        return flies;
    }

    public void setFlies(String flies) {
        this.flies = flies;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public String getLogisticMan() {
        return logisticMan;
    }

    public void setLogisticMan(String logisticMan) {
        this.logisticMan = logisticMan;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getServerMan() {
        return serverMan;
    }

    public void setServerMan(String serverMan) {
        this.serverMan = serverMan;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCompensation() {
        return compensation;
    }

    public void setCompensation(String compensation) {
        this.compensation = compensation;
    }

    public String getFinilTime() {
        return finilTime;
    }

    public void setFinilTime(String finilTime) {
        this.finilTime = finilTime;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getEvalText() {
        return evalText;
    }

    public void setEvalText(String evalText) {
        this.evalText = evalText;
    }


}
