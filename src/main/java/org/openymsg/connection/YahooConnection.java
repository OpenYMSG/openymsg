package org.openymsg.connection;

import org.openymsg.connection.read.PacketReader;
import org.openymsg.connection.write.PacketWriter;

public interface YahooConnection extends SessionConnection, PacketWriter, PacketReader {
}
