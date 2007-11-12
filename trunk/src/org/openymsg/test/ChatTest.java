/**
 * 
 */
package org.openymsg.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openymsg.network.FireEvent;
import org.openymsg.network.ServiceType;
import org.openymsg.network.event.SessionNotifyEvent;

/**
 * @author Giancarlo Frison - Nimbuzz B.V. <giancarlo@nimbuzz.com>
 *
 */
public class ChatTest extends YahooTest{
	@Test
	public void testSendMessage() throws IOException {
		sess1.sendMessage(OTHERUSR, CHATMESSAGE);
		FireEvent event = listener2.waitForEvent(5, ServiceType.MESSAGE);
		assertNotNull(event);
		assertEquals(event.getEvent().getMessage(), CHATMESSAGE);
	}

	@Test
	public void testSendMessageIdentity() throws IOException {
		sess1.sendMessage(OTHERUSR, CHATMESSAGE,sess1.getLoginIdentity());
		FireEvent event = listener2.waitForEvent(5, ServiceType.MESSAGE);
		assertNotNull(event);
		assertEquals(event.getEvent().getMessage(), CHATMESSAGE);
	}
	
	@Test
	public void testSendReceiveStartNotifyTyping() throws IOException {
		sess1.sendTypingNotification(OTHERUSR, true);
		FireEvent event = listener2.waitForEvent(5, ServiceType.NOTIFY);
		assertNotNull(event);
		SessionNotifyEvent sne = (SessionNotifyEvent) event.getEvent();
		assertTrue(sne.isTyping());
		assertTrue(sne.isOn());
		sess1.sendMessage(OTHERUSR, CHATMESSAGE,sess1.getLoginIdentity());
		event = listener2.waitForEvent(5, ServiceType.MESSAGE);
		assertNotNull(event);
		assertEquals(event.getEvent().getMessage(), CHATMESSAGE);
	}
	
	@Test
	public void testSendReceiveStopNotifyTyping() throws IOException {
		sess1.sendTypingNotification(OTHERUSR, true);
		FireEvent event = listener2.waitForEvent(5, ServiceType.NOTIFY);
		assertNotNull(event);		
		sess1.sendTypingNotification(OTHERUSR, false);
		event = listener2.waitForEvent(5, ServiceType.NOTIFY);
		assertNotNull(event);		
		SessionNotifyEvent sne = (SessionNotifyEvent) event.getEvent();
		assertTrue(sne.isTyping());
		assertTrue(sne.isOff());
		sess1.sendMessage(OTHERUSR, CHATMESSAGE,sess1.getLoginIdentity());
		event = listener2.waitForEvent(5, ServiceType.MESSAGE);
		assertNotNull(event);
		assertEquals(event.getEvent().getMessage(), CHATMESSAGE);
	}
	
	
//	@Test
//	public void testChatLogin() throws Exception {
//		final ChatroomManager manager = new ChatroomManager(null, null);
//		final YahooChatCategory root = manager.loadCategories();
//		root.loadRooms();
//		YahooChatLobby find = findLobby(root);
//		if(find!=null) {
//			sess1.chatLogin(find);
//			log.info(listener1.waitForEvent(5));
//		}
//	}
//	private YahooChatLobby findLobby(YahooChatCategory cat) throws Exception {
//		log.info("category:"+cat.getName());
//		YahooChatLobby ret = null;
//		for (YahooChatCategory category : cat.getSubcategories()) {
//			category.loadRooms();
//			for (YahooChatRoom room : category.getPublicRooms()) {
//				log.info("public rooms : "+room.getName());
//				for (YahooChatLobby lobby : room.getLobbies()) {
//					ret = lobby;
//					break;
//				}
//			}
//			if(ret==null)
//				ret = findLobby(category);
//		}
//		return ret;
//	}
}
