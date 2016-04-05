package org.openymsg.execute.dispatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * One factory for each user. Each additional thread will have a name of name + ":" + count
 * @author nhart
 */
public class NamedThreadFactory implements ThreadFactory {
	private static final Map<String, Integer> NAMES = new ConcurrentHashMap<String, Integer>();
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
		return name + ":" + count;
	}
}
