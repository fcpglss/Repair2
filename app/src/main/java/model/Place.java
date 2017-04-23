package model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Place implements Serializable {

    private static final long serialVersionUID = 8748889646L;

    private int p_id;

    private String p_name;

    private int areaID;

    private List<Flies> flies;

    public List<Flies> getFlies() {
        return flies;
    }

    public void setFlies(List<Flies> flies) {
        this.flies = flies;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }


    public static class ComparatorPlace implements Comparator {

        public int compare(Object o1, Object o2) {
            model.Place p1 = (Place) o1;
            model.Place p2 = (Place) o2;
            String tempPlace1 = p1.getP_name();
            String tempPlace2 = p2.getP_name();
           // int t = tempPlace1.compareTo(tempPlace2);

            String regEx = "[^0-9]";
            String rehExOfNumber = "[0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(tempPlace1);
            Matcher m2 = p.matcher(tempPlace2);
            //比较字符串
            int chars = m.toString().compareTo(m2.toString());

            if (chars == 0) {
                //比较数字
                Pattern number=Pattern.compile(rehExOfNumber);
                Matcher n =number.matcher(tempPlace1);
                Matcher n2=number.matcher(tempPlace2);
                return   n.toString().compareTo(n2.toString());
            } else {

                return chars;
            }
        }
    }

}
