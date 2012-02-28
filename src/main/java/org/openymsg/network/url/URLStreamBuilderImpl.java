package org.openymsg.network.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class URLStreamBuilderImpl implements URLStreamBuilder {
	private static final Log log = LogFactory.getLog(URLStreamBuilderImpl.class);
	private String url;
	private int timeout;
	private URLStreamStatus status;
	private String cookie;
	private boolean keepHeaders;
	private boolean keepData;

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
	public URLStreamBuilder keepHeaders(boolean keepHeaders) {
		this.keepHeaders = keepHeaders;
		return this;
	}

	@Override
	public URLStreamBuilder keepData(boolean keepData) {
		this.keepData = keepData;
		return this;
	}

	@Override
	public URLStream build() {
		URL u;
		try {
			u = new URL(url);
		}
		catch (MalformedURLException e) {
			log.warn("Failed opening url: " + url, e);
			this.status.setMalformedURLException(e);
			return null;
		}
		URLConnection uc;
		try {
			uc = u.openConnection();
		}
		catch (IOException e) {
			log.warn("Failed connection url: " + u, e);
			this.status.setUrlConnectionException(e);
			return null;
		}

		if (cookie != null) {
			uc.setRequestProperty("Cookie", cookie);
		}

		uc.setConnectTimeout(timeout);

		if (uc instanceof HttpsURLConnection) {
			HttpsURLConnection httpUc = (HttpsURLConnection) uc;
			// used to simulate failures
			// if (triesBeforeFailure++ % 3 == 0) {
			// throw new SocketException("Test failure");
			// }
			// TODO - handle hosts sert issues
			// if (!yahooLoginHost.equalsIgnoreCase(LOGIN_YAHOO_COM)) {
			// httpUc.setHostnameVerifier(new HostnameVerifier() {
			//
			// public boolean verify(String hostname, SSLSession session) {
			// Principal principal = null;
			// try {
			// principal = session.getPeerPrincipal();
			// }
			// catch (SSLPeerUnverifiedException e) {
			// }
			// String localName = "no set";
			// if (principal != null) {
			// localName = principal.getName();
			// }
			// log.debug("Hostname verify: " + hostname + "localName: " + localName);
			// return true;
			// }
			// });
			// }
			// TODO HANDLE ssl with login
			// if (!yahooLoginHost.equalsIgnoreCase(LOGIN_YAHOO_COM)) {
			// httpUc.setHostnameVerifier(new HostnameVerifier() {
			//
			// public boolean verify(String hostname, SSLSession session) {
			// return true;
			// }
			// });
			// }
			int responseCode = -1;
			String responseMessage = null;
			try {
				responseCode = httpUc.getResponseCode();
				responseMessage = httpUc.getResponseMessage();
			}
			catch (IOException e) {
				log.warn("Failed reading responseCode and responseMessage", e);
				this.status.setResponseException(e);
				return null;
			}
			this.status.setResponseCode(responseCode);
			this.status.setResponseMessage(responseMessage);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try {
					InputStream inputStream = null;
					Map<String, List<String>> headers = null;
					if (this.keepData) {
						inputStream = uc.getInputStream();
					}
					if (this.keepHeaders) {
						headers = httpUc.getHeaderFields();
					}
					return new URLStream(inputStream, headers);
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			else {
				log.warn("Failed opening login url: " + url + " return code: " + responseCode);
				return null;
			}
		}
		else {
			Class<? extends URLConnection> ucType = null;
			if (uc != null) {
				ucType = uc.getClass();
			}
			log.error("Failed opening login url: " + url + " returns: " + ucType);
			// TODO handle failure
			return null;
		}
	}

	@Override
	public URLStreamStatus getStatus() {
		return this.status;
	}
}
