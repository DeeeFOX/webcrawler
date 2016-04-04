package cn.scut.chiu.webcrawler.crawler.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.scut.chiu.webcrawler.dao.CrawlerDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.util.NumberUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.distributed.SelectorOp;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;
import cn.scut.chiu.webcrawler.distributed.message.JobStatusType;
import cn.scut.chiu.webcrawler.distributed.message.Message;
import cn.scut.chiu.webcrawler.distributed.message.MessageType;

public class JobManager implements Runnable, SelectorOp{

	private static Logger logger = LogManager.getLogger(JobManager.class.getName());
	
	private int jobManagerId;
	private volatile Integer jobManagerStatus;
	private Queue<Integer> commandReceived;
	private volatile Integer jobCount;
	private Map<Integer, Integer> jobIdtoStatus;

	private Selector selector;
	private Map<SocketChannel, byte[]> keepDataTrack;
	private ByteBuffer buffer;
	private CrawlerDAO crawlerDAO;
	
	public JobManager(int jobManagerId, Selector selector) throws IOException {
		this.jobManagerId = jobManagerId;
		commandReceived = new LinkedBlockingQueue<Integer>();
		initIO(selector);
		initJobInfo();
	}
	
	private void initIO(Selector selector) throws IOException {
		this.selector = selector;
		buffer = ByteBuffer.allocate(Config.getInt("buffer.rw.size"));
		keepDataTrack = new HashMap<SocketChannel, byte[]>();
	}
	
