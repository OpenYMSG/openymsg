package org.openymsg.network.url;

import java.io.IOException;
import java.net.URL;

public class UrlConnectionExceptionInfo {
	private final URL url;
	private final IOException urlConnectionException;

	public UrlConnectionExceptionInfo(URL url, IOException urlConnectionException) {
		this.url = url;
		this.urlConnectionException = urlConnectionException;
	}

	public URL getUrl() {
		return url;
	}

	public IOException getUrlConnectionException() {
		return urlConnectionException;
	}
}
