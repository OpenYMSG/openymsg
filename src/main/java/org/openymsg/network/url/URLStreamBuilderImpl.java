package org.openymsg.network.url;

import java.io.ByteArrayOutputStream;
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
	private URLStreamBuilderStatus status;
	private String cookie;
	private boolean keepHeaders;
	private boolean keepData;
	private InputStream inputStream;
	private Map<String, List<String>> headers;

	public URLStreamBuilderImpl() {
		this.status = new URLStreamBuilderStatus();
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
	public Map<String, List<String>> getHeaders() {
		return headers;
	}


	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	public ByteArrayOutputStream getOutputStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = this.getInputStream();

		if (in == null)
			return null;

		try {

			int read = -1;
			byte[] buff = new byte[256];
			while ((read = in.read(buff)) != -1) {
				out.write(buff, 0, read);
			}
			in.close();
		}
		catch (IOException e) {
			log.warn("Failed extracting response");
			// TODO handle failure
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					log.warn("Failed closing stream");
					// TODO handle failure
				}
			}
		}
		return out;
	}

	public URLStreamBuilderStatus build() {
		URL u;
		try {
			u = new URL(url);
		}
		catch (MalformedURLException e) {
			log.warn("Failed opening url: " + url, e);
			this.status.setMalformedURLException(e);
			return status;
		}
		URLConnection uc;
		try {
			uc = u.openConnection();
		}
		catch (IOException e) {
			log.warn("Failed connection url: " + u, e);
			this.status.setUrlConnectionException(e);
			return status;
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
				return status;
			}
			this.status.setResponseCode(responseCode);
			this.status.setResponseMessage(responseMessage);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try {
					if (this.keepData) {
						this.inputStream = uc.getInputStream();
					}
					if (this.keepHeaders) {
						this.headers = httpUc.getHeaderFields();
					}
					return status;
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return status;
				}
			}
			else {
				log.warn("Failed opening login url: " + url + " return code: " + responseCode);
				return status;
			}
		}
		else {
			Class<? extends URLConnection> ucType = null;
			if (uc != null) {
				ucType = uc.getClass();
			}
			log.error("Failed opening login url: " + url + " returns: " + ucType);
			// TODO handle failure
			return status;
		}
	}
}
