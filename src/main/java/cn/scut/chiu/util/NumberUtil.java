package cn.scut.chiu.util;

import java.text.DecimalFormat;

public class NumberUtil {
	private static DecimalFormat df;
	
	static {
		df = new DecimalFormat();
	}
	
	public static String parse(String decPattern, double number) {
		df.applyPattern(decPattern);
		return df.format(number);
	}
	
	public static byte[] intToByteArray(final int number) {
		byte[] byteArray = new byte[Integer.SIZE / Byte.SIZE];
		for (int i=0; i<byteArray.length; i++) {
			byteArray[byteArray.length - 1 - i] = (byte) (number >>> (i * Byte.SIZE));
		}
		return byteArray;
	}
	
	public static int byteArrayToInt(final byte[] byteArray) {
		int value = 0;
		for (int i=0; i<byteArray.length; i++) {
			value += (byteArray[i] & 0xFF) << ((byteArray.length - 1 - i) * Byte.SIZE);
		}
		return value;
	}
	
	public static  void main(String[] args) {
		
	}
}
