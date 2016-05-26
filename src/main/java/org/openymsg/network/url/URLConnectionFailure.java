package org.openymsg.network.url;

import java.io.IOException;
import java.net.URL;

public class URLConnectionFailure {
	private URL url;
	private IOException exception;
	private String responseMessage;
	private int responseCode;

	public URLConnectionFailure(URL url, IOException exception) {
		this.url = url;
		this.exception = exception;
	}

	public URLConnectionFailure(URL url, String responseMessage, IOException exception) {
		this(url, exception);
		this.responseMessage = responseMessage;
	}

	public URLConnectionFailure(URL url, String responseMessage, int responseCode) {
		this(url, responseMessage, (IOException) null);
		this.responseCode = responseCode;
	}

	public URL getUrl() {
		return url;
	}

	public IOException getException() {
		return exception;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}
}
