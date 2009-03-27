package org.openymsg.network.connection;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

import org.openymsg.network.ServiceType;
import org.openymsg.v1.network.ConnectionHandler;


public abstract class YMSGSocket<T extends YMSGInputStream> {
	public final static byte PROTOCOL_12 = 0x0c; 
	public final static byte PROTOCOL_15 = 0x0f; 
	public final static byte PROTOCOL_16 = 0x10; 
	public final static byte[] VERSION_12 = { 0x00, PROTOCOL_12, 0x00, 0x00 };
	public final static byte[] VERSION_15 = { 0x00, PROTOCOL_15, 0x00, 0x00 };
	public final static byte[] VERSION_16 = { 0x00, PROTOCOL_16, 0x00, 0x00 };
	private String host; // Yahoo IM host
	private int port; // Yahoo IM port
	private int[] fallbackPorts;
	private Socket socket; // Network connection

	protected T ips; // For receiving messages
	private DataOutputStream ops; // For sending messages

	public YMSGSocket(String host, int port, int[] fallbackPorts) {
		this.host = host;
		this.port = port;
		this.fallbackPorts = fallbackPorts;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Opens a socket to Yahoo IM, or throws an exception. If fallback ports are
	 * to be used, will attempt each port in turn - upon failure will return the
	 * last exception (the one for the final port).
	 */
	public void open() throws SocketException, IOException {
		try {
			socket = new Socket(host, port);
		} catch (SocketException e) {
			if (fallbackPorts.length == 0) {
				throw e;
			}
			int i = 0;
			while (socket == null) {
				try {
					socket = new Socket(host, fallbackPorts[i]);
					port = fallbackPorts[i];
				} catch (SocketException ex) {
					socket = null;
					i++;
					if (i >= fallbackPorts.length)
						throw ex;
				}
			}
		}

		ips = createInputStream(socket.getInputStream());
		ops = new DataOutputStream(new BufferedOutputStream(socket
				.getOutputStream()));
	}

	protected abstract T createInputStream(InputStream inputStream);
	
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		this.socket = null;
		this.ips = null;
		this.ops = null;
	}

	/**
	 * Note: the term 'packet' here refers to a YMSG message, not a TCP packet
	 * (although in almost all cases the two will be synonymous). This is to
	 * avoid confusion with a 'YMSG message' - the actual discussion packet.
	 * 
	 * service - the Yahoo service number status - the Yahoo status number (not
	 * sessionStatus!) body - the payload of the packet
	 * 
	 * Note: it is assumed that 'ops' will have been set by the time this method
	 * is called.
	 */
	public void sendPacket(PacketBodyBuffer body, int version, ServiceType service,
			long status, long sessionId) throws IOException {
		byte[] b = body.getBuffer();
		// Because the buffer is held at class member level, this method
		// is not automatically thread safe. Besides, we should be only
		// sending one message at a time!
		synchronized (ops) {
			// 20 byte header
			ops.write(ConnectionHandler.MAGIC, 0, 4); // Magic code 'YMSG'
			ops.write(getBits(version), 0, 4); // Version
			ops.writeShort(get16Bit(b.length)); // Body length (16 bit unsigned)
			ops.writeShort(get16Bit(service.getValue())); // Service ID (16
			// bit unsigned
			ops.writeInt(get32Bit(status)); // Status (32 bit
			// unsigned)
			ops.writeInt(get32Bit(sessionId)); // Session id (32
			// bit unsigned)
			// Then the body...
			ops.write(b, 0, b.length);
			// Now send the buffer
			ops.flush();
		}
	}

	private byte[] getBits(int version) {
		if (version == 12) {
			return VERSION_12;
		}
		else if (version == 15) {
			return VERSION_15;
		}
		else if (version == 16) {
			return VERSION_16;
		}
		throw new IllegalArgumentException("version: " + version + " not understood");
	}

	private int get32Bit(long value) {
		return (int) (value & 0xFFFFFFFF);
	}
	private int get16Bit(int value) {
		return (short) (value & 0xFFFF);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Direct connection: ").append(host)
				.append(":").append(port);
		return sb.toString();
	}

}
