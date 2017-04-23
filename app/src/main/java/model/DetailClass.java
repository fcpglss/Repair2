package model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static class ComparatorDetail implements Comparator {

        public int compare(Object o1, Object o2) {
            model.DetailClass p1 = (DetailClass) o1;
            model.DetailClass p2 = (DetailClass) o2;
            String tempDetailClass1 = p1.getClassDetail();
            String tempDetailClass2 = p2.getClassDetail();
            // int t = tempPlace1.compareTo(tempPlace2);

            String regEx = "[^0-9]";
            String rehExOfNumber = "[0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(tempDetailClass1);
            Matcher m2 = p.matcher(tempDetailClass2);
            //比较字符串
            int chars = m.toString().compareTo(m2.toString());

            if (chars == 0) {
                //比较数字
                Pattern number = Pattern.compile(rehExOfNumber);
                Matcher n = p.matcher(tempDetailClass1);
                Matcher n2 = p.matcher(tempDetailClass2);
                return n.toString().compareTo(n2.toString());
            } else {

                return chars;
            }
        }
    }
}