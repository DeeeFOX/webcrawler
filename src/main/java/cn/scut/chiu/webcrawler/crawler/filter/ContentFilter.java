package cn.scut.chiu.webcrawler.crawler.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;

public class ContentFilter {
	
	private static Set<String> titleFilterWords;

	static {
		try {
			initContentFilters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void initContentFilters() throws IOException {
		titleFilterWords = new HashSet<String>();
		BufferedReader br = LocalFileUtil.getReader(Config.get(Params.FILTER_TITLE_WORD_FILE), Params.CHARSET_UTF8);
		String filterWord = null;
		while ((filterWord = br.readLine()) != null) {
			titleFilterWords.add(filterWord);
		}
		br.close();
	}
	
	public static String filterDisturbance(String mainText) {
		if (mainText == null) {
			return null;
		}
		String ret = mainText.trim().replaceAll("<[^<]*>", "");
//		System.out.println(ret);
		ret = ret.replaceAll("[（|(][^（|(|)|）]*记者[^（|(|)|）]*[）|)]", "");
//		System.out.println(ret);
		ret = ret.replaceAll(" ", ""); // filter &nbsp;
		return ret;
	}

	public static boolean filterByTitle(String titleText) {
		for (String filterWords : titleFilterWords) {
			if (titleText.indexOf(filterWords) != -1) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(ContentFilter.filterDisturbance("<SCRIPT LANGUAGE=&quot;JavaScript1.1&quot; SRC=&quot;http://36.adsina.allyes.com/main/adfshow?user=AFP6_for_SINA|News|WorldPIP&amp;db=news_sina&amp;local=yes&amp;js=on&quot;></SCRIPT> <NOSCRIPT><A HREF=&quot;http://36.adsina.allyes.com/main/adfclick?user=AFP6_for_SINA|News|WorldPIP&amp;db=news_sina&quot;><IMG SRC=&quot;http://36.adsina.allyes.com/main/adfshow?user=AFP6_for_SINA|News|WorldPIP&amp;db=news_sina&quot; WIDTH=1 HEIGHT=1 BORDER=0></a></NOSCRIPT>"));
	}
}
