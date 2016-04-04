package cn.scut.chiu.webcrawler.distributed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.distributed.message.JobStatusType;
import cn.scut.chiu.webcrawler.distributed.message.Message;
import cn.scut.chiu.webcrawler.distributed.message.MessageType;

public abstract class NodeBase implements SelectorOp{
	
	private static Logger logger = LogManager.getLogger(NodeBase.class.getName());
	protected int status;
	
	protected String localIp;
	protected int localAcceptPort;
	protected Selector selector;
	protected Selector localSelector;
	protected ServerSocketChannel server;
	// keep socket data track
	protected Map<SocketChannel, List<byte[]>> keepDataTrack;
	protected ByteBuffer buffer;
	
	public NodeBase() throws IOException {
		initIO();
	}
	
	/**
	 * Init ServerSocketChannel and Selector instance
	 * 
	 * @throws IOException
	 */
	private void initIO() throws IOException {
		localIp = Config.getLocalMachineIp();
		localAcceptPort = Config.getInt(getNodeType() + ".accept.port");
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.socket().setReuseAddress(true);
		server.bind(new InetSocketAddress(localIp, localAcceptPort));
		logger.info(getNodeType() + ": server bind ip: " + localIp + " port: " + localAcceptPort);
		selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);
		keepDataTrack = new HashMap<SocketChannel, List<byte[]>>();
		buffer = ByteBuffer.allocate(Config.getInt("buffer.rw.size"));
	}
	
	/**
	 * select for operation based on Selector instance
	 */
	@Override
	public void selectOp() {
		try {
			if (selector.selectNow() <=0 ) {
				return;
			}
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = keys.next();
				// prevent the same key from coming up
				keys.remove();
				if (!key.isValid()) {
					continue;
				}
				if (key.isAcceptable()) {
					acceptOp(key);
				} else if (key.isReadable()) {
					readOp(key);
				} else if (key.isWritable()) {
					writeOp(key);
				} else if (key.isConnectable()) {
					connectOp(key);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * when accept happen it is to say that the master connect the worker
	 * accept it and wait for read
	 * 
	 * @param key
	 * @throws IOException
	 */
	@Override
	public void acceptOp(SelectionKey key) throws IOException {
		ServerSocketChannel tmpServer = (ServerSocketChannel) key.channel();
		SocketChannel tmpSocketChannel = tmpServer.accept();
		tmpSocketChannel.configureBlocking(false);
		tmpSocketChannel.socket().setReuseAddress(true);
		logger.info(getNodeType() + ": incoming connection from: " + tmpSocketChannel.getRemoteAddress());
		keepDataTrack.put(tmpSocketChannel, new ArrayList<byte[]>());
		// then waiting for read
		tmpSocketChannel.register(selector, SelectionKey.OP_READ);
	}

	/**
	 * when read happen it is to say that the other side write the worker
	 * read it and wait for write (when the other side read)
	 * 
	 * @param key
	 * @throws IOException 
	 */
	@Override
	public void readOp(SelectionKey key) throws IOException {
		SocketChannel tmpSocket = (SocketChannel) key.channel();
		buffer.clear();
		int numRead = -1;
		try {
			numRead = tmpSocket.read(buffer);
		} catch (IOException e) {
			logger.error(getNodeType() + ": cannot read error");
		}
		if(numRead == -1) {
			keepDataTrack.remove(tmpSocket);
			logger.error(getNodeType() + ": connection closed by: " + tmpSocket.getRemoteAddress());
			tmpSocket.close();
			key.cancel();
			return;
		}
		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);
		doMessageJob(key, tmpSocket, data);
	}
	
	/**
	 * 
	 * @param key
	 * @param tmpSocket
	 * @param data
	 * @throws IOException
	 */
	@Override
	public void doMessageJob(SelectionKey key, SocketChannel tmpSocket, byte[] message) throws IOException {
		switch (Message.parseMessageType(message)) {
		case MessageType.REQUEST:
			keepDataTrack.get(tmpSocket).add(message);
			// then wait for write(the other side read)
			key.interestOps(SelectionKey.OP_WRITE);
			break;
		case MessageType.RESPONSE:
			logger.info(tmpSocket.getRemoteAddress() + "'s responses \"" + Message.parseResponse(message) + "\"");
			tmpSocket.close();
			key.cancel();
			keepDataTrack.remove(tmpSocket);
			break;
		}
	}
	
	/**
	 * when write happen it is to say that the other side is waiting for reading from the worker
	 * write it and do the next step depend on the data that receive before
	 * 
	 * @param key
	 * @throws IOException 
	 */
	@Override
	public void writeOp(SelectionKey key) throws UnsupportedEncodingException, IOException {
		SocketChannel tmpSocket = (SocketChannel) key.channel();
		List<byte[]> channelData = keepDataTrack.get(tmpSocket);
		Iterator<byte[]> it = channelData.iterator();
		while (it.hasNext()) {
			byte[] message = it.next();
			it.remove();
			tmpSocket.write(ByteBuffer.wrap(Message.createResponse(message)));
			doCommandJob(message);
		}
		keepDataTrack.remove(tmpSocket);
		key.cancel();
		tmpSocket.close();
	}
	
	@Override
	public void doConnectJob(String ip, int port, byte[] data) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.socket().setReuseAddress(true);
		socketChannel.bind(null);
		socketChannel.connect(new InetSocketAddress(ip, port));
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		List<byte[]> channelData = new ArrayList<byte[]>();
		channelData.add(data);
		keepDataTrack.put(socketChannel, channelData);
	}
	
	/**
	 * when connect happen it is to say that the other side is accept for connecting from the worker
	 * connect it and write a message to the other side, here the other side specifies the master
	 * 
	 * @param key
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@Override
	public void connectOp(SelectionKey key) throws UnsupportedEncodingException, IOException {
		SocketChannel tmpSocket = (SocketChannel) key.channel();
		if (!tmpSocket.finishConnect()) {
			return;
		}
		List<byte[]> channelData = keepDataTrack.get(tmpSocket);
		Iterator<byte[]> it = channelData.iterator();
		while (it.hasNext()) {
			byte[] data = it.next();
			it.remove();
			tmpSocket.write(ByteBuffer.wrap(data));
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	
	protected int getStatus() {
		return status;
	}
	
	protected boolean shouldStop() {
		return status == JobStatusType.STOP ? true:false;
	}
	
	public void setStop() {
		status = JobStatusType.STOP;
	}
	
	public String getNodeType() {
		return getClass().getSimpleName().toLowerCase();
	}
}
