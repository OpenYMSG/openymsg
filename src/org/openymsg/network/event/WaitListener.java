/**
 * 
 */
package org.openymsg.network.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openymsg.network.FireEvent;
import org.openymsg.network.ServiceType;
import org.openymsg.network.Session;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 *
 */
public class WaitListener implements SessionListener {

	BlockingQueue<FireEvent> events = new ArrayBlockingQueue<FireEvent>(50);
	Logger log = Logger.getLogger("org.openymsg");
	private Session session;

	public WaitListener(Session session) {
		this.session = session;
		session.addSessionListener(this);
	}
	/* (non-Javadoc)
	 * @see org.openymsg.network.event.SessionListener#dispatch(org.openymsg.network.FireEvent)
	 */
	public void dispatch(FireEvent event) {
		log.debug("session :"+session.getLoginIdentity().getId()+"  event: "+event.getType());
		events.offer(event);
	}

	/**
	 * return the specific event based on type, waiting for max seconds time
	 * @param seconds
	 * @param type
	 * @return
	 */
	public FireEvent waitForEvent(int seconds, ServiceType type){
		try {
			int nt = seconds *10 ;
			for(int i =0;i<100;i++) {
				FireEvent ret = events.poll(nt, TimeUnit.MILLISECONDS);
				if(ret!=null && ret.getType().equals(type))
					return ret;
			}
			return null;
		} catch (InterruptedException e) {
			log.error("exception, waitForEvent will return null" , e);
			return null;
		}
	}

	public FireEvent waitForEvent(int seconds){
		try {
				return events.poll(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("exception, waitForEvent will return null" , e);
			return null;
		}
	}

	public void clearEvents() {
		events.clear();
	}

}
