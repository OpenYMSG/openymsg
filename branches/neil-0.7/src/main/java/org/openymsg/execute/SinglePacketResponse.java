package org.openymsg.execute;

import org.openymsg.network.YMSG9Packet;

public interface SinglePacketResponse {
	void execute(YMSG9Packet packet);
}
