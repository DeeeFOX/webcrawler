package cn.scut.chiu.webcrawler.conf;

import com.google.common.collect.Maps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

public class Config {

	private static Logger logger = LogManager.getLogger(Config.class);
	private static String machineIp;
	private static Properties properties;
	private static Map<String, Integer> intProps;
	private static Map<String, Double> doubleProps;

	static {
		try {
			machineIp = InetAddress.getLocalHost().getHostAddress();
			properties = new Properties();
			properties.loadFromXML(new FileInputStream(Params.CONF_FILE_PATH_DEFAULT));
			intProps = Maps.newHashMap();
			doubleProps = Maps.newHashMap();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		} catch (InvalidPropertiesFormatException e) {
			logger.error(e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * get the config value by key name
	 * 
	 * @param keyName
	 * @return
	 */
	public static String get(String keyName) {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.loadFromXML(new FileInputStream(Params.CONF_FILE_PATH_DEFAULT));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String value = properties.getProperty(keyName);
		if (null == value) {
			return "null";
		}
		return value.trim();
	}

	public static int getInt(String keyName) {
		if (null == intProps) {
			intProps = Maps.newHashMap();
		}
		Integer ret = intProps.get(keyName);
		if (null == ret) {
			// should add a precheck or throw a exception
			ret = Integer.parseInt(properties.getProperty(keyName));
			intProps.put(keyName, ret);
		}
		return ret;
	}

	public static boolean getBoolean(String keyName) {
		int ret = getInt(keyName);
		if (ret != 0) {
			return true;
		} else {
			return false;
		}
	}

	public static double getDouble(String keyName) {
		Double ret = doubleProps.get(keyName);
		if (null == ret) {
			// should add a precheck or throw a exception
			ret = Double.valueOf(properties.getProperty(keyName));
			doubleProps.put(keyName, ret);
		}
		return ret;
	}

	public static String getLocalMachineIp() {
		return machineIp;
	}
	
	public static String[] getWorkersIpGroup() {
		return get(Params.WORKERS_IP).split(",");
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(Config.get("master.accept.port"));
	}
}
