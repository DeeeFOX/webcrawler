package cn.scut.chiu.webcrawler.dao;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.object.URLStatistics;
import cn.scut.chiu.webcrawler.dao.mysql.MySQLAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Chiu on 2015/10/6.
 */
public class StatisticsDAO {

    private static Logger logger = LogManager.getLogger(StatisticsDAO.class);
    private static Connection detectedConn;
    private static PreparedStatement detPreStat;
    private static Connection upDetConn;
    private static PreparedStatement upDetPreStat;
    private static Connection insertConn;
    private static PreparedStatement insertPreStat;
    private static Connection upCrawledConn;
    private static PreparedStatement upCrawlPreStat;
    static {
        detectedConn = MySQLAPI.getNewConnection();
        upDetConn = MySQLAPI.getNewConnection();
        insertConn = MySQLAPI.getNewConnection();
        upCrawledConn = MySQLAPI.getNewConnection();
        try {
            detectedConn.setReadOnly(true);
            detPreStat = detectedConn.prepareStatement(Config.get(Params.SQL_DETECT_URL));
            upDetPreStat = upDetConn.prepareStatement(Config.get(Params.SQL_UPDATE_DETSTAT));
            insertPreStat = insertConn.prepareStatement(Config.get(Params.SQL_INSERT_STAT));
            upCrawlPreStat = upCrawledConn.prepareStatement(Config.get(Params.SQL_UPDATE_CRAWLSTAT));
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized URLStatistics hasDetected(String urlStr) throws SQLException {
        if (!checkIsValid(detectedConn)) {
            logger.warn("not valid");
            detectedConn = MySQLAPI.getNewConnection();
            detPreStat = detectedConn.prepareStatement(Config.get(Params.SQL_DETECT_URL));
        }
        int index = 1;
        detPreStat.setString(index++, urlStr);
        ResultSet rs = detPreStat.executeQuery();
        if (rs.next()) {
            URLStatistics ret = new URLStatistics();
            ret.setId(rs.getInt(Params.COLUMN_ID));
            ret.setDetectCount(rs.getInt(Config.get(Params.COLUMN_DETECTCOUNT)));
            return ret;
        } else {
            return null;
        }
    }

    public static synchronized int updateDetected(URLStatistics urlStat) throws SQLException {
        if (!checkIsValid(upDetConn)) {
            logger.warn("not valid");
            upDetConn = MySQLAPI.getNewConnection();
            upDetPreStat = upDetConn.prepareStatement(Config.get(Params.SQL_UPDATE_DETSTAT));
        }
        int index = 1;
        upDetPreStat.setInt(index++, urlStat.getDetectCount() + 1);
        upDetPreStat.setInt(index++, urlStat.getId());
        int ret = upDetPreStat.executeUpdate();
        checkAutoCommit(upDetConn);
        return ret;
    }

    public static int updateCrawled(String urlStr, int crawlStat, String nodeIp) throws SQLException {
        if (!checkIsValid(upCrawledConn)) {
            logger.warn("not valid");
            upCrawledConn = MySQLAPI.getNewConnection();
            upCrawlPreStat = upCrawledConn.prepareStatement(Config.get(Params.SQL_UPDATE_CRAWLSTAT));
        }
        int index = 1;
        upCrawlPreStat.setInt(index++, crawlStat);
        upCrawlPreStat.setString(index++, nodeIp);
        upCrawlPreStat.setString(index++, urlStr);
        int ret = upCrawlPreStat.executeUpdate();
        checkAutoCommit(upCrawledConn);
        return ret;
    }
    public static synchronized boolean insertNewlyDetected(URLStatistics urlStat) throws SQLException {
        if (!checkIsValid(insertConn)) {
            logger.warn("not valid");
            insertConn = MySQLAPI.getNewConnection();
            insertPreStat = insertConn.prepareStatement(Config.get(Params.SQL_INSERT_STAT));
        }
        int index = 1;
        insertPreStat.setObject(index++, urlStat.getSeedUrlStr());
        insertPreStat.setObject(index++, urlStat.getHostName());
        insertPreStat.setObject(index++, urlStat.getCategoryId());
        insertPreStat.setObject(index++, urlStat.getTypeId());
        insertPreStat.setObject(index++, urlStat.getUrlStr());
        insertPreStat.setObject(index++, urlStat.getUrlHashCode());
        insertPreStat.setObject(index++, urlStat.getDetectNode());
        boolean ret = insertPreStat.execute();
        checkAutoCommit(insertConn);
        return ret;
    }

    private static boolean checkIsValid(Connection detectedConn) throws SQLException {
        boolean ret = detectedConn.isValid(Config.getInt(Params.MYSQL_CONN_TIMEOUT));
        if (!ret) {
            checkAutoCommit(detectedConn);
            detectedConn.close();
        }
        return ret;
    }

    private static void checkAutoCommit(Connection conn) throws SQLException {
        if (!conn.getAutoCommit()) {
            conn.commit();
        }
    }
}
