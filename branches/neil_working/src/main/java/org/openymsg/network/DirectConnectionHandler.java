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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class DirectConnectionHandler extends ConnectionHandler {
    private String host; // Yahoo IM host

    private int port; // Yahoo IM port

    private boolean dontUseFallbacks = false; // Don't use fallback port

    private Socket socket; // Network connection

    private YMSG9InputStream ips; // For receiving messages

    private DataOutputStream ops; // For sending messages

    private static final Log log = LogFactory.getLog(DirectConnectionHandler.class);

    public DirectConnectionHandler(String h, int p) {
        host = h;
        port = p;
        dontUseFallbacks = true;
    }

    public DirectConnectionHandler(int p) {
        this(Util.directHost(), p);
    }

    public DirectConnectionHandler(boolean fl) {
        this();
        dontUseFallbacks = fl;
    }

    public DirectConnectionHandler() {
        this(Util.directHost(), Util.directPort());
        dontUseFallbacks = false;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    /**
     * Session calls this when a connection handler is installed
     */
    @Override
    void install(Session ss) {
        // session=ss;
    }

    /**
     * Opens a socket to Yahoo IM, or throws an exception. If fallback ports are to be used, will attempt each port in
     * turn - upon failure will return the last exception (the one for the final port).
     */
    @Override
    void open() throws SocketException, IOException {
        if (dontUseFallbacks) {
            socket = new Socket(host, port);
        }
        else {
            int[] fallbackPorts = Util.directPorts();
            int i = 0;
            while (socket == null) {
                try {
                    socket = new Socket(host, fallbackPorts[i]);
                    port = fallbackPorts[i];
                }
                catch (SocketException e) {
                    socket = null;
                    i++;
                    if (i >= fallbackPorts.length) throw e;
                }
            }
        }
        log.debug("Source socket: " + socket.getLocalSocketAddress() + " yahoo socket: " + socket.getInetAddress() + ":"
                + this.socket.getPort());
        ips = new YMSG9InputStream(socket.getInputStream());
        ops = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    void close() throws IOException {
        if (socket != null) socket.close();
        socket = null;
        ips = null;
        ops = null;
    }

    /**
     * Note: the term 'packet' here refers to a YMSG message, not a TCP packet (although in almost all cases the two
     * will be synonymous). This is to avoid confusion with a 'YMSG message' - the actual discussion packet.
     * 
     * service - the Yahoo service number status - the Yahoo status number (not sessionStatus!) body - the payload of
     * the packet
     * 
     * Note: it is assumed that 'ops' will have been set by the time this method is called.
     */
    @Override
    protected void sendPacket(PacketBodyBuffer body, ServiceType service, long status, long sessionId)
            throws IOException {
        byte[] b = body.getBuffer();
        // Because the buffer is held at class member level, this method
        // is not automatically thread safe. Besides, we should be only
        // sending one message at a time!
        synchronized (ops) {
            // 20 byte header
            ops.write(NetworkConstants.MAGIC, 0, 4); // Magic code 'YMSG'
            ops.write(NetworkConstants.VERSION, 0, 4); // Version
            ops.writeShort(b.length & 0xFFFF); // Body length (16 bit unsigned)
            ops.writeShort(service.getValue() & 0xFFFF); // Service ID (16
            // bit unsigned
            ops.writeInt((int) (status & 0xFFFFFFFF)); // Status (32 bit
            // unsigned)
            ops.writeInt((int) (sessionId & 0xFFFFFFFF)); // Session id (32
            // bit unsigned)
            // Then the body...
            ops.write(b, 0, b.length);
            // Now send the buffer
            ops.flush();
        }
    }

    /**
     * Return a Yahoo message
     */
    @Override
    protected YMSG9Packet receivePacket() throws IOException {
        return ips.readPacket();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Direct connection: ").append(host).append(":").append(port);
        return sb.toString();
    }

    /**
     * Allow changing the host to open a new connection
     * @return ip address
     */
    public void setHost(String host) {
        this.host = host;
        try {
            close();
        } catch (Exception ex) {
            // silently fail;
        }
    }
}
