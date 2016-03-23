package org.openymsg.network.direct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.ConnectionEndedReason;
import org.openymsg.network.ConnectionHandler;
import org.openymsg.network.ConnectionHandlerCallback;
import org.openymsg.network.MessageStatus;
import org.openymsg.network.NetworkConstants;
import org.openymsg.network.PacketBodyBuffer;
import org.openymsg.network.ServiceType;
import org.openymsg.network.YMSG9Packet;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class DirectConnectionHandler implements ConnectionHandler {
	/** logger */
	private static final Log log = LogFactory.getLog(DirectConnectionHandler.class);
	private Socket socket;
	private YMSG9InputStream ips;
	private DataOutputStream ops;
	private long sessionId;
	private Set<ConnectionHandlerCallback> listeners = new HashSet<ConnectionHandlerCallback>();
	private SocketLockChecker socketLockChecker;

	public DirectConnectionHandler(Socket socket, SocketLockChecker socketLockChecker) {
		this.socket = socket;
		this.socketLockChecker = socketLockChecker;
		// TODO monitor socket?
		try {
			ips = new YMSG9InputStream(socket.getInputStream());
			ops = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {// TODO handle failure
			log.info("Failed creating streams", e);
			this.notifyListeners(ConnectionEndedReason.SocketClosed);
		}
	}

	// TODO pass failure
	private void notifyListeners(ConnectionEndedReason reason) {
		synchronized (this.listeners) {
			for (ConnectionHandlerCallback listener : this.listeners) {
				listener.connectionEnded(reason);
			}
		}
	}

	/**
	 * Note: the term 'packet' here refers to a YMSG message, not a TCP packet (although in almost all cases the two
	 * will be synonymous). This is to avoid confusion with a 'YMSG message' - the actual discussion packet. service -
	 * the Yahoo service number status - the Yahoo status number (not sessionStatus!) body - the payload of the packet
	 * Note: it is assumed that 'ops' will have been set by the time this method is called.
	 */
	@Override
	public synchronized void sendPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status) {
		log.debug("Sent packet: Magic:YMSG Version:16 Length:" + body.getBuffer().length + " Service:" + service
				+ " Status:" + status + " SessionId:" + Long.toHexString(sessionId) + " " + body);
		byte[] b = body.getBuffer();
		// Because the buffer is held at class member level, this method
		// is not automatically thread safe. Besides, we should be only
		// sending one message at a time!
		// synchronized (ops) {
		// 20 byte header
		try {
			try {
				this.socketLockChecker.startWriting();
				ops.write(NetworkConstants.PROTOCOL, 0, 4); // Magic code 'YMSG'
				ops.write(NetworkConstants.VERSION, 0, 4); // Version
				ops.writeShort(b.length & 0xFFFF); // Body length (16 bit unsigned)
				ops.writeShort(service.getValue() & 0xFFFF); // Service ID (16
				// bit unsigned
				ops.writeInt((int) (status.getValue() & 0xFFFFFFFF)); // Status (32 bit
				// unsigned)
				ops.writeInt((int) (sessionId & 0xFFFFFFFF)); // Session id (32
				// bit unsigned)
				// Then the body...
				ops.write(b, 0, b.length);
				// Now send the buffer
				ops.flush();
			} finally {
				this.socketLockChecker.finishWriting();
			}
		} catch (IOException e) {
			log.info("sending packet", e);
			this.notifyListeners(ConnectionEndedReason.SocketClosed);
		}
	}

	/**
	 * Return a Yahoo message or null. Does not wait
	 */
	// TODO - handle 2001 - bad sign in using Fred
	// Feb 10, 2012 2:02:04 PM org.openymsg.network.ServiceType getServiceType
	// WARNING: No such ServiceType value '2001' (which is '7d1' in hex).
	// Magic:YMSG Version:0 Length:10 Service:null Status:-1 SessionId:0x30000000 [66] [1004]
	@Override
	public YMSG9Packet receivePacket() {
		try {
			if (ips == null) {
				log.warn("trying to pull when input stream is null");
				return null;
			}
			if (ips.isHoldingMessage()) {
				YMSG9Packet packet = ips.readPacket();
				if (this.sessionId == 0) {
					this.sessionId = packet.sessionId;
				} else if (this.sessionId != packet.sessionId) {
					log.error("Problem with not matching session ids: " + this.sessionId + " and " + packet.sessionId);
				}
				log.debug("receiving packet:" + packet);
				return packet;
			} else {
				log.trace("skipping with no message");
			}
		} catch (IOException e) {
			log.info("Failed reading connection", e);
			this.notifyListeners(ConnectionEndedReason.SocketClosed);
		} catch (UnknowServiceException e) {
			log.warn("unknown service: " + e.getPacket());
		}
		return null;
	}

	@Override
	public void shutdown() {
		try {
			this.ips.close();
			this.ops.close();
			this.socket.close();
		} catch (IOException e) {
			log.warn("Failed shutdown connection", e);
		}
		this.sessionId = 0;
		this.ips = null;
		this.ops = null;
		this.socket = null;
	}

	@Override
	public void addListener(ConnectionHandlerCallback listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(ConnectionHandlerCallback listener) {
		this.listeners.remove(listener);
	}

	@Override
	public boolean isDisconnected() {
		return socket == null;
	}

	@Override
	public boolean isLocked(int millisDuration) {
		boolean answer = this.socketLockChecker.isLocked(millisDuration);
		if (answer) {
			this.notifyListeners(ConnectionEndedReason.LockedSocket);
		}
		return answer;
	}
}
