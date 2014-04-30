package org.openymsg.network.direct;

public class SocketLockCheckerImpl implements SocketLockChecker {
	private long lastWriteTimestamp = 0;

	@Override
	public void startWriting() {
		lastWriteTimestamp = System.currentTimeMillis();
	}

	@Override
	public void finishWriting() {
		lastWriteTimestamp = 0;
	}

	@Override
	public boolean isLocked(int millisDuration) {
		long lastWriteTimestampCopy = lastWriteTimestamp;
		if (lastWriteTimestampCopy == 0) {
			return false;
		}

		return System.currentTimeMillis() > lastWriteTimestampCopy + millisDuration;
	}

}
