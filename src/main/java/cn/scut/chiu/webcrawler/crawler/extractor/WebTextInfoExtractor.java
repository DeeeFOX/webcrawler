package cn.scut.chiu.webcrawler.crawler.extractor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.filter.TitleFilter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.TextDetector;
import cn.scut.chiu.webcrawler.crawler.filter.ElementsFilter;
import cn.scut.chiu.webcrawler.crawler.filter.ContentFilter;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.crawler.parser.GenericHTMLParser;

public class WebTextInfoExtractor {

	public WebTextInfoExtractor() {
		
	}
	
	public WebTextInfo extractWebText(WebTextURL url) {
		try {
			Document parsedDoc = GenericHTMLParser.parseURL(url.getURL());
//			System.out.println(parsedDoc.html());
			String title = extractTitle(parsedDoc, url);
			if (title == null || title.indexOf(Params.PAGE_NOT_FOUND) != -1) {
				return null;
			}
			String mainText = extractMainText(parsedDoc, url);
//			System.out.println("-->" + mainText.replaceAll("", "")+ "<--");
			if(mainText == null || mainText.length() <= Config.getInt(Params.MIN_VALID_MAINTEXT_LEN)) {
				return null;
			}
			WebTextInfo newsInfo = new WebTextInfo(url.toString());
			newsInfo.setTitle(title);
			newsInfo.setMainText(mainText);
			newsInfo.setCrawlTime(System.currentTimeMillis());
			newsInfo.setTextType(url.getTextType());
			newsInfo.setCategory(url.getCategory());
//			newsInfo.printXML();
			return newsInfo;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Extract the titile from the page that has been convert to the document
	 * 
	 * @param parsedDoc
	 * @return
	 */
	public String extractTitle(Document parsedDoc, WebTextURL url) {
		if(parsedDoc != null) {
			String titleTagText = "";
			Elements heads = parsedDoc.getElementsByTag("head");
			if (null != heads && heads.size() != 0) {
				titleTagText = heads.first().text().trim();
			}
			if("".equals(titleTagText)) {
				Element titleEle = parsedDoc.getElementsByTag("body").first().getElementsByTag("title").first();
				if(titleEle != null) {
					titleTagText = titleEle.text().trim();
				} else {
					// no body nor head
					return null;
				}
			}
			if (ContentFilter.filterByTitle(titleTagText)) {
				return null;
			}
			String ret = TitleFilter.filterNoise(titleTagText, url.getTextType(), url.getHostSite());
			return ret;
		} else {
			// no html page info
			return null;
		}
	}
	
	/**
	 * 
	 * Extract the main text from the page that has been convert to the document
	 * 
	 * @param parsedDoc
	 * @return
	 */
	public String extractMainText(Document parsedDoc, WebTextURL webTextURL) {
		Element body = parsedDoc.getElementsByTag("body").first();
		if(body == null) {
			return null;
		}
		String hostname = webTextURL.getHostSite();
		body = ElementsFilter.filterPriorDelBody(body, hostname);
//		System.out.println(body.html());
		Element mainTextBody = TextDetector.detectNewsMainText(body, 0.0, 0.0, 1);
//		System.out.println(mainTextBody.html());
		mainTextBody = ElementsFilter.filterPostDelBody(mainTextBody, hostname);
//		System.out.println(mainTextBody.html());
		if(mainTextBody == null) {
			return null;
		}
		
		Elements ps = mainTextBody.getElementsByTag("p");
		ps.addAll(mainTextBody.getElementsByTag("h1"));
		ps.addAll(mainTextBody.getElementsByTag("b"));
//		System.out.println("maintext text:" + mainTextBody.text());
		if (ps.size() <= 2 && webTextURL.getTextType() == 2) {
			ps.addAll(mainTextBody.getElementsByTag("div"));
		}
		if(ps.size() <= 0) {
			return ContentFilter.filterDisturbance(mainTextBody.text()).trim();
		}
		StringBuffer mainText = new StringBuffer("");
		Elements imgs = mainTextBody.getElementsByTag("img");
		for(Element p : ps) {
			if(p.getElementsByTag("a").size() == p.childNodeSize()) {
				// itself, continue
				continue;
			}
			mainText.append(p.text().trim() + "\n");
//			Elements p_imgs = p.getElementsByTag("img");
//			if(p_imgs != null) {
//				for(Element p_img : p_imgs) {
//					mainText.append("\t<img>" + p_img.attr("src") + ")" + "</img>\n");
//					imgs.remove(p_img);
//				}
//			}
		}
		if (mainText.toString().trim().length() == 0) {
			mainText.replace(0, mainText.length(), mainTextBody.text());
		}
		return ContentFilter.filterDisturbance(mainText.toString()).trim();
	}

	public static void main(String[] args) throws MalformedURLException {
//		new NewsInfoExtractor().extractNews(new NewsURL(new URL("http://legal.people.com.cn/n/2014/0901/c188502-25581632.html"), 1)).printXML();
//		new NewsInfoExtractor().extractNews(new NewsURL(new URL("http://culture.people.com.cn/n/2014/0902/c22219-25586431.html"), 1)).printXML();
//		new NewsInfoExtractor().extractNews(new NewsURL(new URL("http://games.sina.com.cn/j/n/2014-09-20/1028817276.shtml"), 1)).printXML();
//		new NewsInfoExtractor().extractNews(new NewsURL(new URL("http://games.sina.com.cn/g/n/2014-09-19/1028816496.shtml"), 1)).printXML();
//		new NewsInfoExtractor().extractN-ews(new NewsURL(new URL("http://games.sina.com.cn/ol/n/2014-09-19/1005816448.shtml"), 1)).printXML();
//		new WebTextInfoExtractor().extractWebText(new WebTextURL(new URL("http://hanfengbi.blog.sohu.com/305835783.html"), 1, 1)).printXML();
//		new WebTextInfoExtractor().extractWebText(new WebTextURL(new URL("http://blog.csdn.net/dingjixian/article/details/39923083"), 1, 1)).printXML();
//		new WebTextInfoExtractor().extractWebText(new WebTextURL(new URL("http://business.sohu.com/20141016/n405177858.shtml"), 1, 1)).printXML();
//		new WebTextInfoExtractor().extractWebText(new WebTextURL(new URL("http://blog.ifeng.com/article/34244359.html"), 1, 1)).printXML();
		new WebTextInfoExtractor().extractWebText(new WebTextURL(new URL("http://run.sports.news_163.com/15/0421/16/ANO4U3G6000509NH.html"), 1, 1)).printXML();
	}
}
