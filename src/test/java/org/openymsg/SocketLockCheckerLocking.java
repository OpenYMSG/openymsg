package org.openymsg;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.network.direct.SocketLockChecker;

public class SocketLockCheckerLocking implements SocketLockChecker {
	private static final Log log = LogFactory.getLog(SocketLockCheckerLocking.class);
	private Lock lock = new ReentrantLock();
	private boolean locked;

	@Override
	public void startWriting() {
		log.debug("startWriting: " + locked);
		lock.lock();
		locked = true;
	}

	@Override
	public void finishWriting() {
		log.debug("finishWriting: " + locked);
		lock.unlock();
		locked = false;
	}

	@Override
	public boolean isLocked(int duration) {
		log.debug("isLocked: " + locked);
		return locked;
	}

	public void lock() {
		log.debug("startLocking: " + locked);
		lock.lock();
		log.debug("finishLocking");
		locked = true;
	}

	public void unLock() {
		lock.unlock();
		locked = false;
	}

}
