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
