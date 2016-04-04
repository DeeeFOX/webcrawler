package cn.scut.chiu.webcrawler.crawler.object;

import java.util.UUID;

public class WebTextInfo extends TextInfo {
	
	private String urlStr;
	
	public WebTextInfo() {
		init();
	}
	
	public WebTextInfo(String urlStr) {
		init();
		this.urlStr = urlStr;
		saveKey = createKeyByURL();
	}

	@Override
	public void init() {
		infoType = InfoType.TEXT;
	}
	
	protected String createKeyByURL() {
		return UUID.nameUUIDFromBytes(urlStr.getBytes()).toString().replaceAll("-", "");
	}
	
	public long getCrawlTime() {
		return createTime;
	}

	public void setCrawlTime(long crawlTime) {
		createTime = crawlTime;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	
	@Override
	public void printXML() {
		String out = "<title>" + title + "</title>\n"
				+ "<main text>\n" + mainText + "\n</main text>\n"
				+ "<url>" + urlStr + "</url>\n"
				+ "<crawl time>" + createTime + "</crawl time>\n"
				+ "<category>" + category + "</category>\n"
				+ "<type>" + textType + "</type>\n"
				+ "<polarity>" + polarity + "</polarity>\n";
		System.out.println(out);
	}
	
	public boolean hasContent() {
		return mainText != null && !mainText.trim().equals("");
	}
}
