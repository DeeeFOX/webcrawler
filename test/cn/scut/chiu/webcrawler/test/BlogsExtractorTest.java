package cn.scut.chiu.webcrawler.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.extractor.WebTextInfoExtractor;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;

public class BlogsExtractorTest {
	public static void main(String[] args) throws IOException {
		BufferedWriter bw = LocalFileUtil.getWriter("crawler_rel/blog_out/blog_fyfz", "UTF-8");
		WebTextInfoExtractor extractor = new WebTextInfoExtractor();
		Queue<WebTextURL> urls = LocalFileUtil.getBlogsURL("crawler_rel/seeds_out/blog_fyfz", "UTF-8");
		int count = 0;
		int size = urls.size() / 10;
		int factor = urls.size() / size;
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
			bw.write(textInfo.getTitle() + "\n" + textInfo.getMainText().replaceAll("\r|\n", "") + "\n");
		}
		bw.flush();
		bw.close();
	}

	// http://blog.ifeng.com/
}
