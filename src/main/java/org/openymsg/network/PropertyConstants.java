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
 * Name and default constants for system properties. These can be overridden by
 * using the java.lang.System static methods or the -D command line switch.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface PropertyConstants {
	/**
	 * Names of properties
	 */
	public final static String DIRECT_HOST = "openymsg.network.directHost";

	public final static String DIRECT_PORTS = "openymsg.network.directPorts";

	public final static String HTTP_HOST = "openymsg.network.httpHost";

	public final static String HTTP_PROXY_AUTH = "openymsg.network.httpProxyAuth";

	public final static String FT_HOST = "openymsg.network.fileTransferHost";

	/**
	 * Property defaults
	 */
	// Originally cs12.msg.sc5.yahoo.com:5050, then scs.yahoo.com
	public final static String DIRECT_HOST_DEFAULT = "scs.msg.yahoo.com";

	public final static int[] DIRECT_PORTS_DEFAULT = { 5050, 23, 25, 80 };

	public final static String HTTP_HOST_DEFAULT = "http.pager.yahoo.com";

	public final static String FT_HOST_DEFAULT = "filetransfer.msg.yahoo.com";
}
