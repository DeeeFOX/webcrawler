package cn.scut.chiu.webcrawler.crawler.job;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;

public class JobFactory {

	private volatile static Integer jobIdCount = 0;
	
	public static Job initJob(int commandType) {
		Job job = null;
		synchronized (jobIdCount) {
			switch (commandType) {
			case CommandType.WEBTEXT_DETECT:
				job = new SeedCrawlJob(getJobId());
				break;
			case CommandType.WEBTEXT_CRAWL:
				job = new WebTextCrawlJob(getJobId());
				break;
			}
		}
		return job;
	}

	private static int getJobId() {
		jobIdCount %= Params.JOB_ID_RANGE;
		if (jobIdCount == 0) {
			jobIdCount++;
		}
		return jobIdCount++;
	}
}
