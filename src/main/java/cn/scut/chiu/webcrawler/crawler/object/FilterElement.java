package cn.scut.chiu.webcrawler.crawler.object;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by Chiu on 2015/9/30.
 */
public class FilterElement {
    private String tag;
    private Map.Entry<String, Map.Entry<String, String>> attr;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map.Entry<String, Map.Entry<String, String>> getAttr() {
        return attr;
    }

    public void buildAttr(String attrName, String attrValue, String delValue) {
        Map.Entry<String, String> tmp = new AbstractMap.SimpleEntry(attrValue, delValue);
        attr = new AbstractMap.SimpleEntry(attrName, tmp);
    }

    public void print() {
        if (null == attr) {
            System.out.println(String.format("{%s}", tag));
        } else {
            System.out.println(String.format("{%s : {%s : %s}", tag, attr.getKey(), attr.getValue()));
        }
    }
}
