package org.openymsg.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.SessionConfig;

/**
 * Ask the capacity servers for an ip address to open a socket.
 * Results from the http request look like
 * COLO_CAPACITY=1\r\n
 * CS_IP_ADDRESS=10.6.108.121\r\n
 * @author neilhart
 *
 */
public class CapacityServers {
    private static final String CS_IP_ADDRESS = "CS_IP_ADDRESS=";
    private static final String COLO_CAPACITY = "COLO_CAPACITY_";
    private static final Log log = LogFactory.getLog(CapacityServers.class);
    private SessionConfig config;

    public CapacityServers(SessionConfig config) {
        this.config = config;
    }
    
    public Collection<String> getIpAddresses() {
        HashSet<String> ipAddresses = new HashSet<String>();
        for (String host : config.getCapacityHosts()) {
            String ipAddress = getIpAddress(host);
            if (ipAddress != null) {
                ipAddresses.add(ipAddress);
            }
        }
        return ipAddresses;
    }

    private String getIpAddress(String host) {
        String url = String.format(NetworkConstants.CAPACITY_URL_FORMAT, host);
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();

            if (uc instanceof HttpURLConnection) {
                int responseCode = ((HttpURLConnection) uc).getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = uc.getInputStream();

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int read = -1;
                    byte[] buff = new byte[256];
                    while ((read = in.read(buff)) != -1) {
                        out.write(buff, 0, read);
                    }
                    in.close();

                    return readIpAddress(host, url, out);
                }
                else {
                    log.error("Failed opening url: " + url + " return code: " + responseCode);
                }
            }
            else {
                Class<? extends URLConnection> ucType = null;
                if (uc != null) {
                    ucType = uc.getClass();
                }
                log.error("Failed opening  url: " + url + " returns: " + ucType);
            }
        }
        catch (Exception e) {
            log.error("Failed url: " + url, e);
        }
        return null;
    }

	protected String readIpAddress(String host, String url, ByteArrayOutputStream out) {
		StringTokenizer toks = new StringTokenizer(out.toString(), "\r\n");
		if (toks.countTokens() <= 0) {
		    log.warn("Failed getting tokens for: " + url);
		    return null;
		}

		String coloCapacityString = getTokenValue(COLO_CAPACITY, toks);
		Integer coloCapacity = null;
		if (coloCapacityString == null || coloCapacityString.isEmpty()) {
		    log.error("No colo capacity found for: " + host);
		} else {
		    coloCapacity = new Integer(coloCapacityString);
		    if (coloCapacity > 1) {
		    	log.info("Colo Capacity is greater than 1");
		    }
		}

		String ipAddress = getTokenValue(CS_IP_ADDRESS, toks);
		if (ipAddress == null || ipAddress.isEmpty()) {
		    log.error("No ipAddress found for: " + host);
		    return null;
		} else {
		    log.debug("Colo Capacity is: " + coloCapacity + ", ipAddress is: " + ipAddress + " for: " + host);
		    return ipAddress;
		}
	}

    private String getTokenValue(String prefix, StringTokenizer toks) {
        if (toks.hasMoreElements()) {
            String token = toks.nextToken();
            log.trace(prefix + " token: " + token + ".");
            String string = token.substring(prefix.length());
            log.trace(prefix + " string: " + string + ".");
            return string;
        } else {
            log.error("No more tokens for: " + prefix);
        }
        return null;
    }
    
}
