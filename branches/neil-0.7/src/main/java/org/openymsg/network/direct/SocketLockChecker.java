package org.openymsg.network.direct;

public interface SocketLockChecker {
	void startWriting();

	void finishWriting();

	boolean isLocked(int duration);

}
