package cn.scut.chiu.webcrawler.crawler.task;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;

public abstract class Task {
	
	protected int taskId;
	protected volatile LinkedList<WebTextURL> toDoUrlList;
	protected volatile LinkedList<URL> doneUrlList;
	protected volatile boolean isDone;
	
	public Task() throws IOException {
		toDoUrlList = new LinkedList<WebTextURL>();
		doneUrlList = new LinkedList<URL>();
		isDone = false;
	}
	
	public Task(LinkedList<WebTextURL> urlList) throws IOException {
		toDoUrlList = new LinkedList<WebTextURL>();
		doneUrlList = new LinkedList<URL>();
		for(WebTextURL url : urlList) {
			toDoUrlList.add(url);
		}
		isDone = false;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public synchronized int sizeOfToDoUrlList() {
		return toDoUrlList.size();
	}
	
	public synchronized WebTextURL getToDoUrl(int index) {
		return toDoUrlList.get(index);
	}
	public synchronized boolean addToDoUrl(WebTextURL url) {
		return toDoUrlList.add(url);
	}
	
	public synchronized WebTextURL pollToDoUrl() {
		return toDoUrlList.poll();
	}
	
	public synchronized boolean removeToDoUrl(URL url) {
		return toDoUrlList.remove(url);
	}
	
	public synchronized boolean isContainToDo(URL url) {
		return toDoUrlList.contains(url);
	}
	
	public synchronized boolean isToDoEmpty() {
		return toDoUrlList.isEmpty();
	}
	
	public synchronized void setTaskDone() {
		isDone = true;
	}
	
	public boolean isTaskDone() {
		return isDone;
	}
	
	public abstract Class<? extends Task> getType();
}
