package cn.scut.chiu.webcrawler.crawler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.distributed.message.CommandType;

public class CrawlerHistory {

//	public static final Object REFRESH = new Object();
//
//	private static Set<String> detectedURLs;
//	private static Set<String> newlyDetectURLs;
//	private static Config config;
	private static Map<String, Set<WebTextURL>> toCrawlURLs;
//
	static {
//		initDetectedURLs();
//		newlyDetectURLs = new HashSet<String>();
//		config = new Config();
		toCrawlURLs = new HashMap<String, Set<WebTextURL>>();
	}
//
//	private static void initDetectedURLs() {
//		detectedURLs = new HashSet<String>();
//		try {
//			BufferedReader br = LocalFileUtil.getReader(Config.get(Params.CRAWLER_HISTORY_DETECTED_FILE), Params.CHARSET_UTF8);
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				if (line.trim().length() == 0) {
//					continue;
//				}
//				detectedURLs.add(line);
//			}
//		} catch ( IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	public static boolean addNewlyDetected(String urlStr) {
//		return newlyDetectURLs.add(urlStr);
//	}
//
//	public static boolean addHasDetected(String urlStr) {
//		return detectedURLs.add(urlStr);
//	}
//
//	public static boolean hasDetected(String urlStr) {
//		return detectedURLs.contains(urlStr);
//	}
//
//	public synchronized static void reNewDectectedURLs() {
//		saveDetectedURLs();
//		detectedURLs.clear();
//		detectedURLs = newlyDetectURLs;
//		newlyDetectURLs = new HashSet<String>();
//		System.gc();
//	}
//
//	private static void saveDetectedURLs() {
//		try {
//			BufferedWriter bw = LocalFileUtil.getWriter(Params.CRAWLER_HISTORY_DETECTED_FILE, Params.CHARSET_UTF8);
//			for (String urlStr : detectedURLs) {
//				bw.write(urlStr + "\n");
//			}
//			bw.flush();
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
	public static void addToCrawlURLs(String mark, WebTextURL url) {
		if (!toCrawlURLs.containsKey(mark)) {
			toCrawlURLs.put(mark, new HashSet<WebTextURL>());
		}
		toCrawlURLs.get(mark).add(url);
	}
//
//	public static void putToCrawlURLs(String mark, Set<WebTextURL> tmpToCrawlURLs) {
//		toCrawlURLs.put(mark, tmpToCrawlURLs);
//	}
//
	public static Queue<WebTextURL> getWebTextURL(int commandType, String mark) throws IOException {
		switch (commandType) {
		case CommandType.WEBTEXT_DETECT:
			return LocalFileUtil.getSeedURLS(Config.get(Params.CONF_KEY_SEED_NAME).split(Params.SPLIT_COMMA), Params.CHARSET_UTF8);
		case CommandType.WEBTEXT_CRAWL:
			Set<WebTextURL> urls = toCrawlURLs.get(mark);
			if (urls == null) {
				return null;
			}
			Queue<WebTextURL> ret = new LinkedBlockingQueue<WebTextURL>();
			ret.addAll(urls);
			toCrawlURLs.remove(mark);
			urls.clear();
			urls = null;
			return ret;
		}
		return null;
	}
}