	private void initJobInfo() {
		jobCount = 0;
		jobIdtoStatus = new HashMap<Integer, Integer>();
	}

	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// sleep for a while
		// to prevent visiting a web site too often to be blocked
		try {
			Thread.sleep(System.currentTimeMillis()%Params.CLUSTER_TIME_INTERVAL_HALF_MIN);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobManagerStatus = JobStatusType.READY;
		while(!shouldStop() || keepDataTrack.size()!=0) {
			if(commandReceived.size() == 0) {
				selectOp();
				continue;
			}
			try {
				doCommandJob(NumberUtil.intToByteArray(commandReceived.poll()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		destruct();
	}

	@Override
	public void doCommandJob(byte[] message)
			throws UnsupportedEncodingException, IOException {
		Job job = null;
		int commandType = NumberUtil.byteArrayToInt(message);
		switch (commandType) {
		case CommandType.WEBTEXT_DETECT:
			job = JobFactory.initJob(CommandType.WEBTEXT_DETECT);
			job.setJobManager(this);
			increaseJobCount();
			jobIdtoStatus.put(job.getJobId(), CommandType.WEBTEXT_DETECT);
			setJobManagerStatus(JobStatusType.BUSY_DETECTING);
			job.setMark(jobManagerId+"_"+job.getJobId());
			job.start();
			break;
		case CommandType.DECTECT_DONE:
//			CrawlerHistory.reNewDectectedURLs();	// this has been removed and replaced by once a day
			doConnectJob(Config.getLocalMachineIp(),
					Config.getInt("worker.accept.port"),
					Message.createRequest(CommandType.DECTECT_DONE, 0, getJobManagerId()));
			break;
		case CommandType.WEBTEXT_CRAWL:
			job = JobFactory.initJob(CommandType.WEBTEXT_CRAWL);
			job.setJobManager(this);
			job.setDAO(crawlerDAO);
			int detect = -1;
			for (int jobId : jobIdtoStatus.keySet()) {
				if (jobIdtoStatus.get(jobId) == CommandType.DECTECT_DONE) {
					detect = jobId;
					break;
				}
			}
			jobIdtoStatus.remove(detect);
			job.setMark(jobManagerId+"_"+detect);
			job.start();
			break;
		case CommandType.CRAWL_DONE:
			decreaseJobCount();
			doConnectJob(Config.getLocalMachineIp(),
					Config.getInt("worker.accept.port"),
					Message.createRequest(CommandType.CRAWL_DONE, 0, getJobManagerId()));
			break;
		}
	}
	
	@Override
	public void selectOp() {
		try {
			if(selector.selectNow()<=0) {
				return;
			}
			Iterator<SelectionKey> its = selector.selectedKeys().iterator();
			while(its.hasNext()) {
				SelectionKey key = its.next();
				// prevent the same key from coming up
				its.remove();
				if (!key.isValid()) {
					continue;
				}
				if(key.isConnectable()) {
					connectOp(key);
				} else if(key.isReadable()) {
					readOp(key);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void readOp(SelectionKey key) throws IOException {
		SocketChannel tmpSocket = (SocketChannel) key.channel();
		buffer.clear();
		int numRead = -1;
		try {
			numRead = tmpSocket.read(buffer);
		} catch (IOException e) {
			logger.error("cannot read");
		}
		if (numRead == -1) {
			logger.error("connection closed by: " + tmpSocket.getRemoteAddress());
			tmpSocket.close();
			key.cancel();
			return;
		}
		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);
		doMessageJob(key, tmpSocket, data);
	}
	

	@Override
	public void doMessageJob(SelectionKey key, SocketChannel tmpSocket,
			byte[] message) throws IOException {
		switch (Message.parseMessageType(message)) {
		case MessageType.RESPONSE:
			logger.info("Worker's responses \"" + Message.parseCommand(message) + Message.parseResponse(message) + "\"");
			tmpSocket.close();
			key.cancel();
			keepDataTrack.remove(tmpSocket);
//			logger.info(keepDataTrack);
			break;
		}
	}
	
	@Override
	public void connectOp(SelectionKey key) throws IOException {
		SocketChannel tmpSocket = (SocketChannel) key.channel();
		if(!tmpSocket.finishConnect()) {
			return;
		}
		tmpSocket.write(ByteBuffer.wrap(keepDataTrack.get(tmpSocket)));
		key.interestOps(SelectionKey.OP_READ);	
	}
	

	@Override
	public void doConnectJob(String ip, int port, byte[] data)
			throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.socket().setReuseAddress(true);
		socketChannel.configureBlocking(false);
		socketChannel.bind(null);
		socketChannel.connect(new InetSocketAddress(ip, port));
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		keepDataTrack.put(socketChannel, data);
	}
	
	public void setJobManagerStatus(int status) {
		synchronized (jobManagerStatus) {
			jobManagerStatus = status;
		}
	}
	
	public int getJobManagerStatus() {
		return jobManagerStatus;
	}
	
	public void addLocalCommand(int command) {
		synchronized (commandReceived) {
			commandReceived.add(command);
		}
	}
	
	private boolean shouldStop() {
		synchronized (jobManagerStatus) {
			return jobManagerStatus == JobStatusType.STOP ? true : false;
		}
	}
	
	public int getJobManagerId() {
		return jobManagerId;
	}
	
	private void increaseJobCount() {
		synchronized (jobCount) {
			jobCount ++;
		}
	}

	private void decreaseJobCount() {
		synchronized (jobCount) {
			jobCount --;
		}
	}
	
	public int getJobCount() {
		synchronized (jobCount) {
			return jobCount;
		}
	}
	
	public void setJobStatus(int jobId, int commandType) {
		jobIdtoStatus.put(jobId, commandType);
	}

	public void setDAO(CrawlerDAO crawlerDAO) {
		this.crawlerDAO = crawlerDAO;
	}
	
	private void destruct() {
		logger.info("Job manager " + jobManagerId + " destruct");
		try {
			selector.selectNow();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buffer = null;
	}

	@Deprecated
	@Override
	public void acceptOp(SelectionKey key) throws IOException {
	}

	@Deprecated
	@Override
	public void writeOp(SelectionKey key) throws UnsupportedEncodingException,
			IOException {
	}
}
