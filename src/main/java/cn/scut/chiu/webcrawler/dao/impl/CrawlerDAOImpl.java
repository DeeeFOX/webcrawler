package cn.scut.chiu.webcrawler.dao.impl;

import cn.scut.chiu.util.ObjectConvertor;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.object.TextInfo;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.dao.CrawlerDAO;
import cn.scut.chiu.webcrawler.dao.hbase.HBaseAPI;
import cn.scut.chiu.webcrawler.dao.hbase.HBaseAPIImpl;
import cn.scut.chiu.webcrawler.dao.object.HBaseObject;
import cn.scut.chiu.webcrawler.dao.object.WebText;
import com.google.common.collect.Lists;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Chiu on 2015/9/29.
 */
public class CrawlerDAOImpl implements CrawlerDAO {

    private static final Logger logger = LogManager.getLogger(CrawlerDAOImpl.class);

    private MongoTemplate mongoTemplate;

    private HBaseAPI hBaseAPI;

    public CrawlerDAOImpl() {
        try {
            if (Config.getBoolean(Params.IF_USE_HBASE)) {
                hBaseAPI = new HBaseAPIImpl(Params.HTABLE_TABLE_ARTICLE);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean batchInsertText(Collection<TextInfo> txtLst) {
        if (null == txtLst || txtLst.isEmpty()) {
            return false;
        }
        if (null == hBaseAPI) {
            try {
                hBaseAPI = new HBaseAPIImpl(Params.HTABLE_TABLE_ARTICLE);
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        List<HBaseObject> txtObjs = ObjectConvertor.convert2HBObject(txtLst, hBaseAPI.getRegionServerCount());
        hBaseAPI.putSomeRows(txtObjs);
        txtObjs.clear();
        txtObjs = null;
        // where create where clean
        return true;
    }

    @Override
    public int batchInsertText4Index(Collection<TextInfo> txtLst) {
        if (null == txtLst || txtLst.isEmpty()) {
            return -1;
        }
        List<WebText> webTexts = Lists.newLinkedList();
        for (TextInfo textInfo : txtLst) {
            if (textInfo instanceof WebTextInfo) {
                // 避免了为null的情况
                // 所有数据都copy一份到mongodb,以免hbase突然傻逼
                WebText webText = ObjectConvertor.convertTextInfo2WebText((WebTextInfo) textInfo);
                webTexts.add(webText);
//                if (textInfo.getCategory() == -3) {
//                    WebText webText = ObjectConvertor.convertTextInfo2WebText((WebTextInfo) textInfo);
//                    webTexts.add(webText);
//                }
            } else {
                if (null != textInfo) {
                    textInfo.printXML();
                }
            }
        }
        if (webTexts.size() != 0) {
            mongoTemplate.insertAll(webTexts);
        }
        webTexts.clear();
        webTexts = null;
        // where create where clean
        return 1;
    }

    @Override
    public int closeAll() {
        try {
            hBaseAPI.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
