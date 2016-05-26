package org.openymsg.network.url;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

// TODO - to stream with the info
public class URLStreamStatus {
	private URLCreationFailure buildUrlFailure;
	private URLResponseFailure handlingResponseFailure;
	private URLConnectionFailure handlingConnectingFailure;
	private URLSystemFailure systemFailure;

	public boolean isCorrect() {
		return (buildUrlFailure == null) && (handlingResponseFailure == null) && (handlingConnectingFailure == null)
				&& (systemFailure == null);
	}

	public void call(UrlStreamStatusCallback callback) {
		if (!isCorrect()) {
			if (buildUrlFailure != null) {
				callback.failed(buildUrlFailure);
			} else if (handlingResponseFailure != null) {
				callback.failed(handlingResponseFailure);
			} else if (handlingConnectingFailure != null) {
				callback.failed(handlingConnectingFailure);
			} else if (systemFailure != null) {
				callback.failed(systemFailure);
			}
		}
	}

	public void setFailedBuildingUrl(String url, MalformedURLException e) {
		buildUrlFailure = new URLCreationFailure(url, e);
	}

	public void setFailedHandlingResponse(URL u, IOException e) {
		handlingResponseFailure = new URLResponseFailure(u, e);
	}

	public void setFailedConnecting(URL u, IOException e) {
		handlingConnectingFailure = new URLConnectionFailure(u, e);
	}

	public void setFailedHandlingResponse(URL u, String responseMessage, IOException e) {
		handlingConnectingFailure = new URLConnectionFailure(u, responseMessage, e);
	}

	public void setFailedHandlingResponse(URL u, String responseMessage, int responseCode) {
		handlingConnectingFailure = new URLConnectionFailure(u, responseMessage, responseCode);
	}

	public void setFailedSystem(URL u, Class<? extends URLConnection> ucType) {
		systemFailure = new URLSystemFailure(u, ucType);
	}
}
