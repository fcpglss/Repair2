package model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Area implements Serializable{

	private static final long serialVersionUID=344512344L;


	private int id;
	private String Area;

	private List<Place> places2s;

	@Override
	public String toString() {
		return "Area{" +
				"id=" + id +
				", Area='" + Area + '\'' +
				", places2s=" + places2s +
				'}';
	}

	public List<Place> getPlaces2s() {
		return places2s;
	}

	public void setPlaces2s(List<Place> places2s) {
		this.places2s = places2s;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getArea() {
		return Area;
	}
	public void setArea(String area) {
		Area = area;
	}


	public static class Comparator1 implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			model.Area area1 = (model.Area) o1;
			model.Area area2 = (model.Area) o2;
			return area1.getArea().compareTo(area2.getArea());
		}
	}


}
