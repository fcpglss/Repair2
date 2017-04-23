package model;


import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Category implements Serializable {

	private static final long serialVersionUID=8711364L;

	private int c_id;
	private List<DetailClass> detailClassList;

	public List<DetailClass> getDetailClassList() {
		return detailClassList;
	}

	public void setDetailClassList(List<DetailClass> detailClassList) {
		this.detailClassList = detailClassList;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

	private String c_name;

	public String getC_priority() {
		return c_priority;
	}

	public void setC_priority(String c_priority) {
		this.c_priority = c_priority;
	}

	private String c_priority;
	private String c_imageurl;

	public String getC_imageurl() {
		return c_imageurl;
	}

	public void setC_imageurl(String c_imageurl) {
		this.c_imageurl = c_imageurl;
	}

	public static class ComparatorCategory implements Comparator {

		public int compare(Object o1, Object o2) {
			model.Category p1 = (Category) o1;
			model.Category p2 = (Category) o2;
			String tempCategory1 = p1.getC_name();
			String tempCategory2 = p2.getC_name();
			// int t = tempPlace1.compareTo(tempPlace2);

			String regEx = "[^0-9]";
			String rehExOfNumber = "[0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(tempCategory1);
			Matcher m2 = p.matcher(tempCategory2);
			//比较字符串
			int chars = m.toString().compareTo(m2.toString());

			if (chars == 0) {
				//比较数字
				Pattern number=Pattern.compile(rehExOfNumber);
				Matcher n =p.matcher(tempCategory2);
				Matcher n2=p.matcher(tempCategory2);
				return   n.toString().compareTo(n2.toString());
			} else {

				return chars;
			}
		}
	}
	
	
}