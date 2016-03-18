package org.openymsg.legacy.addressBook;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.legacy.network.NetworkConstants;
import org.openymsg.legacy.network.Util;
import org.openymsg.legacy.roster.Roster;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BuddyListImport {
	private static final Log log = LogFactory.getLog(BuddyListImport.class);
	private Roster roster;
	private String cookieLine;
	private String username; // used for logging

	public BuddyListImport(String username, Roster roster, String[] sessionCookies) {
		this.username = username;
		if (sessionCookies != null) {
			cookieLine = /* FIXME "Cookie: "+ */
					"Y=" + sessionCookies[NetworkConstants.COOKIE_Y] + "; " + "T="
							+ sessionCookies[NetworkConstants.COOKIE_T];
			String cookieB = sessionCookies[NetworkConstants.COOKIE_B];
			if (cookieB != null) {
				cookieLine = cookieLine + "; " + "B=" + cookieB;
			}
		} else {
			cookieLine = null;
		}
		this.roster = roster;
	}

	public void process(String userId, String password) throws IOException {
		String addressBookLink =
				"http://address.yahoo.com/yab/us?v=XM" + "&prog=ymsgr&useutf8=1&diffs=1&t=0&rt=0&prog-ver=7,0,0,426";
		// + "&prog=ymsgr&.intl=us&useutf8=1&diffs=1&t=0&rt=0&prog-ver=" + NetworkConstants.CLIENT_VERSION;
		// https://us-mg4.mail.yahoo.com/yab-fe/me/ExportContacts?pc=1&action=export_contacts&wssid=.....&export_type=action_export_yahoo%r=0.737351645482704
		URL u = new URL(addressBookLink);
		URLConnection uc = u.openConnection();
		Util.initURLConnection(uc);
		uc.setRequestProperty("User-Agent",
				"Yahoo!%20Messenger/235554 CFNetwork/520.5.1 Darwin/11.4.2 (x86_64) (MacBookPro10%2C1)");// NetworkConstants.USER_AGENT);
		// System.out.println("getRequestProperty before" + uc.getRequestProperty("Cookie"));
		if (cookieLine != null) {
			uc.setRequestProperty("Cookie", cookieLine);
		}
		// System.out.println("getRequestProperty after" + uc.getRequestProperty("Cookie"));
		CookieManager cm = (CookieManager) CookieHandler.getDefault();
		// System.out.println("cookieManager: " + cm);
		// if (cm != null) {
		// try {
		// // System.out.println("cookieStore: " + cm.getCookieStore().get(u.toURI()));
		//
		// Map<String, List<String>> blankHeaders = new HashMap<String, List<String>>();
		// // System.out.println("cookieMap.get: " + cm.get(u.toURI(), blankHeaders));
		// }
		// catch (URISyntaxException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// System.out.println("cookieLine: " + cookieLine);
		// log.trace("Cookie: " + uc.getRequestProperty("Cookie"));
		if (uc instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) uc).getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream responseStream = uc.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(responseStream, baos);
				byte[] bytes = baos.toByteArray();
				responseStream = new ByteArrayInputStream(bytes);
				byte[] buff = new byte[256];
				int read = -1;
				while ((read = responseStream.read(buff)) != -1) {
					String buffLine = new String(buff);
					log.debug(buffLine);
				}
				String buffLine = new String(buff);
				log.debug(buffLine);
				responseStream = new ByteArrayInputStream(bytes);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				try {
					// Using factory get an instance of document builder
					DocumentBuilder db = dbf.newDocumentBuilder();
					// parse using builder to get DOM representation of the XML file
					Document dom = db.parse(responseStream);
					// get the root element
					Element docEle = dom.getDocumentElement();
					log.trace("user: " + username + " Root is: " + docEle);
					// get a nodelist of elements
					NodeList nl = docEle.getElementsByTagName("ct");
					log.trace("user: " + username + " Found ct elements: " + nl.getLength());
					if (nl != null && nl.getLength() > 0) {
						for (int i = 0; i < nl.getLength(); i++) {
							// get the employee element
							Element el = (Element) nl.item(i);
							// log.trace("ct element: " + el);
							// get the Employee object
							YahooAddressBookEntry user = getContact(el);
							// add it to list
							this.roster.addOrUpdateAddressBook(user);
							// contacts.add(e);
						}
					} else {
						log.debug("user: " + username + " No node list found for ct. AddressBook empty?");
					}
				} catch (Exception pce) {
					log.error("user: " + username + " Failed reading xml addressbook", pce);
				}
			} else {
				log.warn("user: " + username + " responseCode from http is: " + responseCode);
			}
		}
	}

	private YahooAddressBookEntry getContact(Element empEl) {
		String id = getTextValue(empEl, "yi");
		String lcsid = getTextValue(empEl, "lcsid");
		String firstName = getTextValue(empEl, "fn");
		String lastName = getTextValue(empEl, "ln");
		String nickName = getTextValue(empEl, "nn");
		String groupName = getTextValue(empEl, "li");
		if (isEmpty(id) && isEmpty(lcsid)) {
			log.debug("user: " + username + " Failed building user firstname: " + firstName + ", lastname: " + lastName
					+ ", nickname: " + nickName + ", groupName: " + groupName + ", element: " + empEl + "/"
					+ empEl.getAttributes());
		}
		if (isEmpty(id) && !isEmpty(lcsid)) {
			id = lcsid;
		}
		YahooAddressBookEntry user = new YahooAddressBookEntry(id, firstName, lastName, nickName, groupName);
		// log.trace("firstname: " + firstName + ", lastname: " + lastName + ", nickname: " +
		// nickName + ", groupName: " + groupName);
		return user;
	}

	private boolean isEmpty(String id) {
		return id == null || id.length() == 0;
	}

	private String getTextValue(Element ele, String tagName) {
		// String textVal = null;
		return ele.getAttribute(tagName);
		// if(nl != null && nl.getLength() > 0) {
		// Element el = (Element)nl.item(0);
		// textVal = el.getFirstChild().getNodeValue();
		// }
		//
		// return textVal;
	}
}
