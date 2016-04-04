package cn.scut.chiu.webcrawler.crawler.task;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.crawler.CrawlerHistory;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;

public class TaskFactory {

	private static Logger logger = LogManager.getLogger(TaskFactory.class.getName());
	public synchronized static LinkedBlockingQueue<Task> packTask(int commandType, String mark) throws InterruptedException, IOException {
		
		Queue<WebTextURL> urls = CrawlerHistory.getWebTextURL(commandType, mark);
		if (urls == null) {
			logger.warn("got no urls from db");
			return null;
		}
		LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
		Iterator<WebTextURL> it = urls.iterator();
		if(it.hasNext()) {
			LinkedList<WebTextURL> toCrawlUrlList = new LinkedList<WebTextURL>();
			WebTextURL url = it.next();
			String preHost = url.getHost();
			toCrawlUrlList.add(url);
			while(it.hasNext()) {
				url = it.next();
				if(toCrawlUrlList.size() <= 10 && preHost.equals(url.getHost())) {
					toCrawlUrlList.add(url);
				} else {
					switch (commandType) {
					case CommandType.WEBTEXT_DETECT:
						tasks.add(new SeedTask(toCrawlUrlList));
						break;
					case CommandType.WEBTEXT_CRAWL:
						tasks.add(new WebTextTask(toCrawlUrlList));
						break;
					}
					toCrawlUrlList = new LinkedList<WebTextURL>();
					toCrawlUrlList.add(url);
				}
				preHost = url.getHost();
			}
			if(toCrawlUrlList.size()>0) {
				switch (commandType) {
				case CommandType.WEBTEXT_DETECT:
					tasks.add(new SeedTask(toCrawlUrlList));
					break;
				case CommandType.WEBTEXT_CRAWL:
					tasks.add(new WebTextTask(toCrawlUrlList));
					break;
				}
			}
		}
		if(tasks.size()<=0) {
			return null;
		}
		return tasks;
	}
}
