package cn.scut.chiu.webcrawler.crawler.filter;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URLFilter {

	public static HashSet<String> retainURL(Elements urls, int type, String host, String siteName) {
		if (urls == null) {
			return null;
		}
		HashSet<String> urlSet = new HashSet<String>();
		String urlStr = null;
		for(Element url : urls) {
			if (type == 1) {
				if (host != null) {
					if (siteName != null) {
						if ((urlStr = retainSpec(url)) == null) {
							continue;
						}
					} else {
						if ((urlStr = retainNewsURL(url)) == null) {
							continue;
						}
					}
				} else {
					if ((urlStr = retainNewsURL(url)) == null) {
						continue;
					}
				}
			} else if (type == 2) {
				if (host != null) {
					if (siteName != null) {
						if ((urlStr = retainSpec(url)) == null) {
							continue;
						}
					} else if (host.toLowerCase().indexOf("csdn") != -1) {
						if ((urlStr = retainCSDN(url)) == null) {
							continue;
						}
					} else {
						if ((urlStr = retainBlogURL(url)) == null) {
							continue;
						}
					}
				} else {
					if ((urlStr = retainBlogURL(url)) == null) {
						continue;
					}
				}

			}
			urlSet.add(urlStr);
		}
		if(urlSet.size()>0) {
			return urlSet;
		}
		return null;
	}

	protected static String retainNewsURL(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		String urlText = url.text().trim();
		if(urlText.length()<=5) {
			return null;
		}
		return urlStr;
	}

	protected static String retainBlogURL(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		String urlText = url.text().trim();
		if(urlText.length()<=5 || urlStr.indexOf("blog") == -1 || urlStr.split("/").length <= Config.getInt(Params.MIN_SLASH_LIMIT)) {
			return null;
		}
		return urlStr;
	}

	protected static String retainCnBeta(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		if(urlStr.indexOf("articles") == -1) {
			return null;
		}
		return urlStr;
	}

	protected static String retainCSDN(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		if(urlStr.indexOf("article") == -1) {
			return null;
		}
		return urlStr;
	}

	private static String retainPingWest(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		if(urlStr.toLowerCase().indexOf("www.pingwest.com/") == -1
				|| urlStr.length() > urlStr.replaceAll("(category)|(tags)|(subject)|(php)|(author)|(about)|(recruiting)|(page)|(feed)|(partner)|(contact)|(\\.png)", "").length()) {
			return null;
		}
		return urlStr;
	}

	private static String retainIFanR(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		if(urlStr.indexOf("news") == -1) {
			return null;
		}
		return urlStr;
	}


	private static String retainGeekPark(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		if(urlStr.indexOf("topics") == -1) {
			return null;
		}
		return urlStr;
	}

	protected static String retainSpec(Element url) {
		if (url == null) {
			return null;
		}
		String urlStr = url.attr("abs:href").trim();
		return urlStr;
	}

	public static void main(String[] args) {
		String url = "http://blog.news_sina.com.cn/u/1358221022";
		String url2 = "http://blog.news_sina.com.cn/s/blog_724cee030102vc3e.html?tj=news";
		String url3 = "http://blog.news_sina.com.cn/s/blog_531c8a890102vsta.html?tj=fina";
		String url4 = "http://blog.news_sina.com.cn/lm/tech/blog.news_sina.com.cn/s/blog_5d098bcc0102vw34.html?tj=tech";
		String url5 = "http://control.blog.news_sina.com.cn/admin/advice/advice_list.php";
		String url6 = "http://blog.news_ifeng.com/article/37757717.html";
		String url7 = "http://blog.news_ifeng.com/zhuanti/liangan2015/";
		String url8 = "http://news.cntv.cn/2015/10/03/ARTI1443850717731709.shtml";
		String url9 = "\n" +
				"\n" +
				"\thttp://www.chinanews.com/sh/2015/09-30/7551614.shtml\n";
		String url10 = "http://gz.house.ifeng.com/detail/2015_09_29/50569204_0.shtml";
		String url11 = "http://www.ifanr.com/news/569022";
		String blogRegrex = "^.*blog(?!.*(\\/u|lm|special|help|m\\/)).*(?<!\\.index|\\.php|\\.china)$";
		String newsRegrex = "^http://www.ifanr.com/news/[0-9]+$";
		Pattern pattern = Pattern.compile(newsRegrex);
		Matcher matcher = pattern.matcher(url11);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
	}
}
