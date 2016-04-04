package cn.scut.chiu.webcrawler.crawler.job;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.crawler.task.Task;
import cn.scut.chiu.webcrawler.dao.CrawlerDAO;

public abstract class Job implements Runnable {
	
	protected int jobId;
	protected ExecutorService crawlerPool;
	protected int runningCrawlerCount;
	protected boolean isFinished;
	protected Queue<Task> tasks;

	protected JobManager jobManager;
	protected String mark;
	protected CrawlerDAO crawlerDAO;
	
	public Job(int id) {
		jobId = id;
		jobManager = null;
		int core = Runtime.getRuntime().availableProcessors();
		int maxCore = core * 2;
		if (core > Config.getInt("crawler.pool.core.crawler")) {
			core = Config.getInt("crawler.pool.core.crawler");
		}
		if (maxCore > Config.getInt("crawler.pool.max.crawler")) {
			maxCore = Config.getInt("crawler.pool.max.crawler");
		}
		crawlerPool = new ThreadPoolExecutor(core, maxCore,
				60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		isFinished = false;
		tasks = null;
	}

	public void start() {
		new Thread(this).start();
	}
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	protected void informJobManager(int commandType) {
		jobManager.addLocalCommand(commandType);
		jobManager.setJobStatus(jobId, commandType);
	}
	
	protected int getJobMangerId() {
		return jobManager.getJobManagerId();
	}
	
	public int getJobId() {
		return jobId;
	}
	
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * 1) insert text info to db; 2) insert crawled info to db
	 * @param result
	 * @return
	 */
	protected abstract boolean insertResults(Map<?, ?> result);

	public void setMark(String mark) {
		this.mark = mark;
	}

	public void setDAO(CrawlerDAO crawlerDAO) {
		this.crawlerDAO = crawlerDAO;
	}
}
