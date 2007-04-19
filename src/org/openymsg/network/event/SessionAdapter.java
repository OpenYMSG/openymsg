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
 * Empty-method implementation of the {@link SessionListener} interface. A
 * typical usage of this class would be to extend it, and override just that one
 * particular method that you're interested in.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
@SuppressWarnings("unused")
public class SessionAdapter implements SessionListener {
	public void fileTransferReceived(SessionFileTransferEvent event) {
	}

	public void connectionClosed(SessionEvent event) {
	}

	public void listReceived(SessionEvent event) {
	}

	public void messageReceived(SessionEvent event) {
	}

	public void buzzReceived(SessionEvent event) {
	}

	public void offlineMessageReceived(SessionEvent event) {
	}

	public void errorPacketReceived(SessionErrorEvent event) {
	}

	public void inputExceptionThrown(SessionExceptionEvent event) {
	}

	public void newMailReceived(SessionNewMailEvent event) {
	}

	public void notifyReceived(SessionNotifyEvent event) {
	}

	public void contactRequestReceived(SessionEvent event) {
	}

	public void contactRejectionReceived(SessionEvent event) {
	}

	public void conferenceInviteReceived(SessionConferenceEvent event) {
	}

	public void conferenceInviteDeclinedReceived(SessionConferenceEvent event) {
	}

	public void conferenceLogonReceived(SessionConferenceEvent event) {
	}

	public void conferenceLogoffReceived(SessionConferenceEvent event) {
	}

	public void conferenceMessageReceived(SessionConferenceEvent event) {
	}

	public void friendsUpdateReceived(SessionFriendEvent event) {
	}

	public void friendAddedReceived(SessionFriendEvent event) {
	}

	public void friendRemovedReceived(SessionFriendEvent event) {
	}

	public void groupRenameReceived(SessionGroupEvent event) {
	}

	public void chatJoinReceived(SessionChatEvent event) {
	}

	public void chatExitReceived(SessionChatEvent event) {
	}

	public void chatMessageReceived(SessionChatEvent event) {
	}

	public void chatUserUpdateReceived(SessionChatEvent event) {
	}

	public void chatConnectionClosed(SessionEvent event) {
	}
}
