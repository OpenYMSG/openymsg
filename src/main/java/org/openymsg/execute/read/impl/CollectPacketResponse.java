package org.openymsg.execute.read.impl;

import java.util.ArrayList;
import java.util.List;

import org.openymsg.execute.MultiplePacketResponse;
import org.openymsg.execute.SinglePacketResponse;
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
			System.err.println("status is: " + packet.status);
		}
		if (this.finished) {
			this.response.execute(packets);
			this.packets.clear();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((response == null) ? 0 : response.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		CollectPacketResponse other = (CollectPacketResponse) obj;
		if (response == null) {
			if (other.response != null) return false;
		}
		else if (!response.equals(other.response)) return false;
		return true;
	}
}
