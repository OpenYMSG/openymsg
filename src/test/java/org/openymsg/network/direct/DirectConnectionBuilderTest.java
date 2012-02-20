package org.openymsg.network.direct;

import org.openymsg.SessionConfig;
import org.openymsg.SessionConfigImpl;
import org.openymsg.network.ConnectionHandlerStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DirectConnectionBuilderTest {
	private SessionConfig config;
	private DirectConnectionBuilder builder;
	
	@BeforeMethod
	public void setup() {
		config = new SessionConfigImpl();
		builder = new DirectConnectionBuilder();
	}
	
	@Test
	public void connectViaCapacityServers() {
		builder.with(config).useCapacityServers();
		builder.build();
		ConnectionHandlerStatus handlerStatus = builder.getHandlerStatus();
		System.out.println(handlerStatus);
	}

	@Test
	public void connectViaScsServers() {
		builder.with(config).useScsServers();
		builder.build();
		ConnectionHandlerStatus handlerStatus = builder.getHandlerStatus();
		System.out.println(handlerStatus);
	}
}
