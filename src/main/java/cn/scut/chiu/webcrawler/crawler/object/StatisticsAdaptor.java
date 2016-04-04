package cn.scut.chiu.webcrawler.crawler.object;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.dao.StatisticsDAO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by Chiu on 2015/10/6.
 */
public class StatisticsAdaptor {

    private static Logger logger = LogManager.getLogger(StatisticsAdaptor.class.getName());

    /**
     * find urlStr detected count before and return
     * 0 not detected; > 0 detected before
     *
     * @param urlStr
     * @return
     */
    public synchronized URLStatistics hasDetected(String urlStr) throws SQLException {
        return StatisticsDAO.hasDetected(urlStr);
    }

    public synchronized int updateDetected(URLStatistics urlStatistics) throws SQLException {
        return StatisticsDAO.updateDetected(urlStatistics);
    }

    public synchronized int updateCrawled(String urlStr, boolean isSuccessed, String nodeIp) throws SQLException {
        if (isSuccessed) {
            return StatisticsDAO.updateCrawled(urlStr, Config.getInt(Params.CRAWL_STAT_SUCCESS), nodeIp);
        } else {
            return StatisticsDAO.updateCrawled(urlStr, Config.getInt(Params.CRAWL_STAT_FAIL), nodeIp);
        }
    }

    public synchronized boolean insertNewlyDetected(WebTextURL seedUrl, String urlStr) throws SQLException {
        URLStatistics urlStatistics = new URLStatistics(seedUrl, urlStr);
        return StatisticsDAO.insertNewlyDetected(urlStatistics);
    }

    public static void main(String[] args) throws SQLException, MalformedURLException {

        logger.warn("haha");
        StatisticsAdaptor adaptor = new StatisticsAdaptor();
        String url = "http://www.sina.com/haha=xxxx&xixi=?kakao";
        System.out.println(url.length());
        logger.info("haha");
        logger.error("haha");
        URLStatistics urlStatistics = adaptor.hasDetected(url);
        if (null == urlStatistics) {
            System.out.println("Insert");
            WebTextURL webTextUrl = new WebTextURL(new URL("http://www.sina.com"), 1, 1);
            adaptor.insertNewlyDetected(webTextUrl, url);
        } else if (urlStatistics.getDetectCount() < 4){
            System.out.println("Update det");
            adaptor.updateDetected(urlStatistics);
        } else {
            System.out.println("Update crawl");
            adaptor.updateCrawled(url, true, Config.getLocalMachineIp());
        }
    }
}
