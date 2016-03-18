package org.openymsg.config;

public class SessionConfigKeepAlive extends SessionConfigSimple {
	@Override
	public Integer getSessionTimeout() {
		return new Integer(3 * 60);
	}
}
