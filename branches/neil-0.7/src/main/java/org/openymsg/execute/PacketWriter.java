package org.openymsg.execute;




public interface PacketWriter {
	void execute(Message message);
	void shutdown();
}
