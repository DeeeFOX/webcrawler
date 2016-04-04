package cn.scut.chiu.webcrawler.crawler;

import cn.scut.chiu.webcrawler.conf.Config;
import cn.scut.chiu.webcrawler.conf.Params;
import com.google.common.collect.Sets;

import java.net.URL;
import java.util.Set;

public class WebTextURL {

	public static Set<String> hostSites;

	private URL url;
	private int category;
	private int textType;
	private String hostSite;

	static {
		initCandHostSites();
	}

	private static void initCandHostSites() {
		hostSites = Sets.newHashSet(Config.get(Params.CRAWLER_HOST_SITES).split(Params.SPLIT_COMMA));
	}

	/**
	 * Init a web text url instance by the parameters
	 * 
	 * @param url The url itself
	 * @param category Category that this url belongs to
	 * @param textType Text type that this url belongs to (temporary)
	 */
	public WebTextURL(URL url, int category, int textType) {
		this.url = url;
		this.category = category;
		this.textType = textType;
		this.hostSite = url.getHost();
		for (String hostSite : hostSites) {
			if (this.hostSite.indexOf(hostSite) != -1) {
				this.hostSite = hostSite;
				break;
			}
		}
	}
	
	public URL getURL() {
		return url;
	}
	
	public int getCategory() {
		return category;
	}
	
	public int getTextType() {
		return textType;
	}
	
	public String getHost() {
		return url.getHost();
	}

	public String getHostSite() {
		return hostSite;
	}

	@Override
	public String toString() {
		return url.toString();
		
	}
	
	@Override
	public int hashCode() {
		return url.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WebTextURL)) {
			return false;
		} else {
			if (url.toString().equals(((WebTextURL)obj).toString())) {
				return true;
			} else {
				return false;
			}
		}
	}
}
