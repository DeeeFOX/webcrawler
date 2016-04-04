package cn.scut.chiu.webcrawler.conf;

public class Params {

	// mil seconds
	public static final int READ_TIMEOUT = 60000;
	public static final int CRAWL_TIMEOUT = 20000;
	public static final String CRAWLER_DOM_REMOVE_ELE = "crawler.dom.remove.ele";
	public static final String NEWS_URL_REGREX_FILE = "news.url.regrex.file";
	public static final String BLOG_URL_REGREX_FILE = "blog.url.regrex.file";
	// <
	public static final double MIN_CHIU_SCORE_DECLINE_RATE_STAGE_1_BOTTOM = 0.6125;
	public static final double MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_BOTTOM = 0.44;
	public static final double MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_ROOF = 1.6;
	public static final double SECONDE_MAX_CHIU_SCROE_TO_MAX_CHIU_SCORE_RATE = 0.45;
	// >
	public static final double MAX_CHIU_SCORE_DECLINE_RATE_STAGE_1 = 0.38;
	public static final double MAX_NON_HREF_RATE_INCR_STAGE_2 = 1.52;
	public static final double MAX_CHILD_NON_HREF_TEXT_RATE_2 = 0.65;
	// both
	public static final double MAX_CHILD_NON_HREF_TEXT_RATE = 0.817;

	public static final String CONF_FILE_PATH_DEFAULT = "conf/config.xml";
	public static final String FILTER_TITLE_WORD_FILE = "filter.title.word.file";
	public static final String FILTER_TITLE_TAG_FILE_NEWS = "filter.title.tag.file.news";
	public static final String FILTER_TITLE_TAG_FILE_BLOGS = "filter.title.tag.file.blogs";
	public static final long FUTURE_GET_TIMEOUT = 10;
//	public static final String LOCAL_SEED_PATH = "data/seed";
//	public static final String LOCAL_NEW_DETECT_PATH = "data/tmp/newdetect";
	public static final String CRAWLER_HISTORY_DETECTED_FILE = "crawler.his.det.file";
	public static final String CRAWLER_POST_DEL_ELE_FILE = "crawler.post.del.ele.file";
	public static final String CRAWLER_PRIOR_DEL_ELE_FILE = "crawler.prior.del.ele.file";
	public static final int MAX_CRAWL_TIME = 1;
	public static final int JOB_MANAGER_ID_BIT_COUNT = 4;
	public static final int JOB_MANAGER_ID_RANGE = 10000;
	public static final Integer JOB_ID_RANGE = 10000;
	public static final String ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
	public static final String HTABLE_FAM_INFO = "INFO";
	public static final int HTABLE_QUAL_COUNT = 6;
	public static final String HTABLE_QUAL_ATITLE = "A_TITLE";
	public static final String HTABLE_QUAL_ATEXT = "A_TEXT";
	public static final String HTABLE_QUAL_AURL = "A_URL";
	public static final String HTABLE_QUAL_ATYPEID = "A_TYPE_ID";
	public static final String HTABLE_QUAL_ACRAWLTIME = "A_CRAWLTIME";
	public static final String HTABLE_QUAL_ACLASSID = "A_CLASS_ID";
	public static final String HTABLE_TABLE_ARTICLE = "hbarticle";
	public static final String HTABLE_FAM_RES = "RES";
	public static final String HBASE_REGIONSERVER_COUNT = "hbase.regionserver.count";
	public static final String CHARSET_UTF8 = "UTF-8";
	public static final long CLUSTER_TIME_INTERVAL_HALF_MIN = 1000 * 30;
	
	public static final String CONF_KEY_SEED_NAME = "crawler.seed.path";
	public static final int CRAWLER_URL_DETECT_LIMIT = 7;
	
	public static final int MESSAGE_REQ_FIELD_COUNT = 8;
	public static final int MESSAGE_IP_START_PLACE = 4;
	public static final int MESSAGE_FIELD_SIZE = Integer.SIZE / Byte.SIZE;
	public static final String WORKERS_IP = "workers.ip";
	public static final String FILTER_HOSTS = "hosts";
	public static final String CRAWLER_HOST_SITES = "cralwer.host.sites";
	public static final String SPLIT_COMMA = ",";
	public static final String PAGE_NOT_FOUND = "页面找不到";
	public static final String MYSQL_URL = "mysql.url";
	public static final String MYSQL_USR = "mysql.usr";
	public static final String MYSQL_PWD = "mysql.pwd";
	public static final String MYSQL_DRIVER = "mysql.driver";
	public static final String MYSQL_CONN_TIMEOUT = "mysql.conn.timeout";

	public static final String COLUMN_DETECTCOUNT = "column.detectcount";
	public static final String COLUMN_ID = "id";
	public static final String SQL_DETECT_URL = "sql.detect.url";
	public static final String SQL_UPDATE_DETSTAT = "sql.update.detstat";
	public static final String SQL_INSERT_STAT = "sql.insert.stat";
	public static final String SQL_UPDATE_CRAWLSTAT = "sql.update.crawlstat";
	public static final String CRAWL_STAT_FAIL = "crawl.stat.fail";
	public static final String CRAWL_STAT_SUCCESS = "crawl.stat.success";
	public static final String MONGO_APPLICATION_CONTEXT_FILE = "mongo.application.context.file";

	public static String MIN_VALID_MAINTEXT_LEN = "min.valid.maintext.len";
	public static final String MIN_SLASH_LIMIT = "min.slash.limit";

	public static final String IF_USE_HBASE = "if.use.hbase";
}
