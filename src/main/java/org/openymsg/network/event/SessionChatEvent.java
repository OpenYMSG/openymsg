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

import org.openymsg.network.chatroom.YahooChatLobby;
import org.openymsg.network.chatroom.YahooChatUser;

/**
 * Note: this class is designed to hold more than one chat user. However in real
 * life Yahoo never actually seems to send details of more than one user in a
 * single packet (except the for initial packet containing the list of users in
 * the room when you first join - but this event isn't used for that) ...
 * However, the packet does contain a user count. So to play it safe this class
 * uses an array.
 * 
 * From ChatUser ChatUsers Lobby Message Emote chatJoinReceived y y y y n n
 * chatDisconnectReceived y y y y n n chatMessageReceived y y y y y y
 * chatConnectionBroken n n n n n n
 * 
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 * @author S.E. Morris
 */
public class SessionChatEvent extends SessionEvent {
	protected YahooChatUser[] users;

	protected YahooChatLobby lobby;

	protected boolean emote;

    protected String captchaMsg,captchaURL;

	/**
	 * CONSTRUCTORS
	 */
	// Chat user joined/left
	public SessionChatEvent(Object source, int size, YahooChatLobby ycl) {
		super(source);
		users = new YahooChatUser[size];
		lobby = ycl;
	}

	// Message received
	public SessionChatEvent(Object source, YahooChatUser ycu, String message,
			String emote, YahooChatLobby ycl) {
		this(source, 1, ycl);
		setChatUser(0, ycu);
		this.message = message;
		this.emote = (emote != null && emote.equals("2"));
	}

    // -----Captcha
	public SessionChatEvent(Object o,String capM,String capU,YahooChatLobby ycl)
	{
        super(o);
        captchaMsg=capM;  captchaURL=capU;
        lobby=ycl;
	}

	public void setChatUser(int i, YahooChatUser ycu) {
		users[i] = ycu;
	}

	/**
	 * Accessors
	 */
	// User joined room
	public YahooChatUser getChatUser() {
		return users[0];
	}

	public YahooChatUser[] getChatUsers() {
		return users;
	}

	public YahooChatLobby getLobby() {
		return lobby;
	}

	@Override
	public String getFrom() {
		return users[0].getId();
	}

	public boolean isEmote() {
		return emote;
	}

    public String getCaptchaMessage()
    {
        return captchaMsg;
    }

    public String getCaptchaURL()
    {
        return captchaURL;
    }

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" size:").append(users.length).append(" chatuser:").append(
				users[0].getId()).append(" lobby:").append(
				lobby.getNetworkName());
		return sb.toString();
	}
}
