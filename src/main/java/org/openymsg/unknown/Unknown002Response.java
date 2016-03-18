package org.openymsg.unknown;

import org.openymsg.connection.read.SinglePacketResponse;
import org.openymsg.network.YMSG9Packet;

public class Unknown002Response implements SinglePacketResponse {
	@Override
	public void execute(YMSG9Packet packet) {}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object arg0) {
		return arg0 instanceof Unknown002Response;
	}
}
