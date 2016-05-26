package org.openymsg.network.url;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.TrustModifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class URLStreamBuilderImpl implements URLStreamBuilder {
	/** logger */
	private static final Log log = LogFactory.getLog(URLStreamBuilderImpl.class);
	private String url;
	private int timeout;
	private final URLStreamStatus status;
	private String cookie;
	private boolean disableSSLCheck;

	public URLStreamBuilderImpl() {
		this.status = new URLStreamStatus();
	}

	@Override
	public URLStreamBuilder url(String url) {
		this.url = url;
		return this;
	}

	@Override
	public URLStreamBuilder cookie(String cookie) {
		this.cookie = cookie;
		return this;
	}

	@Override
	public URLStreamBuilder timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	@Override
	public URLStream build() {
		log.trace("URL is: " + url);
		URL u;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			log.warn("Failed opening url: " + url, e);
			this.status.setFailedBuildingUrl(url, e);
			return null;
		}
		URLConnection uc = null;
		try {
			uc = u.openConnection();
		} catch (IOException e) {
			log.warn("Failed connection url: " + u, e);
			this.status.setFailedConnecting(u, e);
			return null;
		}
		if (uc == null) {
			log.warn("Failed connection url, returned null for: " + u);
			// TODO add to status
			return null;
		}
		if (cookie != null) {
			uc.setRequestProperty("Cookie", cookie);
		}
		uc.setConnectTimeout(timeout);
		uc.setReadTimeout(timeout);
		if (uc instanceof HttpsURLConnection) {
			HttpsURLConnection httpUc = (HttpsURLConnection) uc;
			if (disableSSLCheck) {
				try {
					log.debug("disabling ssl check: " + u);
					TrustModifier.relaxHostChecking(httpUc);
				} catch (Exception e) {
					log.warn("Failed disabling ssl checking: " + u);
				}
			}
			int responseCode = -1;
			String responseMessage = null;
			try {
				responseCode = httpUc.getResponseCode();
				responseMessage = httpUc.getResponseMessage();
			} catch (IOException e) {
				log.warn("Failed reading responseCode and responseMessage", e);
				this.status.setFailedHandlingResponse(u, e);
				return null;
			}
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = null;
				try {
					inputStream = uc.getInputStream();
				} catch (IOException e) {
					log.warn("Failed getting input stream", e);
					this.status.setFailedHandlingResponse(u, responseMessage, e);
					return null;
				}
				Map<String, List<String>> headers = httpUc.getHeaderFields();
				return new URLStream(inputStream, headers);
			} else {
				log.warn("Failed opening url: " + url + " return code: " + responseCode);
				this.status.setFailedHandlingResponse(u, responseMessage, responseCode);
				return null;
			}
		} else {
			Class<? extends URLConnection> ucType = null;
			if (uc != null) {
				ucType = uc.getClass();
			}
			log.error("Failed opening url: " + url + " returns: " + ucType);
			this.status.setFailedSystem(u, ucType);
			return null;
		}
	}

	@Override
	public URLStreamStatus getStatus() {
		return this.status;
	}

	@Override
	public URLStreamBuilder disableSSLCheck(boolean disableSSLCheck) {
		this.disableSSLCheck = disableSSLCheck;
		return this;
	}
}
