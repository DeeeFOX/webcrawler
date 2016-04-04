package cn.scut.chiu.webcrawler.crawler.filter;

import cn.scut.chiu.util.LocalFileUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Chiu on 2015/11/1.
 */
public class TitleFilter {
    private static Set<String> titleFilterTags4News;
    private static Set<String> titleFilterTags4Blogs;

    static {
        try {
            initTitleFilters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initTitleFilters() throws IOException {
        titleFilterTags4News = Sets.newHashSet();
        BufferedReader br = LocalFileUtil.getReader(Config.get(Params.FILTER_TITLE_TAG_FILE_NEWS), Params.CHARSET_UTF8);
        String filterTag = null;
        while ((filterTag = br.readLine()) != null) {
            titleFilterTags4News.add(filterTag);
        }
        br.close();

        titleFilterTags4Blogs = Sets.newHashSet();
        br = LocalFileUtil.getReader(Config.get(Params.FILTER_TITLE_TAG_FILE_BLOGS), Params.CHARSET_UTF8);
        while ((filterTag = br.readLine()) != null) {
            titleFilterTags4Blogs.add(filterTag);
        }
        br.close();
    }

    public static String filterNoise(String titleTagText, int textType, String hostSite) {
        int index = -1;
        String ret = titleTagText;
        Set<String> filterTags;
        // choose a filters
        if (textType == 1) {
            // news
            filterTags = titleFilterTags4News;
        } else if (textType == 2) {
            // blogs
            filterTags = titleFilterTags4Blogs;
        } else {
            // others
            return ret;
        }
        for (String filterTag : filterTags) {
            if ((index = ret.indexOf(filterTag)) != -1) {
                ret = ret.substring(0, index);
            }
        }
        return ret;
    }
}
