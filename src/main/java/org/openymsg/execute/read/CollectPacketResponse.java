package org.openymsg.execute.read;

import java.util.ArrayList;
import java.util.List;

import org.openymsg.network.YMSG9Packet;

public class CollectPacketResponse implements SinglePacketResponse {
	private MultiplePacketResponse response;
	private boolean finished = false;
	protected List<YMSG9Packet> packets = new ArrayList<YMSG9Packet>();

	public CollectPacketResponse(MultiplePacketResponse response) {
		this.response = response;
	}

	@Override
	public void execute(YMSG9Packet packet) {
		this.packets.add(packet);
		if (packet.status == response.getProceedStatus()) {
			this.finished = true;
		} else {
			// TODO log
			// System.err.println("status is: " + packet.status);
		}
		if (this.finished) {
			this.response.execute(packets);
			this.packets.clear();
		}
	}
}
