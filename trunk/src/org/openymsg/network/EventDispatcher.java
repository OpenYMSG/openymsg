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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.openymsg.network.event.SessionChatEvent;
import org.openymsg.network.event.SessionConferenceEvent;
import org.openymsg.network.event.SessionErrorEvent;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionExceptionEvent;
import org.openymsg.network.event.SessionFileTransferEvent;
import org.openymsg.network.event.SessionFriendEvent;
import org.openymsg.network.event.SessionGroupEvent;
import org.openymsg.network.event.SessionListener;
import org.openymsg.network.event.SessionNewMailEvent;
import org.openymsg.network.event.SessionNotifyEvent;

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

	// registered event listeners
	private volatile Collection<SessionListener> listeners;

	// queue of events that are going to be fired.
	private volatile Queue<FireEvent> queue;

	public EventDispatcher() {
		super("jYMSG Event Dispatcher thread");
		listeners = new Vector<SessionListener>();
		queue = new LinkedList<FireEvent>();
	}

	/**
	 * Adds a session listener to the collection of listeners to which events
	 * are dispatched.
	 * 
	 * @param sessionListener
	 *            SessionListener to be added.
	 * @return Returns ''true'' if this collection changed as a result of the
	 *         call. (Returns false if this collection does not permit
	 *         duplicates and already contains the specified element.)
	 */
	public boolean addSessionListener(SessionListener sessionListener) {
		return listeners.add(sessionListener);
	}

	/**
	 * Removes the listener from the collection of listeners to which events are
	 * dispatched.
	 * 
	 * @param sessionListener
	 *            The SessionListener to be removed
	 * @return Returns ''true'' if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeSessionListener(SessionListener sessionListener) {
		return listeners.remove(sessionListener);
	}

	/**
	 * Gracefully stops this thread after sending out all currently queued
	 * events. No new events can be queued after calling this method.
	 */
	public void kill() {
		synchronized (queue) {
			quitFlag = true;
			queue.notifyAll();
		}
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

		synchronized (queue) {
			if (!queue.offer(new FireEvent(event, type))) {
				throw new IllegalStateException(
						"Unable to offer an event to the eventqueue.");
			}
			queue.notifyAll();
		}
	}

	/**
	 * Dispatches an event immediately to all listeners, instead of queuing. it.
	 * 
	 * @param event
	 *            The event to be dispatched.
	 */
	private void dispatch(FireEvent event) {
		final SessionEvent ev = event.getEvent();

		for (SessionListener l : listeners) {
			switch (event.getType()) {
			case LOGOFF:
				l.connectionClosed(ev);
				break;
			case ISAWAY:
				l.friendsUpdateReceived((SessionFriendEvent) ev);
				break;
			case MESSAGE:
				l.messageReceived(ev);
				break;
			case X_OFFLINE:
				l.offlineMessageReceived(ev);
				break;
			case NEWMAIL:
				l.newMailReceived((SessionNewMailEvent) ev);
				break;
			case CONTACTNEW:
				l.contactRequestReceived(ev);
				break;
			case CONFDECLINE:
				l.conferenceInviteDeclinedReceived((SessionConferenceEvent) ev);
				break;
			case CONFINVITE:
				l.conferenceInviteReceived((SessionConferenceEvent) ev);
				break;
			case CONFLOGON:
				l.conferenceLogonReceived((SessionConferenceEvent) ev);
				break;
			case CONFLOGOFF:
				l.conferenceLogoffReceived((SessionConferenceEvent) ev);
				break;
			case CONFMSG:
				l.conferenceMessageReceived((SessionConferenceEvent) ev);
				break;
			case FILETRANSFER:
				l.fileTransferReceived((SessionFileTransferEvent) ev);
				break;
			case NOTIFY:
				l.notifyReceived((SessionNotifyEvent) ev);
				break;
			case LIST:
				l.listReceived(ev);
				break;
			case FRIENDADD:
				l.friendAddedReceived((SessionFriendEvent) ev);
				break;
			case FRIENDREMOVE:
				l.friendRemovedReceived((SessionFriendEvent) ev);
				break;
			case GROUPRENAME:
				l.groupRenameReceived((SessionGroupEvent) ev);
				break;
			case CONTACTREJECT:
				l.contactRejectionReceived(ev);
				break;
			case CHATLOGON:
				l.chatLogonReceived((SessionChatEvent) ev);
				break;
			case CHATLOGOFF:
				l.chatLogoffReceived((SessionChatEvent) ev);
				break;
			case CHATDISCONNECT:
				l.chatConnectionClosed(ev);
				break;
			case CHATMSG:
				l.chatMessageReceived((SessionChatEvent) ev);
				break;
			case X_CHATUPDATE:
				l.chatUserUpdateReceived((SessionChatEvent) ev);
				break;
			case X_ERROR:
				l.errorPacketReceived((SessionErrorEvent) ev);
				break;
			case X_EXCEPTION:
				l.inputExceptionThrown((SessionExceptionEvent) ev);
				break;
			case X_BUZZ:
				l.buzzReceived(ev);
				break;
			default:
				throw new IllegalArgumentException(
						"Don't know how to handle service type '"
								+ event.getType() + "'");
			}
		}
	}

	@Override
	public void run() {
		synchronized (queue) {
			while (!quitFlag) {
				try {
					// Sleep, surrendering lock on queue, until something to
					// process
					queue.wait();
					// Acquire lock, and process queue, then clear it
					FireEvent event;
					while ((event = queue.poll()) != null) {
						try {
							dispatch(event);
						} catch (Exception e) {
							try {
								dispatch(new FireEvent(
										new SessionExceptionEvent(this,
												"Source: EventDispatcher", e),
										ServiceType.X_EXCEPTION));
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
					}
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}
}
