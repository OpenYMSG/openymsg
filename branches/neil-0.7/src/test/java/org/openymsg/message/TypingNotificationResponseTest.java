package org.openymsg.message;

import org.mockito.Mockito;
import org.openymsg.Contact;
import org.openymsg.PacketReader;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.testng.annotations.Test;

public class TypingNotificationResponseTest {

	@Test
	public void testTyping() {
		String test = "Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testuser] [5] [testbuddy] [13] [1] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageCallback callback = Mockito.mock(SessionMessageCallback.class);
		TypingNotificationResponse response = new TypingNotificationResponse(callback);
		response.execute(packet);
		Contact contact = new Contact("testbuddy", YahooProtocol.YAHOO);
		Mockito.verify(callback).receivedTypingNotification(contact, true);
	}

	@Test
	public void testDone() {
		String test = "Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testuser] [5] [testbuddy] [13] [0] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		SessionMessageCallback callback = Mockito.mock(SessionMessageCallback.class);
		TypingNotificationResponse response = new TypingNotificationResponse(callback);
		response.execute(packet);
		Contact contact = new Contact("testbuddy", YahooProtocol.YAHOO);
		Mockito.verify(callback).receivedTypingNotification(contact, false);
	}

	@Test(enabled = false)
	public void testProtocol() {
	}
}
