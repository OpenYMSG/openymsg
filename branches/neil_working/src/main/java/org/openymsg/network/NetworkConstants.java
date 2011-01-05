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
package org.openymsg.network;

/**
 * Various constant settings for network code.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public abstract class NetworkConstants {

    public final static byte PROTOCOL = 0x10;

    final static byte[] MAGIC = { 'Y', 'M', 'S', 'G' };

    final static byte[] VERSION = { 0x00, PROTOCOL, 0x00, 0x00 };

    final static byte[] VERSION_HTTP = { PROTOCOL, 0x00, (byte) 0xc8, 0x00 };

    public final static int YMSG9_HEADER_SIZE = 20;

    public final static String CLIENT_VERSION = "9.0.0.2152";
    public final static String CLIENT_VERSION_ID = "4194239";

    // File transfer
    /*
     * Now the property openymsg.network.httpFileTransferHost, accessed via Util.class public final static String
     * FILE_TF_HOST = "filetransfer.msg.yahoo.com"; public final static String FILE_TF_URL =
     * "http://"+FILE_TF_HOST+":80/notifyft";
     */
    public final static String FILE_TF_PORTPATH = ":80/notifyft";

    public final static String FILE_TF_USER = "FILE_TRANSFER_SYSTEM";

    // HTTP
    public final static String USER_AGENT = "Mozilla/4.5 [en] (X11; U; FreeBSD 2.2.8-STABLE i386)";

    public final static String END = "\n"; // Line terminator

    // HTTP proxy property names
    public final static String PROXY_HOST_OLD = "proxyHost";

    public final static String PROXY_PORT_OLD = "proxyPort";

    public final static String PROXY_HOST = "http.proxyHost";

    public final static String PROXY_PORT = "http.proxyPort";

    public final static String PROXY_SET = "proxySet";

    public final static String PROXY_NON = "http.nonProxyHosts";

    // SOCKS proxy property names
    public final static String SOCKS_HOST = "socksProxyHost";

    public final static String SOCKS_PORT = "socksProxyPort";

    public final static String SOCKS_SET = "socksProxySet";

    // Cookies in array (see Session.getCookies())
    public final static int COOKIE_Y = 0;

    public final static int COOKIE_T = 1;

    public final static int COOKIE_C = 2;

    // Default timouts (seconds)
    public final static int LOGIN_TIMEOUT = 60;

    /**
     * Amount of seconds between sending subsequent keep-alive packets.
     */
    public final static int KEEPALIVE_TIMEOUT_IN_SECS = 60; // 1 minute
    public final static int PING_TIMEOUT_IN_SECS = 58 * 60; // 1 hour

    // Buzz string
    public final static String BUZZ = "<ding>";
}
