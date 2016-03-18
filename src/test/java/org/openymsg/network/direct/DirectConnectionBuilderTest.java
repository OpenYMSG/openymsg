package org.openymsg.network.direct;

import org.junit.Before;
import org.junit.Test;
import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigSimple;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.network.ConnectionBuilder;

public class DirectConnectionBuilderTest {
	private SessionConfig config;
	private ConnectionBuilder builder;

	@Before
	public void setup() {
		config = new SessionConfigSimple();
		builder = new DirectConnectionBuilder();
	}

	@Test()
	// TODO needs internet
	public void connectViaCapacityServers() {
		builder.with(config).useCapacityServers();
		builder.build();
		ConnectionInfo handlerStatus = builder.getConnectionInfo();
		System.out.println(handlerStatus);
	}

	@Test
	public void connectViaScsServers() {
		builder.with(config).useScsServers();
		builder.build();
		ConnectionInfo handlerStatus = builder.getConnectionInfo();
		System.out.println(handlerStatus);
	}
}
