package model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Room implements Serializable{

	private static final long serialVersionUID=62659734L;

	private int id;
	private String roomNumber;
	private int flies;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public int getFlies() {
		return flies;
	}
	public void setFlies(int flies) {
		this.flies = flies;
	}

	public static class ComparatorRoom implements Comparator {

		public int compare(Object o1, Object o2) {
			model.Room p1 = (Room) o1;
			model.Room p2 = (Room) o2;
			String tempRoom1 = p1.getRoomNumber();
			String tempRoom2 = p2.getRoomNumber();
			// int t = tempPlace1.compareTo(tempPlace2);

			String regEx = "[^0-9]";
			String rehExOfNumber = "[0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(tempRoom1);
			Matcher m2 = p.matcher(tempRoom2);
			//比较字符串
			int chars = m.toString().compareTo(m2.toString());

			if (chars == 0) {
				//比较数字
				Pattern number=Pattern.compile(rehExOfNumber);
				Matcher n =number.matcher(tempRoom1);
				Matcher n2=number.matcher(tempRoom2);
				return   n.toString().compareTo(n2.toString());
			} else {

				return chars;
			}
		}
	}

	
}
