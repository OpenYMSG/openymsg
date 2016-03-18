package org.openymsg.network.url;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class URLStream {
	/** logger */
	private static final Log log = LogFactory.getLog(URLStream.class);
	private InputStream inputStream;
	private Map<String, List<String>> headers;

	public URLStream(InputStream inputStream, Map<String, List<String>> headers) {
		this.inputStream = inputStream;
		this.headers = headers;
	}

	public Map<String, List<String>> getHeaders() {
		return this.headers;
	}

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
		} catch (IOException e) {
			log.warn("Failed extracting response");
			// TODO handle failure
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn("Failed closing stream");
					// TODO handle failure
				}
			}
		}
		return out;
	}
}
