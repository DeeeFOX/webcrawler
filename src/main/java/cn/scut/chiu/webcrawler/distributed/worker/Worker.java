package cn.scut.chiu.webcrawler.distributed.worker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.dao.CrawlerDAO;
import cn.scut.chiu.webcrawler.dao.impl.CrawlerDAOImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.job.JobManager;
import cn.scut.chiu.webcrawler.distributed.NodeBase;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;
import cn.scut.chiu.webcrawler.distributed.message.JobStatusType;
import cn.scut.chiu.webcrawler.distributed.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Worker is for monitoring worker node's status and communicate with master
 * 
 * @author Chiu
 *
 */
public class Worker extends NodeBase implements Runnable {
	
	private static Logger logger = LogManager.getLogger(Worker.class.getName());
	private String masterIp;
	private int masterAcceptPort;
	
	private int jobManagerIdCount;
	private Map<Integer, JobManager> keepJobManagerTrack;
	private Selector localSelector;
	private CrawlerDAO crawlerDAO;
	public Worker() throws NumberFormatException, IOException {
		super();
		initWorkerIO();
		initWorkerInfo();
		initMasterInfo();
		initJobManagersInfo();
	}

	private void initWorkerIO() throws IOException {
		localSelector = Selector.open();
	}

	private void initWorkerInfo() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(Config.get(Params.MONGO_APPLICATION_CONTEXT_FILE));
		crawlerDAO = (CrawlerDAOImpl)ctx.getBean("crawlerDAOImpl");
	}

	private void initMasterInfo() {
		masterIp = Config.get("master.ip");
		masterAcceptPort = Config.getInt("master.accept.port");
	}

	private void initJobManagersInfo() {
		keepJobManagerTrack = new HashMap<Integer, JobManager>();
		jobManagerIdCount = 0;
	}

	public void start() {
		status = JobStatusType.READY;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		status = JobStatusType.READY_TO_CRAWL;
		while (!shouldStop()) {
			selectOp();
		}
	}

	/**
	 * get job done base by command type
	 * 
	 * @param message
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@Override
	public void doCommandJob(byte[] message) throws UnsupportedEncodingException, IOException {
		JobManager jobManager = null;
		int commandType = Message.parseCommand(message);
		switch(commandType) {
		case CommandType.WEBTEXT_DETECT:
			jobManager = new JobManager(getJobManagerId(), localSelector);
			jobManager.addLocalCommand(CommandType.WEBTEXT_DETECT);
			jobManager.setDAO(crawlerDAO);
			keepJobManagerTrack.put(jobManager.getJobManagerId(), jobManager);
			jobManager.start();
			status = JobStatusType.BUSY_DETECTING;
			break;
		case CommandType.DECTECT_DONE:
			jobManager = keepJobManagerTrack.get(Message.parseJobManagerId(message));
			if (jobManager == null) {
				logger.error("can not find job manager by message \"" + Message.parseMessage(message) + "\"");
				return;
			}
			jobManager.addLocalCommand(CommandType.WEBTEXT_CRAWL);
			break;
		case CommandType.CRAWL_DONE:
			int jobManagerId = Message.parseJobManagerId(message);
			jobManager = keepJobManagerTrack.get(jobManagerId);
			if (jobManager == null) {
				logger.error("can not found job manager by message \"" + Message.parseMessage(message) + "\"");
				return;
			}
			if(jobManager.getJobCount() == 0) {
				jobManager.setJobManagerStatus(JobStatusType.STOP);
				keepJobManagerTrack.remove(jobManagerId);
				if(keepJobManagerTrack.isEmpty()) {
					status = JobStatusType.READY_TO_CRAWL;
				}
			}
			doConnectJob(masterIp, masterAcceptPort, Message.createRequest(CommandType.CRAWL_DONE, getStatus(), 0));
			break;
		default:
			logger.error("unknown command \"" + commandType + "\" from message \"" + Message.parseMessage(message) + "\"");
			break;
		}
		
	}
	
	private int getJobManagerId() {
		jobManagerIdCount %= Params.JOB_MANAGER_ID_RANGE;
		if (jobManagerIdCount == 0) {
			jobManagerIdCount++;
		}
		return jobManagerIdCount++;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		new Worker().start();
	}
}