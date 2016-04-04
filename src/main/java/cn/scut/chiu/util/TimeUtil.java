package cn.scut.chiu.util;

import java.text.SimpleDateFormat;

public class TimeUtil {

	private static SimpleDateFormat sdf;
	
	static {
		sdf = new SimpleDateFormat();
	}
	
	public static String parse(String timePattern, long millseconds) {
		sdf.applyPattern(timePattern);
		return sdf.format(millseconds);
	}
	
	public static void main(String[] args) {
		System.out.println(parse("yyyy-MM-dd HH:mm:ss", 1447088463000l));
	}
}
