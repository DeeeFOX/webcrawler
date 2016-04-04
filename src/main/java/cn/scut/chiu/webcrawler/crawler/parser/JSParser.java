package cn.scut.chiu.webcrawler.crawler.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.scut.chiu.webcrawler.conf.Params;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class JSParser {
	
	static {
		Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	}
	
	public static Document parseURL(URL url) {
		HtmlPage page = null;
//		System.out.println(url.toString());
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(Params.CRAWL_TIMEOUT);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setAppletEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);
		// false
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(true);
		webClient.getOptions().setDoNotTrackEnabled(false);
		webClient.getOptions().setGeolocationEnabled(false);
		webClient.getOptions().setPopupBlockerEnabled(false);
		// 4 tencent blog
//		webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//		webClient.addRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
//		webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
//		CookieManager cookieManager = webClient.getCookieManager();
//		// Cookie: =; =; =; =; =; =; =; =; =; =; =; =; =; =; =;pgv_info=ssid=s6155537760; pgv_pvid=4989464; o_cookie=602716933; zzpaneluin=; zzpanelkey=; p_skey=; pt4_token=; p_uin=; rv2=802D9AA413892135A9E78E3D364B59306D703363217BF6E766; property20=367CA8A29D4E5C4F7837A3D6950B8C389C1CFC0C2556E4A048DB9B738DE4C8C60A3B1659F8592BD8; qzspeedup=sdch
//		// pgv_info==; =; =; zzpaneluin=; zzpanelkey=; p_skey=; pt4_token=; p_uin=; =; =; =
//		Cookie cookie = new Cookie("b1.qzong.qq.com", "pgv_r_cookie", "1131491385455");
//		Cookie cookie1 = new Cookie("b1.qzong.qq.com", "PCCOOKIE", "779d606051f12399117736e36087f8c17fd67d0f0274f0be034403c837793bd1");
//		Cookie cookie2 = new Cookie("b1.qzong.qq.com", "pvid", "4989464");
//		Cookie cookie3 = new Cookie("b1.qzong.qq.com", "cpu_performance", "20");
//		Cookie cookie4 = new Cookie("b1.qzong.qq.com", "QZ_FE_WEBP_SUPPORT", "1");
//		Cookie cookie5 = new Cookie("b1.qzong.qq.com", "cpu_performance_v8", "49");
//		Cookie cookie6 = new Cookie("b1.qzong.qq.com", "__Q_w_s_hat_seed", "1");
//		Cookie cookie7 = new Cookie("b1.qzong.qq.com", "__Q_w_s__QZN_TodoMsgCnt", "1");
//		Cookie cookie8 = new Cookie("b1.qzong.qq.com", "pgv_pvi", "1446596608");
//		Cookie cookie9 = new Cookie("b1.qzong.qq.com", "pgv_si", "s545917952");
//		Cookie cookie10 = new Cookie("b1.qzong.qq.com", "ptui_loginuin", "602716933@qq.com");
//		Cookie cookie11 = new Cookie("b1.qzong.qq.com", "ptisp", "edu");
//		Cookie cookie12 = new Cookie("b1.qzong.qq.com", "RK", "ONWO4Ga2XF");
//		Cookie cookie13 = new Cookie("b1.qzong.qq.com", "ptcz", "22ba80498508341a52ef7ab017c04dc5e349f7529e4bfab99179533333f068e5");
//		Cookie cookie14 = new Cookie("b1.qzong.qq.com", "pt2gguin", "o0602716933");
//		Cookie cookie15 = new Cookie("b1.qzong.qq.com", "ssid", "s6155537760");
//		Cookie cookie16 = new Cookie("b1.qzong.qq.com", "pgv_pvid", "4989464");
//		Cookie cookie17 = new Cookie("b1.qzong.qq.com", "o_cookie", "602716933");
//		Cookie cookie18 = new Cookie("b1.qzong.qq.com", "rv2", "802D9AA413892135A9E78E3D364B59306D703363217BF6E766");
//		Cookie cookie19 = new Cookie("b1.qzong.qq.com", "property20", "367CA8A29D4E5C4F7837A3D6950B8C389C1CFC0C2556E4A048DB9B738DE4C8C60A3B1659F8592BD8");
//		Cookie cookie20 = new Cookie("b1.qzong.qq.com", "qzspeedup", "sdch");
		// 4 WEIBO
//		CookieManager cookieManager = webClient.getCookieManager();
//		Cookie cookie = new Cookie("weibo.com", "vjuids", "20e1fff59.136a2074f38.0.fe7d5cbe");
//		Cookie cookie2 = new Cookie("weibo.com", "vjlast", "1334158250.1334158250.30");
//		Cookie cookie3 = new Cookie("weibo.com", "lzstat_uv", "33655110283570379369|2893156");
//		Cookie cookie4 = new Cookie("weibo.com", "SINAGLOBAL", "4354421410243.958.1359509690149");
//		Cookie cookie5 = new Cookie("weibo.com", "SUHB", "0pHhHqvX0UCwJc");
//		Cookie cookie6 = new Cookie("weibo.com", "SUBP", "0033WrSXqPxfM72wWs9jqgMF55529P9D9WhgRSVJuBHmql4PMJwOCwAp");
//		Cookie cookie7 = new Cookie("weibo.com", "login_sid_t", "67aa25899d987cec8b4b44b60ad6295a");
//		Cookie cookie8 = new Cookie("weibo.com", "Apache", "212932310532.7785.1447814372676");
//		Cookie cookie9 = new Cookie("weibo.com", "ULV", "1447814372685:1647:9:1:212932310532.7785.1447814372676:1447469331247");
//		Cookie cookie10 = new Cookie("weibo.com", "YF-Page-G0", "3d55e26bde550ac7b0d32a2ad7d6fa53");
//		Cookie cookie11 = new Cookie("weibo.com", "_s_tentry", "www.nfcmag.com");
//		Cookie cookie12 = new Cookie("weibo.com", "UOR", ",,www.nfcmag.com");
//		Cookie cookie13 = new Cookie("weibo.com", "SUB", "_2AkMhRpr0dcNhrAZVmvgXzWnhaIlH-jjGieTBAH-8JWUBHRgNiMQ2TYQ_R6fxbXPAFpJRFeEQ7w");
//
//		cookieManager.addCookie(cookie);
//		cookieManager.addCookie(cookie2);
//		cookieManager.addCookie(cookie3);
//		cookieManager.addCookie(cookie4);
//		cookieManager.addCookie(cookie5);
//		cookieManager.addCookie(cookie6);
//		cookieManager.addCookie(cookie7);
//		cookieManager.addCookie(cookie8);
//		cookieManager.addCookie(cookie9);
//		cookieManager.addCookie(cookie10);
//		cookieManager.addCookie(cookie11);
//		cookieManager.addCookie(cookie12);
//		cookieManager.addCookie(cookie13);
//		cookieManager.addCookie(cookie14);
//		cookieManager.addCookie(cookie15);
//		cookieManager.addCookie(cookie16);
//		cookieManager.addCookie(cookie17);
//		cookieManager.addCookie(cookie18);
//		cookieManager.addCookie(cookie19);
//		cookieManager.addCookie(cookie20);
//		cookieManager.setCookiesEnabled(true);
//		webClient.setCookieManager(cookieManager);
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		WebRequest webRequest = new WebRequest(url);
//		webRequest.setAdditionalHeader("Accept-Encoding", "deflate,sdch,bzip2");
//		NameValuePair nameValuePair = new NameValuePair("Accept-Encoding", "gzip,deflate,sdch");
//		List<NameValuePair> lst = Lists.newArrayList();
//		lst.add(nameValuePair);
//		webRequest.setRequestParameters(lst);
//		webRequest.setHttpMethod(HttpMethod.GET);
//		webRequest.setCharset("UTF-8");
//		webRequest.setCharset("GBK");
		try {
			page = webClient.getPage(url);
//			WebResponse webResponse = webClient.loadWebResponse(webRequest);
//			System.out.println(webResponse.getContentAsString());
//			page = webClient.getPage(webRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (page == null) {
			return null;
		}
		Document doc = Jsoup.parse(page.asXml());
//		System.out.println(doc.html());
		doc.select(Params.CRAWLER_DOM_REMOVE_ELE).remove();
		webClient.closeAllWindows();
		webClient = null;
		return doc;
	}
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println(parseURL(new URL("http://user.qzone.qq.com/622001757/blog/1452286632")));
	}
}
