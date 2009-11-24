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
import org.openymsg.network.chatroom.ChatroomManager;
import org.openymsg.network.chatroom.YahooChatCategory;
import org.openymsg.network.chatroom.YahooChatLobby;
import org.openymsg.network.chatroom.YahooChatRoom;

/**
 * @author G. der Kinderen, Nimbuzz B.V. guus@nimbuzz.com
 */
public abstract class ChatroomManagerTest<T extends YahooChatCategory<?,?>, U extends YahooChatLobby<?>> {
	private static Logger log = Logger.getLogger(ChatroomManagerTest.class);
	
	@Test
	public void testDefaultChatroomManager() throws Exception {
		createChatroomManager();
	}

	protected abstract ChatroomManager<T, U> createChatroomManager();

	@Test(expected = IllegalStateException.class)
	public void testDefaultChatroomManagerIsUnloaded() {
		final ChatroomManager<T, U> manager = createChatroomManager();
		manager.getLobby("should not be loaded and throw exception.");
	}

	@Test
	public void testLoadCategories() throws Exception {
		final Date start = new Date();
		final ChatroomManager<T, U> manager = createChatroomManager();
		final T root = manager.loadCategories();
		log.info("Loading categories in "
				+ (new Date().getTime() - start.getTime()) + " milliseconds.");
		testCategory(root);
		// prettyPrint(root);
	}

	private void testCategory(YahooChatCategory<?,?> category) {
		assertNotNull(category);
		assertNotNull(category.getId());
		assertNotNull(category.getName());
		assertNotNull(category.getPrivateRooms());
		assertNotNull(category.getPublicRooms());
		assertNotNull(category.getSubcategories());

		for (YahooChatCategory<?,?> cat : category.getSubcategories()) {
			testCategory(cat);
		}
	}

	private void prettyPrint(YahooChatCategory<?,?> category) throws Exception {
		log.info(" ");
		log.info(category.toString());
		category.loadRooms();
		for (YahooChatCategory<?,?> subCategory : category.getSubcategories()) {
			prettyPrint(subCategory);
		}

		if (category.getPrivateRooms().size() > 0) {
			log.info("Private rooms:");
			for (YahooChatRoom<?> room : category.getPrivateRooms()) {
				if (room.lobbyCount() > 0) {
					log.info(" " + room.toString());
					for (YahooChatLobby<?> lobby : room.getLobbies()) {
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
			for (YahooChatRoom<?> room : category.getPublicRooms()) {
				if (room.lobbyCount() > 0) {
					log.info(" " + room.toString());
					for (YahooChatLobby<?> lobby : room.getLobbies()) {
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