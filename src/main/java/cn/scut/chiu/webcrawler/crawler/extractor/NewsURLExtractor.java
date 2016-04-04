package cn.scut.chiu.webcrawler.crawler.extractor;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.scut.chiu.webcrawler.conf.Config;
import com.google.common.collect.Sets;
import org.dom4j.DocumentException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.filter.URLFilter;
import cn.scut.chiu.webcrawler.crawler.parser.GenericHTMLParser;
import cn.scut.chiu.webcrawler.crawler.parser.JSParser;

public class NewsURLExtractor extends URLExtractor {

	private static Map<String, Pattern> patterns;

	static {
		try {
			patterns = initPatterns(Config.get(Params.NEWS_URL_REGREX_FILE));
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> extractURL(URL url) {
		Set<String> ret = null;
		Elements urls = null;
		try {
			// parse the url page into dom style
			Document parsedDoc = GenericHTMLParser.parseURL(url);
			// get the urls by atttribute href with matching the regrex
			String host = url.getHost().toLowerCase();
//			System.out.println(parsedDoc);
//			System.out.println(host);
			String siteName = null;
			if (parsedDoc != null) {
				for (String siteKey : patterns.keySet()) {
					if ("common".equals(siteKey)) {
						// common is for sites not specified
						continue;
					}
					if (host.indexOf(siteKey) != -1) {
						siteName = siteKey;
						urls = parsedDoc.getElementsByAttributeValueMatching("href", patterns.get(siteKey));
						break;
					}
				}
				if (null == urls) {
					urls = parsedDoc.getElementsByAttributeValueMatching("href", patterns.get("common"));
				}
				ret = URLFilter.retainURL(urls, 1, host, siteName);
			}

			if (ret == null || ret.size() < Params.CRAWLER_URL_DETECT_LIMIT) {
				parsedDoc = JSParser.parseURL(url);
//				System.out.println(parsedDoc);
				if (parsedDoc != null) {
					for (String siteKey : patterns.keySet()) {
						if ("common".equals(siteKey)) {
							// common is for sites not specified
							continue;
						}
						if (host.indexOf(siteKey) != -1) {
							siteName = siteKey;
							urls = parsedDoc.getElementsByAttributeValueMatching("href", patterns.get(siteKey));
							break;
						}
					}
					if (null == urls) {
						urls = parsedDoc.getElementsByAttributeValueMatching("href", patterns.get("common"));
					}
					ret = URLFilter.retainURL(urls, 1, host, siteName);
				}
			}
			// filter the urls not matching a more regular rule
			if (null == ret) {
				return Sets.newHashSet();
			}
			return ret;
		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return Sets.newHashSet();
	}
}
