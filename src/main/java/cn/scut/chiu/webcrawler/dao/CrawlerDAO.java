package cn.scut.chiu.webcrawler.dao;

import cn.scut.chiu.webcrawler.crawler.object.TextInfo;

import java.util.Collection;

/**
 * Created by Chiu on 2015/9/29.
 */
public interface CrawlerDAO {
    /**
     * batch insert text obj
     *
     * @param txtLst
     * @return
     */
    boolean batchInsertText (Collection<TextInfo> txtLst);

    /**
     * batch insert text obj 4 index
     *
     * @param txtLst
     * @return
     */
    int batchInsertText4Index(Collection<TextInfo> txtLst);

    int closeAll();
}
