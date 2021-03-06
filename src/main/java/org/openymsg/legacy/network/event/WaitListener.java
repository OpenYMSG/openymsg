/**
 * 
 */
package org.openymsg.legacy.network.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openymsg.legacy.network.FireEvent;
import org.openymsg.legacy.network.ServiceType;
import org.openymsg.legacy.network.Session;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 */
public class WaitListener implements SessionListener {
	BlockingQueue<FireEvent> events = new ArrayBlockingQueue<FireEvent>(50);
	private static final Log log = LogFactory.getLog(WaitListener.class);
	private Session session;

	public WaitListener(Session session) {
		this.session = session;
		session.addSessionListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openymsg.legacy.network.event.SessionListener#dispatch(org.openymsg.legacy.network.FireEvent)
	 */
	@Override
	public void dispatch(FireEvent event) {
		log.debug("session :" + session.getLoginIdentity().getId() + "  event: " + event.getType());
		events.offer(event);
	}

	/**
	 * return the specific event based on type, waiting for max seconds time
	 * @param seconds The amount of seconds to wait for the event.
	 * @param type The kind of event to wait for.
	 * @return The Event that's received, or 'null' if no event was received within the specified time.
	 */
	public FireEvent waitForEvent(int seconds, ServiceType type) {
		try {
			int nt = seconds * 10;
			for (int i = 0; i < 100; i++) {
				FireEvent ret = events.poll(nt, TimeUnit.MILLISECONDS);
				if (ret != null && ret.getType().equals(type))
					return ret;
			}
			return null;
		} catch (InterruptedException e) {
			log.error("exception, waitForEvent will return null", e);
			return null;
		}
	}

	public FireEvent waitForEvent(int seconds) {
		try {
			return events.poll(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("exception, waitForEvent will return null", e);
			return null;
		}
	}

	public void clearEvents() {
		events.clear();
	}
}
