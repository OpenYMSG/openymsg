package org.openymsg.execute;

import org.openymsg.network.ConnectionHandler;


public interface Executor extends PacketReader, PacketWriter {
	void execute(Request request);

	void schedule(Runnable runnable, int repeatTimeInMillis);

	void schedule(Message message, int repeatTimeInMillis);

	void initializeConnection(ConnectionHandler connection);

}
