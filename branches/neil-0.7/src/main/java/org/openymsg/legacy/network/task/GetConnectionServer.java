package org.openymsg.legacy.network.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetConnectionServer {
    private static final String CAPACITY_URL = "http://%s/capacity";
    private static final String CS_IP_ADDRESS = "CS_IP_ADDRESS=";
    private static final String COLO_CAPACITY = "COLO_CAPACITY_";
    private GetConnectionServerConfig config;
    private static final Log log = LogFactory.getLog(GetConnectionServer.class);

    public GetConnectionServer() {
        this.config = new GetConnectionServerConfig() {
            public List<String> getClusterHosts() {
                List<String> hosts = new ArrayList<String>();
                hosts.add("vcs1.msg.yahoo.com");
                hosts.add("vcs2.msg.yahoo.com");
                hosts.add("httpvcs1.msg.yahoo.com");
                hosts.add("httpvcs2.msg.yahoo.com");
                return hosts;
            }
        };
    }

    public GetConnectionServer(GetConnectionServerConfig config) {
        this.config = config;
    }
    
    public String getIpAddress() {
        for (String host : config.getClusterHosts()) {
            String ipAddress = getIpAddress(host);
            if (ipAddress != null) {
                return ipAddress;
            }
        }
        return null;
    }

    protected String getIpAddress(String host) {
        String url = String.format(CAPACITY_URL, host);
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

                    StringTokenizer toks = new StringTokenizer(out.toString(), "\r\n");
                    if (toks.countTokens() <= 0) {
                        log.warn("Failed getting tokens for: " + url);
                        return null;
                    }

                    String coloCapacityString = getTokenValue(COLO_CAPACITY, toks);
                    if (coloCapacityString == null || coloCapacityString.isEmpty()) {
                        log.error("No colo capacity found for: " + host);
                    } else {
                        Integer coloCapacity = new Integer(coloCapacityString);
                        log.info("Colo Capacity is: " + coloCapacity + " for: " + host);
                    }

                    String ipAddress = getTokenValue(CS_IP_ADDRESS, toks);
                    if (ipAddress == null || ipAddress.isEmpty()) {
                        log.error("No ipAddress found for: " + host);
                        return null;
                    } else {
                        log.info("ipAddress is: " + ipAddress + " for: " + host);
                        return ipAddress;
                    }
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

    private String getTokenValue(String prefix, StringTokenizer toks) {
        if (toks.hasMoreElements()) {
            String token = toks.nextToken();
            log.debug(prefix + " token: " + token + ".");
            String string = token.substring(prefix.length());
            log.debug(prefix + " string: " + string + ".");
            return string;
        } else {
            log.error("No more tokens for: " + prefix);
        }
        return null;
    }
    
}
