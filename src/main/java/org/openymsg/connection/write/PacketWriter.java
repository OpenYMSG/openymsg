package org.openymsg.connection.write;

public interface PacketWriter {
	void execute(Message message);

	void drainAndExecute(Message message);
}
