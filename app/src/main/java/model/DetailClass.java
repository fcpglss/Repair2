package model;

import java.io.Serializable;

/**
 * Created by hsp on 2017/3/27.
 */

public class DetailClass implements Serializable {
    private static final long serialVersionUID = 87488455446L;
    private int id;
    private String classDetail;
    private String categoryName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassDetail() {
        return classDetail;
    }

    public void setClassDetail(String classDetail) {
        this.classDetail = classDetail;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
