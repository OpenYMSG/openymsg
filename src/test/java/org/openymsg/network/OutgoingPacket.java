package org.openymsg.network;

public class OutgoingPacket {

	private final PacketBodyBuffer body;
	private final ServiceType service;
	private final MessageStatus status;

	public OutgoingPacket(PacketBodyBuffer body, ServiceType service, MessageStatus status) {
		this.body = body;
		this.service = service;
		this.status = status;
	}

	public PacketBodyBuffer getBody() {
		return body;
	}

	public ServiceType getService() {
		return service;
	}

	public MessageStatus getStatus() {
		return status;
	}

}
