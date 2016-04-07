package org.openymsg.connection.read;

import org.openymsg.network.YMSG9Packet;

public interface ReaderReceiver {

	void received(YMSG9Packet packet);

}
