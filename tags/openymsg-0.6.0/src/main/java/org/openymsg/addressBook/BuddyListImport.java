package org.openymsg.addressBook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.Util;
import org.openymsg.roster.Roster;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BuddyListImport {
    private static final Log log = LogFactory.getLog(BuddyListImport.class);
    private Roster roster;
    private String cookieLine;
    private String username; //used for logging

    public BuddyListImport(String username, Roster roster, String[] sessionCookies) {
        this.username = username; 
        if (sessionCookies != null) {
            cookieLine = /* FIXME "Cookie: "+ */
            "Y=" + sessionCookies[NetworkConstants.COOKIE_Y] + "; " + "T=" + sessionCookies[NetworkConstants.COOKIE_T];
        }
        else {
            cookieLine = null;
        }
        this.roster = roster;
    }

    public void process(String userId, String password) throws IOException {
        String addressBookLink = "http://address.yahoo.com/yab/us?v=XM";

        URL u = new URL(addressBookLink);
        URLConnection uc = u.openConnection();
        Util.initURLConnection(uc);

        if (cookieLine != null) {
            uc.setRequestProperty("Cookie", cookieLine);
        }

        // log.trace("Cookie: " + uc.getRequestProperty("Cookie"));
        if (uc instanceof HttpURLConnection) {
            int responseCode = ((HttpURLConnection) uc).getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = uc.getInputStream();
                // byte[] buff = new byte[256];
                // int read = -1;
                // while ((read = responseStream.read(buff)) != -1) {
                // String buffLine = new String(buff);
                // log.trace(buffLine);
                // }

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

                List<YahooAddressBookEntry> contacts = new ArrayList<YahooAddressBookEntry>();
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
                    }
                    else {
                        log.debug("user: " + username + " No node list found for ct. AddressBook empty?");
                    }
                }
                catch (Exception pce) {
                    log.error("user: " + username + " Failed reading xml addressbook", pce);
                }
            }
            else {
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
            log.debug("user: " + username + " Failed building user firstname: " + firstName 
                    + ", lastname: " + lastName + ", nickname: " + nickName 
                    + ", groupName: " + groupName 
                    + ", element: " + empEl + "/" + empEl.getAttributes());
        }
        if (isEmpty(id) && !isEmpty(lcsid)) {
            id = lcsid;
        }
        YahooAddressBookEntry user = new YahooAddressBookEntry(id, firstName, lastName, 
                nickName, groupName);
        // log.trace("firstname: " + firstName + ", lastname: " + lastName + ", nickname: " + 
        //        nickName + ", groupName: " + groupName);

        return user;
    }

    private boolean isEmpty(String id) {
        return id == null || id.length() == 0;
    }

    private String getTextValue(Element ele, String tagName) {
//        String textVal = null;
        return ele.getAttribute(tagName);
        // if(nl != null && nl.getLength() > 0) {
        // Element el = (Element)nl.item(0);
        // textVal = el.getFirstChild().getNodeValue();
        // }
        //
        // return textVal;
    }

}
