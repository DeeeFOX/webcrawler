package cn.scut.chiu.webcrawler.distributed.message;

public class JobStatusType {
	
	public static final int STOP = 1;
	public static final int READY = 2;
	public static final int READY_TO_CLUSTER = 3;
	public static final int BUSY_CLUSTERING = 4;
	public static final int READY_TO_COMBINE = 5;
	public static final int BUSY_COMBINING = 6;
	public static final int READY_TO_CRAWL = 7;
	public static final int BUSY_DETECTING = 8;
}
