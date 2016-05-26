package org.openymsg.network.url;

import java.io.IOException;
import java.net.URL;

public class URLResponseFailure {
	private final URL url;
	private final IOException exception;

	public URLResponseFailure(URL url, IOException exception) {
		this.url = url;
		this.exception = exception;
	}

	public URL getUrl() {
		return url;
	}

	public IOException getException() {
		return exception;
	}
}
