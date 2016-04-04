package cn.scut.chiu.webcrawler.crawler.filter;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.crawler.TextDetector;
import cn.scut.chiu.webcrawler.crawler.object.FilterElement;
import cn.scut.chiu.webcrawler.crawler.parser.GenericHTMLParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.scut.chiu.webcrawler.conf.Params;

public class ElementsFilter {

	private static final Logger logger = LogManager.getLogger(ElementsFilter.class);
	private static Map<String, List<FilterElement>> postDelEles;
	private static Map<String, List<FilterElement>> priorDelEles;
	
	static {
		try {
			initPost();
			initPrior();
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void initPost() throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new File(Config.get(Params.CRAWLER_POST_DEL_ELE_FILE)));
		org.dom4j.Element hosts = doc.getRootElement().element(Params.FILTER_HOSTS);
		postDelEles = buildFitlerNodes(hosts);
	}

	private static void initPrior() throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new File(Config.get(Params.CRAWLER_PRIOR_DEL_ELE_FILE)));
		org.dom4j.Element hosts = doc.getRootElement().element(Params.FILTER_HOSTS);
		priorDelEles = buildFitlerNodes(hosts);
	}
	
	public static Element filterPriorDelBody(Element mainTextBody, String hostname) {
		List<FilterElement> lst = priorDelEles.get(hostname);
		if (null == lst || lst.size() == 0) {
			return mainTextBody;
		}
		return filterDelBody(mainTextBody, lst);
	}

	public static Element filterPostDelBody(Element mainTextBody, String hostname) {
		List<FilterElement> lst = postDelEles.get(hostname);
		if (null == lst || lst.size() == 0) {
			return mainTextBody;
		}
		return filterDelBody(mainTextBody, lst);
	}
	public static Element filterDelBody(Element mainTextBody, List<FilterElement> filterElements) {
		Elements eles = null;
		for (FilterElement element : filterElements) {
			if (null == element.getAttr()) {
				if ((eles = mainTextBody.getElementsByTag(element.getTag())) != null && eles.size() > 0) {
					eles.empty();
				}
			} else {
				Map.Entry<String, Map.Entry<String, String>> attr = element.getAttr();
				if ((eles = mainTextBody.getElementsByAttributeValueMatching(attr.getKey(), attr.getValue().getKey())) != null && eles.size() > 0) {
					if ("d".equals(attr.getValue().getValue())) {
						eles.empty();
					} else if (eles.text().indexOf(attr.getValue().getValue()) != -1){
						eles.empty();
					}
				}
			}
		}
		return mainTextBody;
	}

	private static Map<String, List<FilterElement>> buildFitlerNodes(org.dom4j.Element hosts) {
		Map<String, List<FilterElement>> ret = Maps.newHashMap();
		for (org.dom4j.Element host : hosts.elements()) {
			String hostName = host.attribute("name").getValue();
			List<FilterElement> lst = Lists.newLinkedList();
			for (org.dom4j.Element tag : host.elements()) {
				String tagName = tag.getName();
				List<org.dom4j.Element> attrs = tag.elements();
				if (null == attrs || attrs.isEmpty()) {
					FilterElement filterElement = new FilterElement();
					filterElement.setTag(tagName);
					lst.add(filterElement);
				} else {
					for (org.dom4j.Element attr : attrs) {
						FilterElement filterElement = new FilterElement();
						filterElement.setTag(tagName);
						String attrName = attr.getName();
						String attrValue = attr.attribute("name").getValue();
						String delValue = attr.getStringValue();
						filterElement.buildAttr(attrName, attrValue, delValue);
						lst.add(filterElement);
					}
				}
			}
			ret.put(hostName, lst);
		}
		return ret;
	}

	public static void main(String[] args) throws IOException, DocumentException, URISyntaxException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new File(Config.get(Params.CRAWLER_PRIOR_DEL_ELE_FILE)));
		org.dom4j.Element hosts = doc.getRootElement().element("hosts");
		Map<String, List<FilterElement>> map = buildFitlerNodes(hosts);
		URL url = new URL("http://maguangyuanboke.blog.news_163.com/blog/static/104690618201410491013895/");
		org.jsoup.nodes.Document document = GenericHTMLParser.parseURL(url);
		Element body = document.getElementsByTag("body").first();
//		System.out.println(body + "\n");
		if(body == null) {
			return;
		}
		String hostname = "163";
		List<FilterElement> lst = map.get(hostname);
		for (FilterElement filterElement : lst) {
			filterElement.print();
			System.out.println("\n");
		}
		Elements eles = null;
		for (FilterElement element : lst) {
			if (null == element.getAttr()) {
				if ((eles = body.getElementsByTag(element.getTag())) != null && eles.size() > 0) {
					eles.empty();
				}
			} else {
				Map.Entry<String, Map.Entry<String, String>> attr = element.getAttr();
				if ((eles = body.getElementsByAttributeValueMatching(attr.getKey(), attr.getValue().getKey())) != null && eles.size() > 0) {
					if ("d".equals(attr.getValue().getValue())) {
						eles.empty();
					} else if (eles.text().indexOf(attr.getValue().getValue()) != -1){
						eles.empty();
					}
				}
			}
		}
		System.out.println(body + "\n");
		Element mainTextBody = TextDetector.detectNewsMainText(body, 0.0, 0.0, 1);
		System.out.println(mainTextBody.html() + "\n");
		System.out.println(Double.MIN_NORMAL);
		System.out.println(Double.MIN_VALUE);
		System.out.println(Double.MAX_VALUE);
		System.out.println(-Double.MAX_VALUE);
		System.out.println(Double.MAX_EXPONENT);
		System.out.println(Double.MIN_EXPONENT);
	}
}
