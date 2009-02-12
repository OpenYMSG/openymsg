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
package org.openymsg.network.event;

import org.openymsg.network.FireEvent;

/**
 * Empty-method implementation of the {@link SessionListener} interface. A
 * typical usage of this class would be to extend it, and override just that one
 * particular method that you're interested in.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionAdapter implements SessionListener {
	public void fileTransferReceived(SessionFileTransferEvent event) {
		// override this function if you want to do something with it.
	}

	public void connectionClosed(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	public void listReceived(SessionListEvent event) {
		// override this function if you want to do something with it.
	}

	public void messageReceived(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	public void buzzReceived(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	public void offlineMessageReceived(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	public void errorPacketReceived(SessionErrorEvent event) {
		// override this function if you want to do something with it.
	}

	public void inputExceptionThrown(SessionExceptionEvent event) {
		// override this function if you want to do something with it.
	}

	public void newMailReceived(SessionNewMailEvent event) {
		// override this function if you want to do something with it.
	}

	public void notifyReceived(SessionNotifyEvent event) {
		// override this function if you want to do something with it.
	}

	public void contactRequestReceived(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	public void contactRejectionReceived(SessionFriendRejectedEvent event) {
		// override this function if you want to do something with it.
	}

	public void conferenceInviteReceived(SessionConferenceEvent event) {
		// override this function if you want to do something with it.
	}

	public void conferenceInviteDeclinedReceived(SessionConferenceEvent event) {
		// override this function if you want to do something with it.
	}

	public void conferenceLogonReceived(SessionConferenceEvent event) {
		// override this function if you want to do something with it.
	}

	public void conferenceLogoffReceived(SessionConferenceEvent event) {
		// override this function if you want to do something with it.
	}

	public void conferenceMessageReceived(SessionConferenceEvent event) {
		// override this function if you want to do something with it.
	}

	public void friendsUpdateReceived(SessionFriendEvent event) {
		// override this function if you want to do something with it.
	}

	public void friendAddedReceived(SessionFriendEvent event) {
		// override this function if you want to do something with it.
	}

	public void friendRemovedReceived(SessionFriendEvent event) {
		// override this function if you want to do something with it.
	}

	public void groupRenameReceived(SessionGroupEvent event) {
		// override this function if you want to do something with it.
	}

	public void chatJoinReceived(SessionChatEvent event) {
		// override this function if you want to do something with it.
	}

	public void chatExitReceived(SessionChatEvent event) {
		// override this function if you want to do something with it.
	}

	public void chatMessageReceived(SessionChatEvent event) {
		// override this function if you want to do something with it.
	}

	public void chatUserUpdateReceived(SessionChatEvent event) {
		// override this function if you want to do something with it.
	}

	public void chatConnectionClosed(SessionEvent event) {
		// override this function if you want to do something with it.
	}

	/**
	 * Dispatches an event immediately to all listeners, instead of queuing. it.
	 * 
	 * @param event
	 *            The event to be dispatched.
	 */
	public void dispatch(FireEvent event) {
		final SessionEvent ev = event.getEvent();

			switch (event.getType()) {
			case LOGOFF:
				connectionClosed(ev);
				break;
			case Y6_STATUS_UPDATE:
				friendsUpdateReceived((SessionFriendEvent) ev);
				break;
			case MESSAGE:
				messageReceived(ev);
				break;
			case X_OFFLINE:
				offlineMessageReceived(ev);
				break;
			case NEWMAIL:
				newMailReceived((SessionNewMailEvent) ev);
				break;
			case CONTACTNEW:
				contactRequestReceived(ev);
				break;
			case CONFDECLINE:
				conferenceInviteDeclinedReceived((SessionConferenceEvent) ev);
				break;
			case CONFINVITE:
				conferenceInviteReceived((SessionConferenceEvent) ev);
				break;
			case CONFLOGON:
				conferenceLogonReceived((SessionConferenceEvent) ev);
				break;
			case CONFLOGOFF:
				conferenceLogoffReceived((SessionConferenceEvent) ev);
				break;
			case CONFMSG:
				conferenceMessageReceived((SessionConferenceEvent) ev);
				break;
			case FILETRANSFER:
				fileTransferReceived((SessionFileTransferEvent) ev);
				break;
			case NOTIFY:
				notifyReceived((SessionNotifyEvent) ev);
				break;
			case LIST:
				listReceived((SessionListEvent) ev);
				break;
			case FRIENDADD:
				friendAddedReceived((SessionFriendEvent) ev);
				break;
			case FRIENDREMOVE:
				friendRemovedReceived((SessionFriendEvent) ev);
				break;
			case GOTGROUPRENAME:
				groupRenameReceived((SessionGroupEvent) ev);
				break;
			case CONTACTREJECT:
				contactRejectionReceived((SessionFriendRejectedEvent) ev);
				break;
			case CHATJOIN:
				chatJoinReceived((SessionChatEvent) ev);
				break;
			case CHATEXIT:
				chatExitReceived((SessionChatEvent) ev);
				break;
			case CHATDISCONNECT:
				chatConnectionClosed(ev);
				break;
			case CHATMSG:
				chatMessageReceived((SessionChatEvent) ev);
				break;
			case X_CHATUPDATE:
				chatUserUpdateReceived((SessionChatEvent) ev);
				break;
			case X_ERROR:
				errorPacketReceived((SessionErrorEvent) ev);
				break;
			case X_EXCEPTION:
				inputExceptionThrown((SessionExceptionEvent) ev);
				break;
			case X_BUZZ:
				buzzReceived(ev);
				break;
			case LOGON:
				logonReceived(ev);
				break;
			default:
				throw new IllegalArgumentException(
						"Don't know how to handle service type '"
								+ event.getType() + "'");
			}
	}

	/**
	 * @param ev
	 */
	private void logonReceived(SessionEvent ev) {
		// TODO Auto-generated method stub
		
	}
}
