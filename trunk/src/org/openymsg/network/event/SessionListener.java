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
 * Listener interface for Session events.
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public interface SessionListener {
	// Someone has sent us a file
	// to - the target (us!)
	// from - the user who sent the file
	// location - the URL of the file data on Yahoo (download this)
	// datestamp - the date when the URL stops being valid (?)
	// message - a text message to accompany the file
	public void fileTransferReceived(SessionFileTransferEvent ev);

	// Yahoo has logged us off the system, or the connection was lost
	public void connectionClosed(SessionEvent ev);

	// A list (friends and groups) update has been received
	public void listReceived(SessionEvent ev);

	// Someone has sent us a message
	// to - the target (us!)
	// from - the user who sent the message
	// message - the message text
	public void messageReceived(SessionEvent ev);

	// Someone has sent us a buzz message
	// to - the target (us!)
	// from - the user who sent the message
	// message - the message text
	public void buzzReceived(SessionEvent ev);

	// Yahoo tells us about a message sent while we were away
	// to - the target (us!)
	// from - the user who sent the message
	// message - the message text
	// datestamp - the date the message was sent (?)
	public void offlineMessageReceived(SessionEvent ev);

	// Message contained tag 16. (These are commonly sent when trying
	// to ignore/unignore users already ignored/unignored).
	public void errorPacketReceived(SessionErrorEvent ev);

	// The input thread has thrown an exception. (Of course, this
	// should *never* happen :-)
	public void inputExceptionThrown(SessionExceptionEvent ev);

	// Yahoo tells us we have unread Yahoo mail
	// mail - number of unread mails
	public void newMailReceived(SessionNewMailEvent ev);

	// Yahoo server wants to notify us of something
	// service - the type of request
	// to - the target (us!)
	// from - the user who sent the message
	// mode - 0=off/1=on (for typing)
	public void notifyReceived(SessionNotifyEvent ev);

	// Someone wants to add us to their friends list
	// to - the target (us!)
	// from - the user who wants to add us
	// message - the request message text
	public void contactRequestReceived(SessionEvent ev);

	// Someone has rejected our attempts to add them to our friends list
	// from - the user who rejected us
	// message - rejection message text
	public void contactRejectionReceived(SessionEvent ev);

	// Someone is inviting us to join a conference. Use Session.
	// acceptConferenceInvite() to accept or Session.declineSessionInvite()
	// to decline.
	// to - the target (us!)
	// from - the host of the conference (yahoo id)
	// topic - the topic (or welcome message) of the conference
	// room - the conference name
	// users[] - yahoo id's of other users in the conference
	public void conferenceInviteReceived(SessionConferenceEvent ev);

	// Someone has refused our invite to out conference
	// to - the target (us!)
	// from - the declining user
	// room - the conference name
	public void conferenceInviteDeclinedReceived(SessionConferenceEvent ev);

	// Someone has joined a conference we are part of.
	// to - the target (us!)
	// from - the user joining (yahoo id)
	// room - the conference name
	public void conferenceLogonReceived(SessionConferenceEvent ev);

	// Someone is leaving a conference we are part of
	// to - the target (us!)
	// from - the user leaving
	// room - the conference name
	public void conferenceLogoffReceived(SessionConferenceEvent ev);

	// Someone has sent round a message to the conference members
	// to - the target (us!)
	// from - the user who sent the message
	// message - the message text
	// room - the conference name
	public void conferenceMessageReceived(SessionConferenceEvent ev);

	// Friend's details have been updated
	// friends - vector of updated YahooUser's
	public void friendsUpdateReceived(SessionFriendEvent ev);

	// Successfully added a friend
	// friend - YahooUser of friend
	// group - name of group added to
	public void friendAddedReceived(SessionFriendEvent ev);

	// Successfully removed a friend
	// friend - YahooUser of friend
	// group - name of group removed from
	public void friendRemovedReceived(SessionFriendEvent ev);

	// Group has been renamed
	// oldName - old name of group
	// newName = new name of group
	public void groupRenameReceived(SessionGroupEvent ev);

	// Someone new joins the chatroom
	// chatuser - YahooChatUser of new user
	// lobby - YahooChatLobby which they have joined
	public void chatJoinReceived(SessionChatEvent ev);

	// Someone leaves the chatroom
	// chatuser - YahooChatUser of new user
	// lobby - YahooChatLobby which they have joined
	public void chatExitReceived(SessionChatEvent ev);

	// Someone has sent a message to the chatroom
	// from - Yahoo id user
	// lobby - YahooChatLobby which they are in
	// message - message text
	public void chatMessageReceived(SessionChatEvent ev);

	// Someone has sent a message to the chatroom
	// from - Yahoo id user
	// chatuser - YahooChatUser of new user (with new details)
	// lobby - YahooChatLobby which they are in
	public void chatUserUpdateReceived(SessionChatEvent ev);

	// Thrown out of a chat room (timed out after inactivity?)
	// lobby - YahooChatLobby which they are in
	public void chatConnectionClosed(SessionEvent ev);

}
