package cn.scut.chiu.webcrawler.distributed.master;

import cn.scut.chiu.webcrawler.distributed.message.JobStatusType;

public class WorkerInfo {
	
	private int status;
	private int jobLoad;
	private String ip;
	private int port;
	
	public WorkerInfo() {
		status = JobStatusType.READY;
		jobLoad = 0;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
}
