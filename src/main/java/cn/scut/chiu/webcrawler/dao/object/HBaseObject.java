package cn.scut.chiu.webcrawler.dao.object;

import java.util.Map;

/**
 * Created by Chiu on 2015/9/29.
 */
public class HBaseObject {
    private String rowKey;
    private Map<String, Map<String, String>> fams;
    private long timestamp;

    public HBaseObject(String rowKey, Map<String, Map<String, String>> fams, long timestamp) {
        this.rowKey = rowKey;
        this.fams = fams;
        this.timestamp = timestamp;
    }

    public String getRowKey() {
        return rowKey;
    }

    public Map<String, Map<String, String>> getFams() {
        return fams;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
