package org.openymsg.execute;

public interface Executor extends PacketReader, PacketWriter, ExecutorStateMonitoring {
	void execute(Request request);

	void schedule(Runnable runnable, int repeatTimeInMillis);

	void schedule(Message message, int repeatTimeInMillis);

}
