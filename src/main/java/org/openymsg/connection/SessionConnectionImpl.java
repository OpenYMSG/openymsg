package org.openymsg.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.connection.read.PacketReaderService;
import org.openymsg.connection.read.ReaderRegistry;
import org.openymsg.connection.write.PacketWriter;
import org.openymsg.connection.write.PacketWriterService;

public class SessionConnectionImpl implements SessionConnection, YahooConnection {
	/** logger */
	private static final Log log = LogFactory.getLog(SessionConnectionImpl.class);
	private final ConnectionStateAndDetails connectionState;
	private PacketWriterService writerService;
	private PacketReaderService readerService;

	public SessionConnectionImpl(ConnectionStateAndDetails connectionState, PacketWriterService writerService,
			PacketReaderService readerService) {
		this.connectionState = connectionState;
		this.writerService = writerService;
		this.readerService = readerService;
		this.connectionState.setUnstarted();
		this.connectionState.setConnecting();
	}

	@Override
	public ConnectionState getConnectionState() {
		return connectionState.getState();
	}

	@Override
	public ConnectionInfo getConnectionInfo() {
		return connectionState.getInfo();
	}

	@Override
	public PacketWriter getPacketWriter() {
		return this.writerService.getPacketWriter();
	}

	@Override
	public ReaderRegistry getReaderRegistry() {
		return this.readerService.getReaderRegistry();
	}

	@Override
	public void shutdown() {
		this.connectionState.shutdown();
	}
}
