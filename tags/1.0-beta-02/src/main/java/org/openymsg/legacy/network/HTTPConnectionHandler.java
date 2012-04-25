/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.legacy.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class HTTPConnectionHandler extends ConnectionHandler {
    private static final Log log = LogFactory.getLog(HTTPConnectionHandler.class);
    private final static long IDLE_TIMEOUT = 30 * 1000;

    private static final String HTTP_HEADER_POST = "POST http://" + Util.httpHost() + "/notify HTTP/1.0"
            + NetworkConstants.END;

    private static final String HTTP_HEADER_AGENT = "User-Agent: " + NetworkConstants.USER_AGENT + NetworkConstants.END;

    private static final String HTTP_HEADER_HOST = "Host: " + Util.httpHost() + NetworkConstants.END;

    private static final String HTTP_HEADER_PROXY_AUTH = "Proxy-Authorization: " + Util.httpProxyAuth()
            + NetworkConstants.END;

    private Session session; // Associated session object

    private String proxyHost; // HTTP proxy host name

    private int proxyPort; // HTTP proxy post

    private long lastFetch; // Time of last packet fetch

    private final Queue<YMSG9Packet> packets; // Incoming packet queue

    private boolean connected = false; // Sending/receiving data?

    private String cookie = null; // HTTP cookie field

    private long identifier = 0; // Some kind of id, from LOGON incoming

    private Notifier notifierThread; // Send IDLE packets on timeout

    /**
     * Creates a new instance based on the HTTP proxy settings as configured in property settings.
     */
    public HTTPConnectionHandler() {
        this(Util.httpProxyHost(), Util.httpProxyPort());
    }

    /**
     * Creates a new instance, using tshe host and port as configured. This constructor may contain a collection of
     * hosts of hosts to not proxy.
     * 
     * @param proxyHost
     *            Host to use.
     * @param proxyPort
     *            Port to use.
     * @param blacklist
     *            Option set of hosts explicitly forbidden to use.
     */
    public HTTPConnectionHandler(String proxyHost, int proxyPort, Collection<String> blacklist) {
        if (proxyHost == null || proxyHost.length() == 0 || proxyPort <= 0 || proxyPort > 65535) {
            throw new IllegalArgumentException("Bad HTTP proxy properties");
        }
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;

        packets = new LinkedList<YMSG9Packet>();
        connected = false;

        // Names are prefixed with "http." for 1.3 and after
        Properties p = System.getProperties();
        p.put(NetworkConstants.PROXY_HOST_OLD, this.proxyHost);
        p.put(NetworkConstants.PROXY_HOST, this.proxyHost);
        p.put(NetworkConstants.PROXY_PORT_OLD, this.proxyPort + "");
        p.put(NetworkConstants.PROXY_PORT, this.proxyPort + "");
        p.put(NetworkConstants.PROXY_SET, "true");

        // Expand list to item|item|... , then store
        if (blacklist != null) {
            StringBuffer sb = new StringBuffer();
            for (String host : blacklist) {
                sb.append(host).append('|');
            }
            sb.setLength(sb.length() - 1);

            System.getProperties().put(NetworkConstants.PROXY_NON, sb.toString());
        }
    }

    /**
     * Creates a new instance, using tshe host and port as configured.
     * 
     * @param proxyHost
     *            Host to use.
     * @param proxyPort
     *            Port to use.
     */
    public HTTPConnectionHandler(String proxyHost, int proxyPort) {
        this(proxyHost, proxyPort, null);
    }

    /**
     * High level method for setting proxy authorization, for users behind firewalls which require credentials to access
     * a HTTP proxy.
     */
    public static void setProxyAuthorizationProperty(String method, String username, String password)
            throws UnsupportedOperationException {
        if (method.equalsIgnoreCase("basic")) {
            String a = username + ":" + password;
            String s = "Basic " + Util.base64(a.getBytes());
            System.setProperty(PropertyConstants.HTTP_PROXY_AUTH, s);
        }
        else {
            throw new UnsupportedOperationException("Method " + method + " unsupported.");
        }
    }

    /**
     * Session calls this when a connection handler is installed
     */
    @Override
    void install(Session session) {
        this.session = session;
    }

    /**
     * Do nothing more than make a note that we are on/off line
     */
    @Override
    void open(boolean searchForAddress) {
        connected = true;
        synchronized (this) // In case two threads call open()
        {
            if (notifierThread == null) notifierThread = new Notifier("HTTP Notifier");
        }
    }

    @Override
    void close() {
        connected = false;
        synchronized (this) // In case two threads call close()
        {
            if (notifierThread != null) {
                notifierThread.quitFlag = true;
                notifierThread = null;
            }
        }
    }

    /**
     * The only time Yahoo can actually send us any packets is when we send it some. Yahoo encodes its packets in a POST
     * body - the format is the same binary representation used for direct connections (with one or two extra codes).
     * 
     * After posting a packet, the connection will receive a HTTP response who's payload consists of four bytes followed
     * by zero or more packets. The first byte of the four is a count of packets encoded in the following body.
     * 
     * Each incoming packet is transfered to a queue, where receivePacket() takes them off - thus preserving the effect
     * that input and output packets are being received independently, as with other connection handlers. As
     * readPacket() can throw an exception, these are caught and transfered onto the queue too, then rethrown by
     * receivePacket() .
     */
    @Override
    synchronized void sendPacket(PacketBodyBuffer body, ServiceType service, long status, long sessionID)
            throws IOException, IllegalStateException {
        if (!connected) throw new IllegalStateException("Not logged in");

        if (filterOutput(body, service)) return;
        byte[] b = body.getBuffer();

        Socket soc = new Socket(proxyHost, proxyPort);
        PushbackInputStream pbis = new PushbackInputStream(soc.getInputStream());
        DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

        // HTTP header
        dos.writeBytes(HTTP_HEADER_POST);
        dos.writeBytes("Content-length: " + (b.length + NetworkConstants.YMSG9_HEADER_SIZE) + NetworkConstants.END);
        dos.writeBytes(HTTP_HEADER_AGENT);
        dos.writeBytes(HTTP_HEADER_HOST);
        if (HTTP_HEADER_PROXY_AUTH != null) dos.writeBytes(HTTP_HEADER_PROXY_AUTH);
        if (cookie != null) dos.writeBytes("Cookie: " + cookie + NetworkConstants.END);
        dos.writeBytes(NetworkConstants.END);
        // YMSG9 header
        dos.write(NetworkConstants.MAGIC, 0, 4);
        dos.write(NetworkConstants.VERSION_HTTP, 0, 4);
        dos.writeShort(b.length & 0xffff);
        dos.writeShort(service.getValue() & 0xffff);
        dos.writeInt((int) (status & 0xffffffff));
        dos.writeInt((int) (sessionID & 0xffffffff));
        // YMSG9 body
        dos.write(b, 0, b.length);
        dos.flush();

        // HTTP response header
        String s = readLine(pbis);
        if (s == null || s.indexOf(" 200 ") < 0) // Not "HTTP/1.0 200 OK"
        {
            throw new IOException("HTTP request returned didn't return OK (200): " + s);
        }
        while (s != null && s.trim().length() > 0)
            // Read past header
            s = readLine(pbis);
        // Payload count
        byte[] code = new byte[4];
        int res = pbis.read(code, 0, 4); // Packet count (Little-Endian?)
        if (res < 4) {
            throw new IOException("Premature end of HTTP data");
        }
        int count = code[0];
        // Payload body
        YMSG9InputStream yip = new YMSG9InputStream(pbis);
        YMSG9Packet pkt;
        for (int i = 0; i < count; i++) {
            pkt = yip.readPacket();
            if (!filterInput(pkt)) {
                if (!packets.add(pkt)) {
                    throw new IllegalArgumentException("Unable to add data to the packetQueue!");
                }
            }
        }

        soc.close();

        // Reset idle timeout
        lastFetch = System.currentTimeMillis();
    }

    // Read one line of text, terminating in usual \r \n combinations
    private String readLine(PushbackInputStream pbis) throws IOException {
        int c = pbis.read();
        StringBuffer sb = new StringBuffer();
        while (c != '\n' && c != '\r') {
            sb.append((char) c);
            c = pbis.read();
        }
        // Check next character
        int c2 = pbis.read();
        if ((c == '\n' && c2 != '\r') || (c == '\r' && c2 != '\n')) pbis.unread(c2);
        return sb.toString();
    }

    /**
     * This method blocks until there is a packet to deliver, during which time it releases its object lock.
     */
    @Override
    YMSG9Packet receivePacket() throws IOException {
        if (!connected) throw new IllegalStateException("Not logged in");
        while (true) {
            synchronized (this) {
                if (packets.size() > 0) {
                    Object o = packets.poll();
                    if (o instanceof IOException) throw (IOException) o;
                    return (YMSG9Packet) o;
                }
            }
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * ConnectionHandler methods end
     */

    /**
     * Sometimes packets need to be altered, either going in or out. Typically this is about reading or adding extra
     * HTTP specific content which Yahoo uses. These methods are called to perform the necessary manipulations.
     * 
     * Returns true if this packet should be surpressed.
     * 
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private boolean filterOutput(PacketBodyBuffer body, ServiceType service) throws UnsupportedEncodingException,
            IOException {
        switch (service) {
        case ISBACK:
        case LOGOFF:
            // Do not send ISBACK or LOGOFF
            return true;
        default:
            break;
        }
        if (identifier > 0) body.addElement("24", identifier + "");
        return false;
    }

    private boolean filterInput(YMSG9Packet pkt) {
        switch (pkt.service) {
        case LIST:
            // Remember cookie and send it in subsequent packets
            String[] cookieArr = extractCookies(pkt);
            cookie = cookieArr[NetworkConstants.COOKIE_Y] + "; " + cookieArr[NetworkConstants.COOKIE_T];
            break;
        case LOGON:
            // Remember the 24 tag and send it in subsequent packets
            identifier = Long.parseLong(pkt.getValue("24"));
            break;
        case MESSAGE:
            // When sending a message we often get a 0x06 packet back, empty
            // or containing the status tag (66) of friend we messaged.
            if (pkt.getValue("14") == null) {
                if (pkt.getValue("10") != null)
                    pkt.service = ServiceType.ISBACK;
                else if (pkt.body.length == 0) return true;
            }
            break;
        default:
            // do nothing
            break;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("HTTP connection: ").append(proxyHost).append(":").append(proxyPort);
        return sb.toString();
    }

    /**
     * This thread fires off a IDLE packet every thirty seconds. This is because the only way the server can deliver us
     * any incoming packets is on the input stream of a HTTP connection we have made ourselves.
     */
    class Notifier extends Thread {
        volatile boolean quitFlag = false;

        Notifier(String nm) {
            super(nm);
            lastFetch = System.currentTimeMillis();
            this.start();
        }

        @Override
        public void run() {
            while (!quitFlag) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    // ignore
                }
                long t = System.currentTimeMillis();
                if (!quitFlag && connected && (t - lastFetch > IDLE_TIMEOUT)
                        && session.getSessionStatus() == SessionState.LOGGED_ON) {
                    try {
                        session.transmitIdle();
                    }
                    catch (IOException e) {
                        log.error(e, e);
                    }
                }
            }
        }
    }
}
