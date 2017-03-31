package model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016-12-14.
 */

public class ResultBean implements Serializable {
    private static final long serialVersionUID = 8745487853134L;


    private List<Room> rooms;

    private List<Flies> flies;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Flies> getFlies() {
        return flies;
    }

    public void setFlies(List<Flies> flies) {
        this.flies = flies;
    }

    public List<Area> getArea() {
        return area;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }

    private List<Area> area;

    private List<State> states;

    private List<Apply> applys;

    private List<Category> category;

    private List<DetailClass> detailClasses;

    public List<DetailClass> getDetailClasses() {
        return detailClasses;
    }

    public void setDetailClasses(List<DetailClass> detailClasses) {
        this.detailClasses = detailClasses;
    }

    public List<Area> getAreas() {
        return area;
    }

    public void setAreas(List<Area> areas) {
        this.area = areas;
    }

    public List<State> getStates() {
        return states;

    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    private List<Announcement> announcements;

    private List<Place> places;

    private List<Employee> employee;

    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Employee> getEmployee() {
        return employee;
    }

    public void setEmployee(List<Employee> employee) {
        this.employee = employee;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }


    public List<Apply> getApplys() {
        return applys;
    }

    public void setApplys(List<Apply> Applys) {
        this.applys = Applys;
    }


    @Override
    public String toString() {
        return "ResultBean{" +
                "rooms=" + rooms +
                ", flies=" + flies +
                ", area=" + area +
                ", states=" + states +
                ", applys=" + applys +
                ", category=" + category +
                ", announcements=" + announcements +
                ", places=" + places +
                ", employee=" + employee +
                ", photos=" + photos +
                '}';
    }
}
