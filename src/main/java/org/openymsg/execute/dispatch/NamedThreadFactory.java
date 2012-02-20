package org.openymsg.execute.dispatch;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {
	private String username;
	private int count = 0;
	
	public NamedThreadFactory(String username) {
		this.username = username;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, username + count++);		
		return thread;
	}

}
