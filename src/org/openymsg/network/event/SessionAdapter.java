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

/**
 * Empty-method implementation of the {@link SessionListener} interface.
 * 
 * @author S.E. Morris
 */
public class SessionAdapter implements SessionListener {
	public void fileTransferReceived(SessionFileTransferEvent ev) {
	}

	public void connectionClosed(SessionEvent ev) {
	}

	public void listReceived(SessionEvent ev) {
	}

	public void messageReceived(SessionEvent ev) {
	}

	public void buzzReceived(SessionEvent ev) {
	}

	public void offlineMessageReceived(SessionEvent ev) {
	}

	public void errorPacketReceived(SessionErrorEvent ev) {
	}

	public void inputExceptionThrown(SessionExceptionEvent ev) {
		ev.getException().printStackTrace();
	}

	public void newMailReceived(SessionNewMailEvent ev) {
	}

	public void notifyReceived(SessionNotifyEvent ev) {
	}

	public void contactRequestReceived(SessionEvent ev) {
	}

	public void contactRejectionReceived(SessionEvent ev) {
	}

	public void conferenceInviteReceived(SessionConferenceEvent ev) {
	}

	public void conferenceInviteDeclinedReceived(SessionConferenceEvent ev) {
	}

	public void conferenceLogonReceived(SessionConferenceEvent ev) {
	}

	public void conferenceLogoffReceived(SessionConferenceEvent ev) {
	}

	public void conferenceMessageReceived(SessionConferenceEvent ev) {
	}

	public void friendsUpdateReceived(SessionFriendEvent ev) {
	}

	public void friendAddedReceived(SessionFriendEvent ev) {
	}

	public void friendRemovedReceived(SessionFriendEvent ev) {
	}

	public void groupRenameReceived(SessionGroupEvent ev) {
	}

	public void chatJoinReceived(SessionChatEvent ev) {
	}

	public void chatExitReceived(SessionChatEvent ev) {
	}

	public void chatMessageReceived(SessionChatEvent ev) {
	}

	public void chatUserUpdateReceived(SessionChatEvent ev) {
	}

	public void chatConnectionClosed(SessionEvent ev) {
	}
}
