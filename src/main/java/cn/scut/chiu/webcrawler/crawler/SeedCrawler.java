package cn.scut.chiu.webcrawler.crawler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cn.scut.chiu.webcrawler.crawler.object.URLStatistics;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.crawler.extractor.BlogURLExtractor;
import cn.scut.chiu.webcrawler.crawler.extractor.NewsURLExtractor;
import cn.scut.chiu.webcrawler.crawler.extractor.WebTextType;
import cn.scut.chiu.webcrawler.crawler.task.Task;

public class SeedCrawler extends CrawlerBase implements Callable<Map<WebTextURL, Set<String>>> {
	
	private static Logger logger = LogManager.getLogger(SeedCrawler.class.getName());
	private NewsURLExtractor newsURLExtractor;
	private BlogURLExtractor blogURLExtractor;


	public SeedCrawler(Task seedTask) {
		super(seedTask);
		newsURLExtractor = new NewsURLExtractor();
		blogURLExtractor = new BlogURLExtractor();
	}

	public Set<String> crawl(WebTextURL url) throws SQLException {
		logger.info("Seed Crawler: crawl " + url.toString());
		Set<String> urls = null;
		if (url.getTextType() == WebTextType.NEWS) {
			urls = newsURLExtractor.extractURL(url.getURL());
		} else if (url.getTextType() == WebTextType.BLOG) {
			urls = blogURLExtractor.extractURL(url.getURL());
		}
		Set<String> found = Sets.newHashSet();
		URLStatistics urlStat;
		for(String urlStr : urls) {
			if (null != (urlStat = statisticsAdaptor.hasDetected(urlStr))) {
				// detected before
				statisticsAdaptor.updateDetected(urlStat);
			} else {
				// not detected before
				statisticsAdaptor.insertNewlyDetected(url, urlStr);
				found.add(urlStr);
			}
		}
		if(found.size() == 0) {
			logger.info("Seed Crawler: finsh crawl nothing new: " + url.toString());
			return null;
		}
		logger.info("Seed Crawler: finsh crawl: " + url.toString());
		return found;
	}

//	public Set<String> oldApi_crawl(WebTextURL url) {
//		// TODO Auto-generated method stub
//		logger.info("Seed Crawler: crawl " + url.toString());
//		Set<String> urls = null;
//		if (url.getTextType() == WebTextType.NEWS) {
//			urls = newsURLExtractor.extractURL(url.getURL());
//		} else if (url.getTextType() == WebTextType.BLOG) {
//			urls = blogURLExtractor.extractURL(url.getURL());
//		}
//		Set<String> found = new HashSet<String>();
//		boolean detectedBefore = false;
//		for(String urlStr : urls) {
//			// no matter has been detected before, add to the newly detected
//			CrawlerHistory.addNewlyDetected(urlStr);
//			synchronized (CrawlerHistory.REFRESH) {
//				if(CrawlerHistory.hasDetected(urlStr)) {
////					System.out.println("Seed Crawler: dectected before \"" + urlStr + "\"");
//					detectedBefore = true;
//				}
//				CrawlerHistory.addHasDetected(urlStr);
//			}
//			if (detectedBefore) {
//				detectedBefore = false;
//				continue;
//			}
////			System.out.println("Seed Crawler: dectect web text \"" + urlStr + "\"");
//			found.add(urlStr);
//		}
//		if(found.size() == 0) {
//			return null;
//		}
//		logger.info("Seed Crawler: finsh crawl " + url.toString());
//		return found;
//	}

	@Override
	public Map<WebTextURL, Set<String>> call() throws Exception {
		// TODO Auto-generated method stub
		WebTextURL url = null;
		Map<WebTextURL, Set<String>> result = new HashMap<WebTextURL, Set<String>>();
		for(int i=0; i<crawlerTask.sizeOfToDoUrlList(); i++) {
			url = crawlerTask.getToDoUrl(i);
			result.put(url, crawl(url));
		}
		return result;
	}
}
