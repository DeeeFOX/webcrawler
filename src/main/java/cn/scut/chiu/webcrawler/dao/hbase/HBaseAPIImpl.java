package cn.scut.chiu.webcrawler.dao.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.scut.chiu.util.ObjectConvertor;
import cn.scut.chiu.util.TimeUtil;
import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.crawler.object.TextInfo;
import cn.scut.chiu.webcrawler.crawler.object.WebTextInfo;
import cn.scut.chiu.webcrawler.dao.object.HBaseObject;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.scut.chiu.webcrawler.conf.Params;

public class HBaseAPIImpl implements HBaseAPI {

	private final static Logger logger = LogManager.getLogger(HBaseAPIImpl.class.getName());
	private final static int regionServerCount = Config.getInt(Params.HBASE_REGIONSERVER_COUNT);
	private static Configuration conf;

	static {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", Config.get(Params.ZOOKEEPER_QUORUM));
	}

	private HTableInterface htable;
	
	public HBaseAPIImpl(String tableName) throws IOException {
		htable = HConnectionManager.createConnection(conf)
				.getTable(tableName);
	}

	@Override
	public void putSingleValue(String rowKey, String family, String qualifier, long timestamp, String value, boolean isFlush) {
		Put put = new Put(Bytes.toBytes(rowKey));
		if (timestamp >= 0) {
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), timestamp, Bytes.toBytes(value));
		} else {
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		}
		try {
			htable.put(put);
			if (isFlush) {
				htable.flushCommits();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void putSomeRows(List<HBaseObject> txtObjs) {
		List<Put> puts = new ArrayList<Put>(txtObjs.size());
		long timestamp = System.currentTimeMillis();
		for (HBaseObject hBaseObject : txtObjs) {
			Put put = new Put(Bytes.toBytes(hBaseObject.getRowKey()));
			Map<String, Map<String, String>> fams = hBaseObject.getFams();
			for (Map.Entry<String, Map<String, String>> fam : fams.entrySet()) {
				byte[] family = Bytes.toBytes(fam.getKey());
				for (Map.Entry<String, String> qual : fam.getValue().entrySet()) {
					byte[] qualifier = Bytes.toBytes(qual.getKey());
					byte[] value = Bytes.toBytes(qual.getValue());
					put.add(family, qualifier, timestamp, value);
//					put.addColumn(family, qualifier, timestamp, value);
				}
			}
			puts.add(put);
		}
		try {
			htable.put(puts);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		// but if this will be wrong method
		puts.clear();
		puts = null;
		// where create where clean
	}

	@Override
	public void putSingleRow(String rowKey, List<String> families, List<String> qualifiers, List<Long> timestamps, List<String> values, boolean isFlush) {
		List<Put> puts = new ArrayList<Put>();
		Put put = null;
		Iterator<String> fits, qits, vits;
		Iterator<Long> tits;
		fits = families.iterator();
		qits = qualifiers.iterator();
		vits = values.iterator();
		tits = timestamps.iterator();
		while (vits.hasNext()) {
			put = new Put(Bytes.toBytes(rowKey));
			String family = fits.next();
			String qualifier = qits.next();
			String value = vits.next();
			long timestamp = tits.next();
			if (timestamp >= 0) {
				put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), timestamp, Bytes.toBytes(value));
			} else {
				put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			}
			puts.add(put);
		}
		try {
			htable.put(puts);
			if (isFlush) {
				htable.flushCommits();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static boolean checkHTable(String htablename, boolean create, boolean overwrite) throws IOException {
		boolean rtn = false;
		HBaseAdmin hbaseAdmin = new HBaseAdmin(conf);
		HTableDescriptor htableDescriptor = new HTableDescriptor(htablename);
		if (hbaseAdmin.tableExists(htablename)) {
			if (overwrite) {
				hbaseAdmin.disableTable(htablename);
				hbaseAdmin.deleteTable(htablename);
				hbaseAdmin.createTable(htableDescriptor);
			}
			rtn = true;
		} else {
			if (create) {
				hbaseAdmin.createTable(htableDescriptor);
				rtn = true;
			}
		}
		hbaseAdmin.close();
		return rtn;
	}
	
	public static boolean createSingleHTable(String htablename, String[] colfams, long maxFileSize, int maxTimestamp, boolean overwrite) throws IOException {
		HBaseAdmin hbadmin = new HBaseAdmin(conf);
		if (hbadmin.tableExists(htablename)) {
			if (overwrite) {
				hbadmin.disableTable(htablename);
				hbadmin.deleteTable(htablename);
			} else {
				logger.error(htablename + " has already existed");
				hbadmin.close();
				return false;
			}
		}
		HTableDescriptor htdescriptor = new HTableDescriptor(htablename);
		for (String family : colfams) {
			if (maxFileSize > 0) {
				htdescriptor.setMaxFileSize(maxFileSize);
			}
			HColumnDescriptor hcdescriptor = new HColumnDescriptor(family);
			if (maxTimestamp > 0) {
				hcdescriptor.setMaxVersions(maxTimestamp);
			}
			htdescriptor.addFamily(hcdescriptor);
		}
		hbadmin.createTable(htdescriptor);
		hbadmin.close();
		return true;
	}

	@Override
	public int getRegionServerCount() {
		return regionServerCount;
	}
	@Override
	public void flush() throws IOException {
		htable.flushCommits();
	}
	
	@Override
	public void close() throws IOException {
		htable.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(TimeUtil.parse("yyyy-MM-dd hh:mm:ss", 1446449329689l));
		System.out.println(System.currentTimeMillis() - Params.CLUSTER_TIME_INTERVAL_HALF_MIN * 2 * 60 * 24);
		System.exit(0);
//		String[] colfams = new String[2];
//		colfams[0] = Params.HTABLE_FAM_INFO;
//		colfams[1] = Params.HTABLE_FAM_RES;
//		createSingleHTable(Params.HTABLE_TABLE_ARTICLE, colfams, -1, -1, false);
		HBaseAPIImpl saver = new HBaseAPIImpl(Params.HTABLE_TABLE_ARTICLE);
//		saver.putSingleValue("test", "INFO", "A_TITLE", System.currentTimeMillis(), "test", false);
//		Thread.sleep(99999);
//		saver.flush();
		WebTextInfo webTextInfo = new WebTextInfo();
		webTextInfo.setMainText("fuckfuckfuck");
		webTextInfo.setUrlStr("www.fuck.com");
		webTextInfo.setCrawlTime(System.currentTimeMillis());
		webTextInfo.setCategory(1);
		webTextInfo.setTextType(1);
		webTextInfo.setTitle("fuckfuckfuck");
		webTextInfo.setSaveKey("1_" + "www.fuck.com".hashCode());
		List<TextInfo> webTextInfos = Lists.newArrayList();
		webTextInfos.add(webTextInfo);
		List<HBaseObject> convs = ObjectConvertor.convert2HBObject(webTextInfos, 4);
		saver.putSomeRows(convs);
	}
}
