package cn.scut.chiu.webcrawler.crawler.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import cn.scut.chiu.webcrawler.conf.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.scut.chiu.webcrawler.conf.Params;

/**
 * Generic HTML parser
 * 
 * @author chiu
 *
 */
public class GenericHTMLParser {

	private static Logger logger = LogManager.getLogger(GenericHTMLParser.class.getName());
	/**
	 * 
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 */
	public static Document parseURL(URL url) throws URISyntaxException {
		try {
//			System.out.println(url.toString());
			Document doc = Jsoup.connect(url.toString()).timeout(Params.READ_TIMEOUT).get();
//			System.out.println(doc.html());
//			Document doc = Jsoup.parse(Jsoup.parse(url, Params.READ_TIMEOUT).html().replaceAll("&lt;", "<").replaceAll("&gt;", ">"), "http://"+url.getHost());
			doc.select(Config.get(Params.CRAWLER_DOM_REMOVE_ELE)).remove();
			return doc;
		} catch (IOException e) {
			logger.error("url(" + url.toString() + ")get time out: " + e.getMessage());
//			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
		System.out.println(GenericHTMLParser.parseURL(new URL("http://www.ifanr.com")));
	}
}
