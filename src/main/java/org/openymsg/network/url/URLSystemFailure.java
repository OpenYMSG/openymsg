package org.openymsg.network.url;

import java.net.URL;
import java.net.URLConnection;

public class URLSystemFailure {
	private URL url;
	private Class<? extends URLConnection> ucType;

	public URLSystemFailure(URL url, Class<? extends URLConnection> ucType) {
		this.url = url;
		this.ucType = ucType;
	}

	public URL getUrl() {
		return url;
	}

	public Class<? extends URLConnection> getUcType() {
		return ucType;
	}
}
