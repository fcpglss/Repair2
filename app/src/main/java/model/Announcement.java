package model;

/**
 * Created by Administrator on 2016-12-8.
 */

public class Announcement {

    /**
     * image_url : E:\Eclipse\JDBCTest\images\ki1.jpg
     * id : 1
     * admin_name : 1
     * title : 学工处
     * create_at : 2016-12-06
     * content : 今日停水
     */

    private String image_url;
    private String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getTitle() {
        return title;
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
