package org.openymsg.config;

import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.TestingConnectionBuilder;
import org.openymsg.network.url.URLStreamBuilder;

public class TestingSessionConfig implements SessionConfig {
	private boolean connect;

	public TestingSessionConfig(boolean connect) {
		this.connect = connect;
	}

	public TestingSessionConfig() {
		this.connect = true;
	}

	@Override
	public String getLoginHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionBuilder getConnectionBuilder() {
		return new TestingConnectionBuilder(this.connect);
	}

	@Override
	public String getPasswordTokenGetUrl(String username, String password, String seed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPasswordTokenLoginUrl(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCapacityHosts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getSocketSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getConnectionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getScsHosts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URLStreamBuilder getURLStreamBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}
