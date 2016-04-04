package cn.scut.chiu.webcrawler.crawler;

import cn.scut.chiu.webcrawler.crawler.object.StatisticsAdaptor;
import cn.scut.chiu.webcrawler.crawler.task.Task;


public abstract class CrawlerBase {

	protected int crawlerId;
	protected volatile Task crawlerTask;
	protected StatisticsAdaptor statisticsAdaptor;

	public CrawlerBase(Task task) {
		crawlerTask = task;
		statisticsAdaptor = new StatisticsAdaptor();
	}

	public int getCrawlerId() {
		return crawlerId;
	}
	
	public Task getCrawlerTask() {
		return crawlerTask;
	}
	
	public void setTask(Task task) {
		crawlerTask = task;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
