/*
 * OpenYMSG, an implementation of the Yahoo Instant Messaging and Chat protocol.
 * Copyright (C) 2007 G. der Kinderen, Nimbuzz.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openymsg.network;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionExceptionEvent;
import org.openymsg.network.event.SessionListener;

/**
 * Dispatcher for events that are fired. Events that get fired are broadcasted
 * to all listeners that are registered to the instance of this object.
 * 
 * The process of dispatching events is threaded so the network code which
 * instigates these events can return to listening for input, and not get tied
 * up in each listener's event handler.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class EventDispatcher extends Thread {
	private volatile boolean quitFlag = false;

	Logger log = Logger.getLogger("org.openymsg");

	// queue of events that are going to be fired.
	private volatile BlockingQueue<FireEvent> queue = new ArrayBlockingQueue<FireEvent>(50);

	private Session session;

	public EventDispatcher(Session session) {
		super("jYMSG Event Dispatcher thread");
		this.session = session;
	}

	/**
	 * Gracefully stops this thread after sending out all currently queued
	 * events. No new events can be queued after calling this method.
	 */
	public void kill() {
		quitFlag = true;
	}
	
	public void append(ServiceType type) {
		if (!queue.offer(new FireEvent(null, type))) 
			throw new IllegalStateException(
			"Unable to offer an event to the eventqueue.");
	}

	/**
	 * Add an event to the dispatch queue. This causes the event to be
	 * dispatched to all registered listeners.
	 * 
	 * @param event
	 *            The sessionEvent that needs to be dispatched.
	 * @param type
	 *            The service typ of the event that's being dispatched.
	 */
	public void append(SessionEvent event, ServiceType type) {
		if (event == null) {
			throw new IllegalArgumentException(
			"Argument 'event' cannot be null.");
		}

		if (type == null) {
			throw new IllegalArgumentException(
			"Argument 'type' cannot be null.");
		}

		if (quitFlag) {
			throw new IllegalStateException(
			"No new events can be queued, because the dispatcher is being closed.");
		}

		if (!queue.offer(new FireEvent(event, type))) 
			throw new IllegalStateException(
			"Unable to offer an event to the eventqueue.");
	}



	@Override
	public void run() {
		while (!quitFlag) {
			FireEvent event;
			try {
				event = queue.poll(50,TimeUnit.MILLISECONDS);
				if(event!= null) {
					try {
						for (SessionListener l : session.getSessionListeners()) 
							l.dispatch(event);
					} catch (Exception e) {
						log.error(e, e);
						try {
							for (SessionListener l : session.getSessionListeners()) {
								l.dispatch(new FireEvent(
										new SessionExceptionEvent(this,
												"Source: EventDispatcher", e),
												ServiceType.X_EXCEPTION));

							}
						} catch (Exception e2) {
							log.error(e, e2);
						}
					}
				}
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}
	}
}
