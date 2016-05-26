package org.openymsg.network.url;

import java.io.IOException;

public class URLCreationFailure {
	private final String url;
	private final IOException exception;

	public URLCreationFailure(String url, IOException exception) {
		this.url = url;
		this.exception = exception;
	}

	public String getUrl() {
		return url;
	}

	public IOException getException() {
		return exception;
	}
}
