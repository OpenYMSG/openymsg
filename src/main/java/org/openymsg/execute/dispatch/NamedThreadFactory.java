package org.openymsg.execute.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

//TODO can I just create one.
public class NamedThreadFactory implements ThreadFactory {
	// TODO, remove
	private static final Map<String, Integer> NAMES = new HashMap<String, Integer>();
	private String name;

	public NamedThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, getName(name));
		return thread;
	}

	private String getName(String name) {
		Integer count = NAMES.get(name);
		if (count == null) {
			count = new Integer(0);
		}
		count++;
		NAMES.put(name, count);
		return name + count;
	}
}
