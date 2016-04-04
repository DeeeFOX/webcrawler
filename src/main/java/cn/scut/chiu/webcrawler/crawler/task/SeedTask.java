package cn.scut.chiu.webcrawler.crawler.task;

import java.io.IOException;
import java.util.LinkedList;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;

public class SeedTask extends Task {
	
	public SeedTask() throws IOException {
		super();
	}
	
	public SeedTask(LinkedList<WebTextURL> urlList) throws IOException {
		super(urlList);
	}

	@Override
	public Class<? extends Task> getType() {
		// TODO Auto-generated method stub
		return this.getClass();
	}
}
