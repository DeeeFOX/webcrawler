package cn.scut.chiu.webcrawler.crawler.job;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.CrawlerHistory;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.SeedCrawler;
import cn.scut.chiu.webcrawler.crawler.task.TaskFactory;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;

public class SeedCrawlJob extends Job {

	private static Logger logger = LogManager.getLogger(SeedCrawlJob.class.getName());
	private Queue<Future<Map<WebTextURL, Set<String>>>> seedTaskResults;
	
	public SeedCrawlJob(int id) {
		super(id);
		seedTaskResults = new LinkedBlockingQueue<Future<Map<WebTextURL, Set<String>>>>();
	}

	@Override
	public void start() {
		try {
			tasks = TaskFactory.packTask(CommandType.WEBTEXT_DETECT, mark);
			if (tasks == null) {
				logger.error("got no tasks from task factory");
				isFinished = true;
				informJobManager(CommandType.DECTECT_DONE);
				return;
			}
			new Thread(this).start();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Future<Map<WebTextURL, Set<String>>> future = null;
		while (!crawlerPool.isTerminated()) {
			if (tasks.size() == 0) {
				crawlerPool.shutdown();
				continue;
			}
			future = crawlerPool.submit(new SeedCrawler(tasks.poll()));
			seedTaskResults.add(future);
		}
		
		Map<WebTextURL, Set<String>> results = null;
		while (!seedTaskResults.isEmpty()) {
			future = seedTaskResults.poll();
			try {
				results = future.get(Params.FUTURE_GET_TIMEOUT, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				logger.error("thread's future get time out");
				//newsTaskResults.add(future);
				continue;
			} catch (InterruptedException e) {
				logger.error("Execution exception: " + e.getMessage());
				//newsTaskResults.add(future);
				continue;
			} catch (ExecutionException e) {
				logger.error("Execution exception: " + e.getMessage());
				//newsTaskResults.add(future);
				continue;
			}
			if(results == null) {
				continue;
			}
			insertResults(results);
			results.clear();
			results = null;
			isFinished = true;
		}
		informJobManager(CommandType.DECTECT_DONE);
		logger.info("Seed crawler job done");
	}

	@Override
	protected boolean insertResults(Map<?, ?> results) {
		if (results == null) {
			return false;
		}
		for (Map.Entry<?, ?> entry: results.entrySet()) {
			Set<String> urlStrs = (Set<String>) entry.getValue();
			if (urlStrs == null || urlStrs.size() == 0) {
				continue;
			}
			WebTextURL txtUrl = (WebTextURL) entry.getKey();
			for(String urlStr : urlStrs) {
				try {
					CrawlerHistory.addToCrawlURLs(mark, new WebTextURL(new URL(urlStr), txtUrl.getCategory(), txtUrl.getTextType()));
				} catch (MalformedURLException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
