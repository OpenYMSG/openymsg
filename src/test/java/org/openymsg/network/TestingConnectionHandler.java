package org.openymsg.network;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestingConnectionHandler implements ConnectionHandler {
	/** logger */
	private static final Log log = LogFactory.getLog(TestingConnectionHandler.class);
	private LinkedBlockingQueue<YMSG9Packet> incomingPackets = new LinkedBlockingQueue<YMSG9Packet>();
	private LinkedBlockingQueue<OutgoingPacket> outgoingPackets = new LinkedBlockingQueue<OutgoingPacket>();

	@Override
	public void sendPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status) {
		OutgoingPacket packet = new OutgoingPacket(body, service, status);
		this.outgoingPackets.add(packet);
	}

	@Override
	public YMSG9Packet receivePacket() {
		return incomingPackets.poll();
	}

	public void addPacket(YMSG9Packet packet) {
		this.incomingPackets.add(packet);
	}

	public OutgoingPacket getOutgoingPacket() throws InterruptedException {
		return this.outgoingPackets.poll(500, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() {
		log.info("shutdown");
	}

	@Override
	public void addListener(ConnectionHandlerCallback listener) {
		log.info("no op add listener");
	}

	@Override
	public void removeListener(ConnectionHandlerCallback listener) {
		log.info("no op remove listener");
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
