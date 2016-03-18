package org.openymsg.connection.read;

import org.openymsg.network.YMSG9Packet;

import java.util.List;

public interface MultiplePacketResponse {
	void execute(List<YMSG9Packet> packets);

	boolean isFinished(long status);
}
