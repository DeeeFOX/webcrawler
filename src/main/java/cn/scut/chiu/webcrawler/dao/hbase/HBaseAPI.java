package cn.scut.chiu.webcrawler.dao.hbase;

import cn.scut.chiu.webcrawler.dao.object.HBaseObject;

import java.io.IOException;
import java.util.List;

public interface HBaseAPI {
	void putSingleValue(String rowKey, String family, String qualifier,
			long timestamp, String value, boolean isFlush) throws IOException;

	void putSomeRows(List<HBaseObject> txtObjs);
	void putSingleRow(String rowKey, List<String> families,
			List<String> qualifiers, List<Long> timestamps,
			List<String> values, boolean isFlush);

	void flush() throws IOException;
	void close() throws IOException;
	
	int getRegionServerCount();
}
