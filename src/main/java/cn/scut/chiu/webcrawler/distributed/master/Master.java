package cn.scut.chiu.webcrawler.distributed.master;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.scut.chiu.webcrawler.conf.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.distributed.NodeBase;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;
import cn.scut.chiu.webcrawler.distributed.message.JobStatusType;
import cn.scut.chiu.webcrawler.distributed.message.Message;

public class Master extends NodeBase implements Runnable {
	
	private static Logger logger = LogManager.getLogger(Master.class.getName());
	private Map<String, WorkerInfo> workersInfo;
	
	public Master() throws NumberFormatException, UnknownHostException, IOException {
		super();
		initWorkersInfo();
	}
	
	private void initWorkersInfo() {
		workersInfo = new HashMap<String, WorkerInfo>();
		String[] workersIp = Config.getWorkersIpGroup();
		for (int i=0; i<workersIp.length; i++) {
			WorkerInfo workerInfo = new WorkerInfo();
			workerInfo.setIp(workersIp[i]);
			workerInfo.setPort(Config.getInt("worker.accept.port"));
			workersInfo.put(workersIp[i], workerInfo);
		}
	}
	
	public void start() {
		status = JobStatusType.READY;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		status = JobStatusType.READY;
		try {
			tmpMethod_BroadcastWorker();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (!this.shouldStop()) {
			selectOp();
		}
		
	}

	private void tmpMethod_BroadcastWorker() throws IOException {
		for(String workerIp : workersInfo.keySet()) {
			WorkerInfo workerInfo = workersInfo.get(workerIp);
			doConnectJob(workerInfo.getIp(), workerInfo.getPort(),
					Message.createRequest(CommandType.WEBTEXT_DETECT, getStatus(), 0));
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
	public void doCommandJob(byte[] message) throws IOException {
		int commandType = Message.parseCommand(message);
		switch(commandType) {
		case CommandType.STATUS_REPORT:
			break;
		case CommandType.CRAWL_DONE:
			WorkerInfo workerInfo = workersInfo.get(Message.parseReqFromIp(message));
			logger.info("worker(" + Message.parseReqFromIp(message) + "): \"crawl job done at "+ new SimpleDateFormat("MM/dd-HH:mm:ss").format(new Date()) +"\"");
			workerInfo.setStatus(Message.parseStatus(message));
//			if(tmpCrawlTimes != -1 && tmpCrawlTimes > Params.MAX_CRAWL_TIME) {
//				return;
//			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doConnectJob(workerInfo.getIp(), workerInfo.getPort(), Message.createRequest(CommandType.WEBTEXT_DETECT, getStatus(), 0));
			break;
		default:
			logger.error("unknown command \"" + commandType + "\" from message \"" + Message.parseMessage(message) + "\"");
			break;
		}
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		Master master = new Master();
		master.start();
	}
}
