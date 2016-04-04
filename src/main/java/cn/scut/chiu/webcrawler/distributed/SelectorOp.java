package cn.scut.chiu.webcrawler.distributed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract interface SelectorOp {
	abstract void selectOp();
	abstract void acceptOp(SelectionKey key) throws IOException;
	abstract void readOp(SelectionKey key) throws IOException;
	abstract void writeOp(SelectionKey key) throws UnsupportedEncodingException, IOException;
	abstract void connectOp(SelectionKey key) throws UnsupportedEncodingException, IOException;
	
	abstract void doMessageJob(SelectionKey key, SocketChannel tmpSocket, byte[] data) throws IOException;
	abstract void doCommandJob(byte[] message) throws UnsupportedEncodingException, IOException;
	abstract void doConnectJob(String ip, int port, byte[] data) throws IOException;
}
