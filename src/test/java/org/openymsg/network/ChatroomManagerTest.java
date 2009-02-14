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

import static junit.framework.Assert.assertNotNull;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openymsg.v1.network.chatroom.ChatroomManagerV1;
import org.openymsg.v1.network.chatroom.YahooChatCategoryV1;
import org.openymsg.v1.network.chatroom.YahooChatLobbyV1;
import org.openymsg.v1.network.chatroom.YahooChatRoomV1;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public class ChatroomManagerTest {
	private static Logger log = Logger.getLogger(ChatroomManagerTest.class);
	
	@Test
	public void testDefaultChatroomManager() throws Exception {
		new ChatroomManagerV1(null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testDefaultChatroomManagerIsUnloaded() {
		final ChatroomManagerV1 manager = new ChatroomManagerV1(null, null);
		manager.getLobby("should not be loaded and throw exception.");
	}

	@Test
	public void testLoadCategories() throws Exception {
		final Date start = new Date();
		final ChatroomManagerV1 manager = new ChatroomManagerV1(null, null);
		final YahooChatCategoryV1 root = manager.loadCategories();
		log.info("Loading categories in "
				+ (new Date().getTime() - start.getTime()) + " milliseconds.");
		testCategory(root);
		// prettyPrint(root);
	}

	private void testCategory(YahooChatCategoryV1 category) {
		assertNotNull(category);
		assertNotNull(category.getId());
		assertNotNull(category.getName());
		assertNotNull(category.getPrivateRooms());
		assertNotNull(category.getPublicRooms());
		assertNotNull(category.getSubcategories());

		for (YahooChatCategoryV1 cat : category.getSubcategories()) {
			testCategory(cat);
		}
	}

	@SuppressWarnings("unused")
	private void prettyPrint(YahooChatCategoryV1 category) throws Exception {
		log.info(" ");
		log.info(category.toString());
		category.loadRooms();
		for (YahooChatCategoryV1 subCategory : category.getSubcategories()) {
			prettyPrint(subCategory);
		}

		if (category.getPrivateRooms().size() > 0) {
			log.info("Private rooms:");
			for (YahooChatRoomV1 room : category.getPrivateRooms()) {
				if (room.lobbyCount() > 0) {
					log.info(" " + room.toString());
					for (YahooChatLobbyV1 lobby : room.getLobbies()) {
						log.info("   " + lobby.getNetworkName()
								+ " users: " + lobby.getReportedUsers()
								+ " voices: " + lobby.getReportedVoices()
								+ " webcams: " + lobby.getReportedWebcams());
					}
				}
			}
		}

		if (category.getPublicRooms().size() > 0) {
			log.info("Public rooms:");
			for (YahooChatRoomV1 room : category.getPublicRooms()) {
				if (room.lobbyCount() > 0) {
					log.info(" " + room.toString());
					for (YahooChatLobbyV1 lobby : room.getLobbies()) {
						log.info("   " + lobby.getNetworkName()
								+ " users: " + lobby.getReportedUsers()
								+ " voices: " + lobby.getReportedVoices()
								+ " webcams: " + lobby.getReportedWebcams());
					}
				}
			}
		}
	}
}
