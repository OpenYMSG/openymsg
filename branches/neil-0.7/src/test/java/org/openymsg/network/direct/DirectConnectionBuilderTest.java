package org.openymsg.network.direct;

import org.openymsg.config.SessionConfig;
import org.openymsg.config.SessionConfigImpl;
import org.openymsg.connection.ConnectionInfo;
import org.openymsg.network.ConnectionBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DirectConnectionBuilderTest {
	private SessionConfig config;
	private ConnectionBuilder builder;
	
	@BeforeMethod
	public void setup() {
		config = new SessionConfigImpl();
		builder = new DirectConnectionBuilder();
	}
	
	@Test
	public void connectViaCapacityServers() {
		builder.with(config).useCapacityServers();
		builder.build();
		ConnectionInfo handlerStatus = builder.getHandlerStatus();
		System.out.println(handlerStatus);
	}

	@Test
	public void connectViaScsServers() {
		builder.with(config).useScsServers();
		builder.build();
		ConnectionInfo handlerStatus = builder.getHandlerStatus();
		System.out.println(handlerStatus);
	}
}
