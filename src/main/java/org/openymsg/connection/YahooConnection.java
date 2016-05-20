package org.openymsg.connection;

import org.openymsg.connection.read.PacketReaderService;
import org.openymsg.connection.write.PacketWriterService;

public interface YahooConnection extends PacketReaderService, PacketWriterService {
	void shutdown();
}
