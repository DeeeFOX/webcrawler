package cn.scut.chiu.webcrawler.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.extractor.BlogURLExtractor;
import cn.scut.chiu.webcrawler.crawler.extractor.NewsURLExtractor;
import cn.scut.chiu.webcrawler.crawler.extractor.WebTextType;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class URLExtractorTest {

	public static void main(String[] args) throws IOException, URISyntaxException {
//		testBlog();
//		testNews();
//		testBlog();
		BufferedWriter bw = LocalFileUtil.getWriter("crawler_rel/seeds_out/test_zt", "UTF-8");
		for (WebTextURL url : LocalFileUtil.getSeedURL("crawler_rel/seeds/test_zt", "UTF-8")) {
			bw.write("#############\t" + url.getURL().toString() + "\t################\n");
			if (url.getTextType() == WebTextType.NEWS) {
				System.out.println(url.toString());
				for (String urlStr : new NewsURLExtractor().extractURL(url.getURL())) {
					bw.write(urlStr + "\n");
				}
			} else if (url.getTextType() == WebTextType.BLOG) {
				System.out.println(url.toString());
				for (String urlStr : new BlogURLExtractor().extractURL(url.getURL())) {
					bw.write(urlStr + "\n");
				}
			}
			bw.flush();
		}
		bw.close();
	}

	private static void testWeibbo() throws MalformedURLException {
		WebTextURL url = new WebTextURL(new URL("http://weibo.com/p/1008087b0c93b241e18116980f85add831a2ee?from=faxian_huati"), 1, 3);
		BlogURLExtractor be =  new BlogURLExtractor();
		Set<String> strings = be.extractURL(url.getURL());
		System.out.println(strings);
		System.exit(0);
	}

	public static void testNews() throws MalformedURLException {
		WebTextURL url = new WebTextURL(new URL("http://www.ifanr.com/"), 1, 1);
		NewsURLExtractor newsURLExtractor = new NewsURLExtractor();
		System.out.println(newsURLExtractor.extractURL(url.getURL()));
		System.exit(0);
	}

	public static void testBlog() throws MalformedURLException {
		WebTextURL url = new WebTextURL(new URL("http://blog.sina.com.cn/lm/mil/"), 1, 2);
		BlogURLExtractor newsURLExtractor = new BlogURLExtractor();
		System.out.println(newsURLExtractor.extractURL(url.getURL()));
		System.exit(0);
	}
}
