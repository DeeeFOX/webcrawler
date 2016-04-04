package cn.scut.chiu.webcrawler.crawler.extractor;

import cn.scut.chiu.webcrawler.conf.Params;
import com.google.common.collect.Maps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class URLExtractor {
	protected static Logger logger = LogManager.getLogger(URLExtractor.class.getName());

	public abstract Set<String> extractURL(URL url);

	protected static Map<String,Pattern> initPatterns(String filePath) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		org.dom4j.Document doc = saxReader.read(new File(filePath));
		Element hosts = doc.getRootElement().element(Params.FILTER_HOSTS);
		return buildPatterns(hosts);
	}

	protected static Map<String,Pattern> buildPatterns(Element hosts) {
		Map<String, Pattern> ret = Maps.newHashMap();
		for (Element host : hosts.elements()) {
			String hostName = host.attribute("name").getValue();
			ret.put(hostName, Pattern.compile(host.getStringValue()));
		}
		return ret;
	}
}
