package model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-12-8.
 */

public class Announcement implements Serializable {

    /**
     * image_url : E:\Eclipse\JDBCTest\images\ki1.jpg
     * id : 1
     * admin_name : 1
     * title : ѧ����
     * create_at : 2016-12-06
     * content : ����ͣˮ
     */
    private static final long serialVersionUID=8711368828013044L;

    private String image_url;
    private int id;
    private String admin_name;
    private String title;
    private String create_at;
    private String content;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getTitle() {
        return title;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
