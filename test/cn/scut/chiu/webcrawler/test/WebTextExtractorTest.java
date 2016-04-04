package cn.scut.chiu.webcrawler.test;

import cn.scut.chiu.webcrawler.crawler.WebTextURL;
import cn.scut.chiu.webcrawler.crawler.extractor.WebTextInfoExtractor;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by Chiu on 2015/10/2.
 */
public class WebTextExtractorTest {
    public static void main(String[] args) throws IOException {
        WebTextInfoExtractor webTextInfoExtractor = new WebTextInfoExtractor();
        WebTextInfo webTextInfo = webTextInfoExtractor.extractWebText(new WebTextURL(new URL("http://user.qzone.qq.com/622000319/blog/1420938705"),
                1, 2));
        if (webTextInfo != null) {
            System.out.println("title:\n" + webTextInfo.getTitle());
            System.out.println("text:\n" + webTextInfo.getMainText().replaceAll("\r|\\s|\n|\t|　", ""));
        }

        HashSet<Object> objects = Sets.newHashSet();
        objects.remove(null);
    }
}