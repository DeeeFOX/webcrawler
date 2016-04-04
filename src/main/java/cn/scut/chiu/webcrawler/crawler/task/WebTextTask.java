package cn.scut.chiu.webcrawler.crawler.task;

import java.io.IOException;
import java.util.LinkedList;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;

/**
 * 
 * @author chiu
 *
 */
public class WebTextTask extends Task{
	
	@Override
	public Class<? extends Task> getType() {
		// TODO Auto-generated method stub
		return this.getClass();
	}
	
	public WebTextTask() throws IOException {
		super();
	}
	
	public WebTextTask(LinkedList<WebTextURL> urlList) throws IOException {
		super(urlList);
	}	
}
