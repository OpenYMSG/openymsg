package org.openymsg.network.url;

public interface UrlStreamStatusCallback {
	void failed(URLResponseFailure handlingResponseFailure);

	void failed(URLCreationFailure buildUrlFailure);

	void failed(URLConnectionFailure handlingConnectingFailure);

	void failed(URLSystemFailure systemFailure);
}
