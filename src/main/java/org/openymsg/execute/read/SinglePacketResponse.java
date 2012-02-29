package org.openymsg.execute.read;

import org.openymsg.network.YMSG9Packet;

public interface SinglePacketResponse {
	void execute(YMSG9Packet packet);
}
