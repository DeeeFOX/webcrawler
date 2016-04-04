package cn.scut.chiu.util;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.object.TextInfo;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.dao.object.HBaseObject;
import cn.scut.chiu.webcrawler.dao.object.WebText;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * Created by Chiu on 2015/9/29.
 */
public class ObjectConvertor {
    public static List<HBaseObject> convert2HBObject(Collection<TextInfo> txtLst, int regionServerCount) {
        List<HBaseObject> lsts = new ArrayList<HBaseObject>(txtLst.size());
        WebTextInfo tmp = null;
        int crawledTextsCount = 0;
        long timestamp = System.currentTimeMillis();
        for (TextInfo text : txtLst) {
            if (text instanceof WebTextInfo) {
                tmp = (WebTextInfo) text;
                Map<String, String> values = Maps.newHashMap();
                values.put(Params.HTABLE_QUAL_ATITLE, tmp.getTitle());
                values.put(Params.HTABLE_QUAL_ATEXT, tmp.getMainText());
                values.put(Params.HTABLE_QUAL_AURL, tmp.getUrlStr());
                values.put(Params.HTABLE_QUAL_ATYPEID, String.valueOf(tmp.getTextType()));
                values.put(Params.HTABLE_QUAL_ACRAWLTIME, String.valueOf(timestamp));
                values.put(Params.HTABLE_QUAL_ACLASSID, String.valueOf(text.getCategory()));
                Map<String, Map<String, String>> fams = Maps.newHashMap();
                fams.put(Params.HTABLE_FAM_INFO, values);
                lsts.add(new HBaseObject(text.getSaveKey(), fams, timestamp));
            }
        }
        return lsts;
    }

    public static WebText convertTextInfo2WebText(WebTextInfo textInfo) {
        WebText webText = new WebText();
        webText.setA_ID(textInfo.getSaveKey());
        webText.setA_AT_ID(textInfo.getTextType());
        webText.setA_CLASS_ID(textInfo.getCategory());
        webText.setA_CONTENT(textInfo.getMainText());
        webText.setA_TITLE(textInfo.getTitle());
        webText.setA_CRAWLTIME(System.currentTimeMillis());
        webText.setA_URL(textInfo.getUrlStr());
        return webText;
    }
}
