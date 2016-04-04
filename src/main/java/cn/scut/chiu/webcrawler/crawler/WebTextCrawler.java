package cn.scut.chiu.webcrawler.crawler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import cn.scut.chiu.webcrawler.conf.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.crawler.extractor.WebTextInfoExtractor;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.crawler.task.Task;

public class WebTextCrawler extends CrawlerBase implements Callable<Map<WebTextURL, WebTextInfo>> {
	
	private static Logger logger = LogManager.getLogger(WebTextCrawler.class.getName());
	private WebTextInfoExtractor extractor;
	
	public WebTextCrawler(Task webTextTask) {
		super(webTextTask);
		extractor = new WebTextInfoExtractor();
	}
	
	public WebTextInfo crawl(WebTextURL url) throws SQLException {
		// TODO Auto-generated method stub
		logger.info("Web text crawler: to crawl " + url.toString());
		WebTextInfo webTextInfo = extractor.extractWebText(url);
		if (null == webTextInfo) {
			logger.warn("Web text crawler: crawl nothing \"" + url + "\"");
			statisticsAdaptor.updateCrawled(url.toString(), false, Config.getLocalMachineIp());
			return null;
		}
		statisticsAdaptor.updateCrawled(url.toString(), true, Config.getLocalMachineIp());
		logger.info("Web text crawler: crawled news \"" + url + "\"");
		return webTextInfo;
	}

	@Override
	public Map<WebTextURL, WebTextInfo> call() throws Exception {
		// TODO Auto-generated method stub
		WebTextURL url = null;
		Map<WebTextURL, WebTextInfo> results = new HashMap<WebTextURL, WebTextInfo>();
		for(int i=0; i<crawlerTask.sizeOfToDoUrlList(); i++) {
			url = crawlerTask.getToDoUrl(i);
			results.put(url, crawl(url));
		}
		return results;
	}
	
}
