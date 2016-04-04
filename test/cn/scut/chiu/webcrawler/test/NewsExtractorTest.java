package cn.scut.chiu.webcrawler.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.Set;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.extractor.WebTextInfoExtractor;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class NewsExtractorTest {
	public static void main(String[] args) throws IOException {
		BufferedWriter bw = LocalFileUtil.getWriter("crawler_rel/news_out/test_zt", "UTF-8");
		WebTextInfoExtractor extractor = new WebTextInfoExtractor();
		Queue<WebTextURL> tmp = LocalFileUtil.getNewsURL("crawler_rel/seeds_out/test_zt", "UTF-8");
		Set<String> urlStrs = Sets.newHashSet();
		Queue<WebTextURL> urls = Queues.newLinkedBlockingQueue();
		for (WebTextURL newsUrl : tmp) {
			if (urlStrs.contains(newsUrl.getURL().toString())) {
				continue;
			}
			urlStrs.add(newsUrl.getURL().toString());
			urls.add(newsUrl);
		}
//		System.out.println(urlStrs.size());
//		System.exit(0);
		for (WebTextURL newsUrl : urls) {
//			if (!(count++ % factor == 0)) {
//				continue;
//			}
			bw.write("#############\t" + newsUrl.toString() + "\t#############\n");
			WebTextInfo textInfo = extractor.extractWebText(newsUrl);
			System.out.println(newsUrl.toString());
			if (textInfo == null) {
				System.out.println("NULL:" + newsUrl.toString());
				continue;
			}
			bw.write(textInfo.getUrlStr() + "\n" + textInfo.getTitle() + "\n" + textInfo.getMainText().replaceAll("\r|\n", "") + "\n");
		}
		bw.flush();
		bw.close();
	}
}
