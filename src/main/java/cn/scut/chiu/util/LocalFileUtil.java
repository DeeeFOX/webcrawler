package cn.scut.chiu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;

public class LocalFileUtil {

	public static BufferedWriter getAppendWriter(String path) throws IOException {
		return new BufferedWriter(new FileWriter(new File(path), true));
	}
	
	public static BufferedWriter getWriter(String path, String charset) throws FileNotFoundException, UnsupportedEncodingException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), charset));
	}

	public static BufferedReader getReader(String path, String charset) throws FileNotFoundException, UnsupportedEncodingException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
	}
	
	public static boolean remove(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		return file.delete();
	}
	
	/**
	 * get line in file into the array
	 * @param filepath
	 * @param encoding
	 * @return
	 */
	public static List<String> getList(String filepath,String charset) throws Exception{
		List<String> array = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath)), charset));
		String line = null;
		while ((line = br.readLine()) != null){
			if (line.trim().length() == 0) {
				continue;
			}
			array.add(line);
		}
		br.close();
		return array;
	}
	
	public static Set<String> getSet(String filepath, String charset) throws IOException {
		Set<String> set = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath)), charset));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().length() == 0) {
				continue;
			}
			set.add(line);
		}
		br.close();
		return set;
	}
	
	public static List<String[]> getLines(String path, String splitRegex,
			Charset charset) {
		try {
			List<String> lines = Files.readLines(new File(path), charset);
			List<String[]> ret = Lists.newLinkedList();
			for (String line : lines) {
				ret.add(line.split(splitRegex));
			}
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Lists.newArrayList();
	}
	
	public static Queue<WebTextURL> getSeedURL(String path, String charset) throws IOException {
		Queue<WebTextURL> urls = new LinkedBlockingQueue<WebTextURL>();
		BufferedReader br = getReader(path, charset);
		String seed = br.readLine();
		String[] pair = null;
		while(seed != null) {
			pair = seed.split("\t");
//			System.out.println(seed);
			urls.add(new WebTextURL(new URL(pair[0]), Integer.valueOf(pair[1]), Integer.valueOf(pair[2])));
			seed = br.readLine();
		}
		br.close();
		if (urls.size() == 0) {
			return null;
		}
		return urls;
	}

	public static Queue<WebTextURL> getSeedURLS(String[] paths, String charset) throws IOException {
		Queue<WebTextURL> urls = new LinkedBlockingQueue<WebTextURL>();
		BufferedReader br = null;
		for (String path : paths) {
			br = getReader(path, charset);
			String seed = br.readLine();
			String[] pair = null;
			while(seed != null) {
				pair = seed.split("\t");
				urls.add(new WebTextURL(new URL(pair[0]), Integer.valueOf(pair[1]), Integer.valueOf(pair[2])));
				seed = br.readLine();
			}
			br.close();
			if (urls.size() == 0) {
				return null;
			}
		}
		return urls;
	}

	public static Queue<WebTextURL> getNewsURL(String path, String charset) throws IOException {
		Queue<WebTextURL> urls = new LinkedBlockingQueue<WebTextURL>();
		BufferedReader br = getReader(path, charset);
		String urlStr = null;
		while((urlStr = br.readLine()) != null) {
			if (!urlStr.startsWith("http://") || urlStr.contains("blog")) {
				continue;
			}
			urls.add(new WebTextURL(new URL(urlStr), 1, 1));
		}
		br.close();
		if (urls.size() == 0) {
			return null;
		}
		return urls;
	}
	
	public static Queue<WebTextURL> getBlogsURL(String path, String charset) throws IOException {
		Queue<WebTextURL> urls = new LinkedBlockingQueue<WebTextURL>();
		BufferedReader br = getReader(path, charset);
		String urlStr = null;
		while((urlStr = br.readLine()) != null) {
			if (!urlStr.startsWith("http://")) {
				continue;
			}
			urls.add(new WebTextURL(new URL(urlStr), 1, 2));
		}
		br.close();
		if (urls.size() == 0) {
			return null;
		}
		return urls;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		for(int i=0; i<9; i++) {
//			BufferedWriter bw = getAppendWriter(Params.LOCAL_NEW_DETECT_PATH);
//			bw.append(i+"\n");
//			bw.flush();
//			bw.close();
//		}
//		for(int i=0; i<20; i++) {
//			BufferedReader br = getReader(Params.LOCAL_SEED_PATH);
//			br.close();
//		}
//		Thread.sleep(500000);
//		LocalFileUtil.getNewsURL(Params.LOCAL_SEED_PATH);
	}
}
