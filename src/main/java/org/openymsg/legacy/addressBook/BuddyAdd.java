package org.openymsg.legacy.addressBook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.legacy.network.NetworkConstants;
import org.openymsg.legacy.network.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class BuddyAdd {
	// public void addBuddy(String contactId, String firstName, String lastname) {
	// BuddyAdd buddyAdd = new BuddyAdd(this.loginID.getId(), sessionCookies);
	// try {
	// buddyAdd.process(contactId, firstName, lastname);
	// }
	// catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	private static final Log log = LogFactory.getLog(BuddyAdd.class);
	private String cookieLine;
	private String username; // used for logging

	public BuddyAdd(String username, String[] sessionCookies) {
		this.username = username;
		if (sessionCookies != null) {
			cookieLine = /* FIXME "Cookie: "+ */
					"Y=" + sessionCookies[NetworkConstants.COOKIE_Y] + "; " + "T="
							+ sessionCookies[NetworkConstants.COOKIE_T];
		} else {
			cookieLine = null;
		}
	}

	// <?xml version="1.0" encoding="utf-8"?><ab k="neilixhart" cc="1">
	// <ct a="1" yi='ytestuser6' fn='john' ln='mal' />
	// </ab>
	public void process(String contactId, String firstName, String lastname) throws IOException {
		String addressBookLink =
				"http://address.yahoo.com/yab/us?v=XM&prog=ymsgr&.intl=us&sync=1&tags=short&noclear=1&useutf8=1&legenc=codepage-1252 HTTP/1.1";
		String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ab k=\"" + username + "\" cc=\"1\">"
				+ "<ct a=\"1\"  yi='" + contactId + "' fn='" + firstName + "' ln='" + lastname + "' /></ab>";
		URL u = new URL(addressBookLink);
		URLConnection uc = u.openConnection();
		Util.initURLConnection(uc);
		if (cookieLine != null) {
			uc.setRequestProperty("Cookie", cookieLine);
		}
		uc.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
		wr.write(data);
		wr.flush();
		// log.trace("Cookie: " + uc.getRequestProperty("Cookie"));
		if (uc instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) uc).getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream responseStream = uc.getInputStream();
				byte[] buff = new byte[256];
				while ((responseStream.read(buff)) != -1) {
					String buffLine = new String(buff);
					log.trace(buffLine);
				}
			} else {
				log.warn("user: " + username + " responseCode from http is: " + responseCode);
			}
		}
	}
}
