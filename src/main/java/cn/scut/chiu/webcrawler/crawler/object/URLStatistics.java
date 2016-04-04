package cn.scut.chiu.webcrawler.crawler.object;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

/**
 * Created by Chiu on 2015/10/6.
 */
public class URLStatistics {

    private int id;
    private String seedUrlStr;
    private String hostName;
    private int categoryId;
    private int typeId;
    private String urlStr;
    private String urlHashCode;
    private Timestamp firstDetectTime;
    private int detectCount;
    private Timestamp lastDetectTime;
    private Timestamp crawlTime;
    private int crawlStatus;
    private String ext;
    private String detectNode;
    private String crawlNode;

    public URLStatistics(WebTextURL seed, String urlStr) {
        this.seedUrlStr = seed.getURL().toString();
        this.hostName = seed.getHostSite();
        this.categoryId = seed.getCategory();
        this.typeId = seed.getTextType();
        this.urlStr = urlStr;
        this.urlHashCode = String.valueOf(urlStr.hashCode());
        this.detectNode = Config.getLocalMachineIp();
    }

    public URLStatistics() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeedUrlStr() {
        return seedUrlStr;
    }

    public void setSeedUrlStr(String seedUrlStr) {
        this.seedUrlStr = seedUrlStr;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getUrlHashCode() {
        return urlHashCode;
    }

    public void setUrlHashCode(String urlHashCode) {
        this.urlHashCode = urlHashCode;
    }

    public Timestamp getFirstDetectTime() {
        return firstDetectTime;
    }

    public void setFirstDetectTime(Timestamp firstDetectTime) {
        this.firstDetectTime = firstDetectTime;
    }

    public int getDetectCount() {
        return detectCount;
    }

    public void setDetectCount(int detectCount) {
        this.detectCount = detectCount;
    }

    public Timestamp getLastDetectTime() {
        return lastDetectTime;
    }

    public void setLastDetectTime(Timestamp lastDetectTime) {
        this.lastDetectTime = lastDetectTime;
    }

    public Timestamp getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Timestamp crawlTime) {
        this.crawlTime = crawlTime;
    }

    public int getCrawlStatus() {
        return crawlStatus;
    }

    public void setCrawlStatus(int crawlStatus) {
        this.crawlStatus = crawlStatus;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDetectNode() {
        return detectNode;
    }

    public void setDetectNode(String detectNode) {
        this.detectNode = detectNode;
    }

    public String getCrawlNode() {
        return crawlNode;
    }

    public void setCrawlNode(String crawlNode) {
        this.crawlNode = crawlNode;
    }

    public static void main(String[] args) throws MalformedURLException {
        URLStatistics urlStatistics = new URLStatistics(new WebTextURL(new URL("http://www.baidu.com"), 1, 1), "http://www.baidu.com.cn");
        System.out.println(urlStatistics.getLastDetectTime() + "\n" + urlStatistics.getFirstDetectTime());
        WebTextURL webTextURL = new WebTextURL(new URL("http://www.ifeng.com/haha.com"), 1, 1);
        System.out.println(webTextURL.getHostSite());
    }

}
