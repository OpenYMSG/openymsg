package org.openymsg.network;

import java.net.Socket;

import org.openymsg.config.SessionConfig;
import org.openymsg.connection.ConnectionInfo;

public class TestingConnectionBuilder implements ConnectionBuilder {
	private ConnectionInfo connectionInfo = new ConnectionInfo();
	private final boolean connect;
	
	
	public TestingConnectionBuilder(boolean connect) {
		this.connect = connect;
	}

	@Override
	public ConnectionInfo getHandlerStatus() {
		if (this.connect) {
			this.connectionInfo.setCapacityIpAddressConnected(new Socket());
		}
		return this.connectionInfo;
	}

	@Override
	public ConnectionBuilder with(SessionConfig config) {
		return this;
	}

	@Override
	public ConnectionBuilder useCapacityServers() {
		return this;
	}

	@Override
	public ConnectionBuilder useScsServers() {
		return this;
	}

	@Override
	public ConnectionHandler build() {
		return new TestingConnectionHandler();
	}

}
