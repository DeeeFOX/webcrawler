package cn.scut.chiu.webcrawler.crawler.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.crawler.object.TextInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.WebTextCrawler;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.crawler.task.TaskFactory;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;

public class WebTextCrawlJob extends Job {

	private static final Logger logger = LogManager.getLogger(WebTextCrawlJob.class.getName());
	
	private Queue<Future<Map<WebTextURL, WebTextInfo>>> webTextTaskResults;
	
	public WebTextCrawlJob(int id) {
		super(id);
		webTextTaskResults = new LinkedBlockingQueue<Future<Map<WebTextURL, WebTextInfo>>>();
	}

	@Override
	public void start() {
		try {
			tasks = TaskFactory.packTask(CommandType.WEBTEXT_CRAWL, mark);
			if (tasks == null) {
				logger.warn("got no tasks from task factory");
				isFinished = true;
				informJobManager(CommandType.CRAWL_DONE);
				return;
			}
			new Thread(this).start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if (jobManager.getJobManagerId() == -1) {
			// to avoid the start crawling
//			CrawlerHistory.reNewDectectedURLs();
			isFinished = true;
			informJobManager(CommandType.CRAWL_DONE);
			return;
		}
		Future<Map<WebTextURL, WebTextInfo>> future = null;
		while (!crawlerPool.isTerminated()) {
			if (tasks.size() == 0) {
				crawlerPool.shutdown();
				continue;
			}
			future = crawlerPool.submit(new WebTextCrawler(tasks.poll()));
			webTextTaskResults.add(future);
		}
		Map<WebTextURL, WebTextInfo> results = null;
		while(!webTextTaskResults.isEmpty()) {
			future = webTextTaskResults.poll();
			try {
				results = future.get(Params.FUTURE_GET_TIMEOUT, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				logger.error("thread's future get time out");
				//newsTaskResults.add(future);
				continue;
			} catch (InterruptedException e) {
				logger.error("InterruptedException exception: " + e.getMessage());
				//newsTaskResults.add(future);
				continue;
			} catch (ExecutionException e) {
				logger.error("Execution exception: " + e.getMessage());
				//newsTaskResults.add(future);
				continue;
			}
			insertResults(results);
			results.clear();
			results = null;
			isFinished = true;
		}
		informJobManager(CommandType.CRAWL_DONE);
		logger.info("Text crawler job done");
	}

	@Override
	protected boolean insertResults(Map<?, ?> results) {
		if (null == results || 0 == results.size()) {
			logger.error("Web text crawler job's result is empty");
			return false;
		}
		List<TextInfo> toSave = new ArrayList(results.values());
		int crawledTextsCount = 0;
		for (TextInfo text : toSave) {
			if (text instanceof WebTextInfo) {
				// in case of null
				String rowKey = String.valueOf(++crawledTextsCount % Config.getInt(Params.HBASE_REGIONSERVER_COUNT)) + "_" + text.getSaveKey();
				text.setSaveKey(rowKey);
			}
		}
		if (Config.getBoolean(Params.IF_USE_HBASE)) {
			logger.info("start save hbase..." + toSave.size());
			crawlerDAO.batchInsertText(toSave);
			logger.info("finish save hbase...");
		}
		// should save hbase first 4 svaekey changed by regionservers when saving the hbase object
		logger.info("start save mongodb...");
		crawlerDAO.batchInsertText4Index(toSave);
		logger.info(": finish mongodb!");
		// where create where clean
		toSave.clear();
		toSave = null;
		return true;
	}
}
