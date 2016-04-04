package cn.scut.chiu.webcrawler.dao.object;

/**
 * Object insert into the mongo db 4 index
 * Created by Chiu on 2015/10/31.
 */
public class WebText {
    private String A_ID;
    private String A_TITLE;
    private String A_CONTENT;
    private String A_TEXT_MODEL;
    private String A_URL;
    private long A_CRAWLTIME;
    private int A_AT_ID;
    private int A_CLASS_ID;
    private double A_POLAR;

    public String getA_ID() {
        return A_ID;
    }

    public void setA_ID(String a_ID) {
        A_ID = a_ID;
    }

    public String getA_TITLE() {
        return A_TITLE;
    }

    public void setA_TITLE(String a_TITLE) {
        A_TITLE = a_TITLE;
    }

    public String getA_CONTENT() {
        return A_CONTENT;
    }

    public void setA_CONTENT(String a_CONTENT) {
        A_CONTENT = a_CONTENT;
    }

    public String getA_TEXT_MODEL() {
        return A_TEXT_MODEL;
    }

    public void setA_TEXT_MODEL(String a_TEXT_MODEL) {
        A_TEXT_MODEL = a_TEXT_MODEL;
    }

    public String getA_URL() {
        return A_URL;
    }

    public void setA_URL(String a_URL) {
        A_URL = a_URL;
    }

    public long getA_CRAWLTIME() {
        return A_CRAWLTIME;
    }

    public void setA_CRAWLTIME(long a_CRAWLTIME) {
        A_CRAWLTIME = a_CRAWLTIME;
    }

    public int getA_AT_ID() {
        return A_AT_ID;
    }

    public void setA_AT_ID(int a_AT_ID) {
        A_AT_ID = a_AT_ID;
    }

    public int getA_CLASS_ID() {
        return A_CLASS_ID;
    }

    public void setA_CLASS_ID(int a_CLASS_ID) {
        A_CLASS_ID = a_CLASS_ID;
    }

    public double getA_POLAR() {
        return A_POLAR;
    }

    public void setA_POLAR(double a_POLAR) {
        A_POLAR = a_POLAR;
    }
}
