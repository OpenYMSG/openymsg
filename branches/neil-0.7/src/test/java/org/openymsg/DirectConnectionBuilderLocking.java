package org.openymsg;

import org.openymsg.network.direct.DirectConnectionBuilder;
import org.openymsg.network.direct.DirectConnectionHandler;
import org.openymsg.network.direct.SocketLockChecker;

public class DirectConnectionBuilderLocking extends DirectConnectionBuilder {
	private SocketLockChecker socketLockChecker;

	public DirectConnectionBuilderLocking(SocketLockChecker socketLockChecker) {
		this.socketLockChecker = socketLockChecker;
	}

	@Override
	protected DirectConnectionHandler createHandler() {
		return new DirectConnectionHandler(this.getSocket(), socketLockChecker);
	}
}
