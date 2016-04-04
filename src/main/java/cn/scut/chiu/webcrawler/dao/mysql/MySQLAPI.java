package cn.scut.chiu.webcrawler.dao.mysql;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Chiu on 2015/10/6.
 */
public class MySQLAPI {
    private static Logger logger = LogManager.getLogger(MySQLAPI.class);

    public static final String URL = Config.get(Params.MYSQL_URL);
    public static final String USER = Config.get(Params.MYSQL_USR);
    public static final String PSWD = Config.get(Params.MYSQL_PWD);

    private static String DRIVER = Config.get(Params.MYSQL_DRIVER);
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getNewConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PSWD);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getNewConnection(String URL, String USER, String PSWD) {
        try {
            return DriverManager.getConnection(URL, USER, PSWD);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = MySQLAPI.getNewConnection();
        PreparedStatement preStat = conn.prepareStatement("SELECT id, textCount FROM monitorsys.topic WHERE id = ?");
        preStat.setInt(1, 6771);
        ResultSet resultSet = preStat.executeQuery();
        System.out.println(resultSet.next());
        System.out.println(resultSet.getInt("id") + " " + resultSet.getInt("textCount"));
        preStat.setInt(1, 6772);
        resultSet = preStat.executeQuery();
        System.out.println(resultSet.next());
        System.out.println(resultSet.getInt("id") + " " + resultSet.getInt("textCount"));
    }
}
