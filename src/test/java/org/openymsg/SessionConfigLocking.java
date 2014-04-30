package org.openymsg;

import org.openymsg.config.SessionConfigSimple;
import org.openymsg.network.ConnectionBuilder;
import org.openymsg.network.direct.SocketLockChecker;

public class SessionConfigLocking extends SessionConfigSimple {
	private SocketLockChecker socketLockChecker;

	public SessionConfigLocking(SocketLockChecker socketLockChecker) {
		this.socketLockChecker = socketLockChecker;
	}

	protected ConnectionBuilder createDirectConnectionBuilder() {
		return new DirectConnectionBuilderLocking(socketLockChecker);
	}

}
