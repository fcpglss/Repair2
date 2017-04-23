package model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flies implements Serializable{

	private static final long serialVersionUID=75453134L;

	private int id;
	private String flies;
	private int aFloor;
	private List<Room> rooms;

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlies() {
		return flies;
	}
	public void setFlies(String flies) {
		this.flies = flies;
	}
	public int getaFloor() {
		return aFloor;
	}
	public void setaFloor(int aFloor) {
		this.aFloor = aFloor;
	}

	public static class ComparatorFlies implements Comparator {

		public int compare(Object o1, Object o2) {
			model.Flies p1 = (Flies) o1;
			model.Flies p2 = (Flies) o2;
			String tempFlies1 = p1.getFlies();
			String tempFlies2 = p2.getFlies();
			// int t = tempPlace1.compareTo(tempPlace2);

			String regEx = "[^0-9]";
			String rehExOfNumber = "[0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(tempFlies1);
			Matcher m2 = p.matcher(tempFlies2);
			//比较字符串
			int chars = m.toString().compareTo(m2.toString());

			if (chars == 0) {
				//比较数字
				Pattern number=Pattern.compile(rehExOfNumber);
				Matcher n =number.matcher(tempFlies1);
				Matcher n2=number.matcher(tempFlies2);
				return   n.toString().compareTo(n2.toString());
			} else {

				return chars;
			}
		}
	}

}
