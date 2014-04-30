package org.openymsg;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openymsg.network.direct.SocketLockChecker;

public class SocketLockCheckerLocking implements SocketLockChecker {
	private Lock lock = new ReentrantLock();
	private boolean locked;

	@Override
	public void startWriting() {
		System.out.println("startWriting: " + locked);
		lock.lock();
		locked = true;
	}

	@Override
	public void finishWriting() {
		System.out.println("finishWriting: " + locked);
		lock.unlock();
		locked = false;
	}

	@Override
	public boolean isLocked(int duration) {
		System.out.println("isLocked: " + locked);
		return locked;
	}

	public void lock() {
		System.out.println("startLocking: " + locked);
		lock.lock();
		System.out.println("finishLocking");
		locked = true;
	}

	public void unLock() {
		lock.unlock();
		locked = false;
	}

}
