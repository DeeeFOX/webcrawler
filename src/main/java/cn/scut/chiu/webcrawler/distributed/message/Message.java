package cn.scut.chiu.webcrawler.distributed.message;

import java.io.UnsupportedEncodingException;

import cn.scut.chiu.util.NumberUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;

public class Message {
	
	public static int parseMessageType(byte[] message) throws UnsupportedEncodingException {
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(message, 0, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
		return NumberUtil.byteArrayToInt(messagePart);
	}

	public static int parseCommand(byte[] message) throws UnsupportedEncodingException {
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(message, 1 * Params.MESSAGE_FIELD_SIZE, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
		return NumberUtil.byteArrayToInt(messagePart);
	}
	
	public static int parseStatus(byte[] message) throws UnsupportedEncodingException {
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(message, 2 * Params.MESSAGE_FIELD_SIZE, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
		return NumberUtil.byteArrayToInt(messagePart);
	}
	
	public static int parseJobManagerId(byte[] message) {
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(message, 3 * Params.MESSAGE_FIELD_SIZE, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
		return NumberUtil.byteArrayToInt(messagePart);
	}
	
	public static String parseReqFromIp(byte[] message) {
		StringBuffer sb = new StringBuffer("");
		byte[] ipPart = new byte[Params.MESSAGE_FIELD_SIZE];
		for (int i=0; i<4; i++) {
			System.arraycopy(message, (Params.MESSAGE_IP_START_PLACE + i) * Params.MESSAGE_FIELD_SIZE, ipPart, 0, Params.MESSAGE_FIELD_SIZE);
			sb.append(NumberUtil.byteArrayToInt(ipPart) + ".");
		}
		return sb.toString().substring(0, sb.length()-1);
	}
	
	public static byte[] createRequest(int commandType, int statusType, int jobManagerId) throws UnsupportedEncodingException {
		byte[] reqMsgArr = new byte[Params.MESSAGE_REQ_FIELD_COUNT*Params.MESSAGE_FIELD_SIZE];
		byte[][] tmpRsgArr = new byte[Params.MESSAGE_REQ_FIELD_COUNT][];
		tmpRsgArr[0] = NumberUtil.intToByteArray(MessageType.REQUEST);
		tmpRsgArr[1] = NumberUtil.intToByteArray(commandType);
		tmpRsgArr[2] = NumberUtil.intToByteArray(statusType);
		tmpRsgArr[3] = NumberUtil.intToByteArray(jobManagerId);
		String[] ip = Config.getLocalMachineIp().split("\\.");
		int index = Params.MESSAGE_IP_START_PLACE;
		for (int i=0; i<ip.length; i++) {
			tmpRsgArr[index++] = NumberUtil.intToByteArray(Integer.valueOf(ip[i]));
		}
		for (int i=0; i<tmpRsgArr.length; i++) {
			System.arraycopy(tmpRsgArr[i], 0, reqMsgArr, i * Params.MESSAGE_FIELD_SIZE, Params.MESSAGE_FIELD_SIZE);
		}
		return reqMsgArr;
	}
	
	public static byte[] createResponse(byte[] message) throws UnsupportedEncodingException {
		byte[] rspMsgArr = new byte[message.length + Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(NumberUtil.intToByteArray(MessageType.RESPONSE), 0, rspMsgArr, 0, Params.MESSAGE_FIELD_SIZE);
		System.arraycopy(message, Params.MESSAGE_FIELD_SIZE, rspMsgArr, Params.MESSAGE_FIELD_SIZE, message.length - Params.MESSAGE_FIELD_SIZE);
		System.arraycopy(NumberUtil.intToByteArray(ResponseType.GET_SUCCESS), 0, rspMsgArr, message.length, Params.MESSAGE_FIELD_SIZE);
		return rspMsgArr;
	}
	
	public static int parseResponse(byte[] message) {
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		System.arraycopy(message, Params.MESSAGE_REQ_FIELD_COUNT * Params.MESSAGE_FIELD_SIZE, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
		return NumberUtil.byteArrayToInt(messagePart);
	}
	
	public static String parseMessage(byte[] message) {
		StringBuffer sb = new StringBuffer();
		byte[] messagePart = new byte[Params.MESSAGE_FIELD_SIZE];
		for (int i=0; i<message.length / Params.MESSAGE_FIELD_SIZE; i++) {
			System.arraycopy(message, i * Params.MESSAGE_FIELD_SIZE, messagePart, 0, Params.MESSAGE_FIELD_SIZE);
			sb.append(NumberUtil.byteArrayToInt(messagePart));
		}
		return sb.toString();
	}
}