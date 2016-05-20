package org.openymsg.connection.read;

import org.openymsg.network.YMSG9Packet;

public interface ConnectionReaderReceiver {

	void received(YMSG9Packet packet);

}
