package org.openymsg.addressbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.config.SessionConfig;
import org.openymsg.execute.dispatch.Request;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.url.URLStream;
import org.openymsg.network.url.URLStreamBuilder;
import org.openymsg.network.url.URLStreamBuilderImpl;
import org.openymsg.network.url.URLStreamStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AddressBookRequest implements Request {
	private static final Log log = LogFactory.getLog(AddressBookRequest.class);
	private SessionConfig config;
	private final String cookieY;
	private final String cookieT;

	public AddressBookRequest(SessionConfig config, String cookieT, String cookieY) {
		this.config = config;
		this.cookieT = cookieT;
		this.cookieY = cookieY;
	}

	@Override
	public void execute() {
		String cookie = String.format(NetworkConstants.ADDRESSBOOK_COOKIE_FORMAT, this.cookieY, this.cookieT);
		URLStreamBuilder builder = new URLStreamBuilderImpl().url(NetworkConstants.ADDRESSBOOK_URL)
				.timeout(config.getConnectionTimeout()).cookie(cookie);
		URLStream stream = builder.build();
		URLStreamStatus status = builder.getStatus();
		InputStream in = stream.getInputStream();

		if (!status.isCorrect()) {
			log.warn("Failed retrieving response for url: " + NetworkConstants.ADDRESSBOOK_URL);
			// TODO handle failure
			return;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// parse using builder to get DOM representation of the XML file
		Document dom = null;
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			dom = db.parse(in);
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// get the root element
		Element docEle = dom.getDocumentElement();
		log.trace("Root is: " + docEle);

		// get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("ct");
		log.trace("Found ct elements: " + nl.getLength());
		Set<YahooAddressBookEntry> contacts = new HashSet<YahooAddressBookEntry>();

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the employee element
				Element el = (Element) nl.item(i);
				// log.trace("ct element: " + el);
				// get the Employee object
				YahooAddressBookEntry user = getContact(el);
				// add it to list
				contacts.add(user);
				// contacts.add(e);
			}
		}
		else {
			log.debug("No node list found for ct. AddressBook empty?");
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
			log.debug("Failed building user firstname: " + firstName + ", lastname: " + lastName + ", nickname: "
					+ nickName + ", groupName: " + groupName + ", element: " + empEl + "/" + empEl.getAttributes());
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

	@Override
	public void failure(Exception ex) {
		// TODO - what to do
		log.error("Failed running", ex);
	}

}
