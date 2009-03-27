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
package org.openymsg.v1.network;

import org.openymsg.network.ChatroomManagerTest;
import org.openymsg.network.chatroom.ChatroomManager;
import org.openymsg.v1.network.chatroom.ChatroomManagerV1;
import org.openymsg.v1.network.chatroom.YahooChatCategoryV1;
import org.openymsg.v1.network.chatroom.YahooChatLobbyV1;

public class ChatroomManagerV1Test extends ChatroomManagerTest<YahooChatCategoryV1, YahooChatLobbyV1> {

	protected ChatroomManager<YahooChatCategoryV1, YahooChatLobbyV1> createChatroomManager() {
		return new ChatroomManagerV1(null, null);
	}

}
