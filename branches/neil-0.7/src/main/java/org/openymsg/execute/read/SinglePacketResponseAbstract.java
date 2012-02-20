package org.openymsg.execute.read;

import org.openymsg.execute.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public abstract class SinglePacketResponseAbstract implements SinglePacketResponse {

	protected YMSG9Packet packet;

	public void execute(YMSG9Packet packet) {
		this.packet = packet;
		this.execute();
		this.packet = null;
	}

	protected abstract void execute();


}
