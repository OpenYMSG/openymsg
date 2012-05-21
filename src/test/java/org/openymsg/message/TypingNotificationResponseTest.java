package org.openymsg.message;

import org.mockito.Mockito;
import org.openymsg.YahooContact;
import org.openymsg.YahooProtocol;
import org.openymsg.network.YMSG9Packet;
import org.openymsg.testing.PacketReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TypingNotificationResponseTest {
	private YahooContact contact = new YahooContact("testbuddy", YahooProtocol.YAHOO);
	private SessionMessageImpl session;
	private TypingNotificationResponse response;

	@BeforeMethod
	public void beforeMethod() {
		session = Mockito.mock(SessionMessageImpl.class);
		response = new TypingNotificationResponse(session);
	}

	@Test
	public void testYahooTyping() {
		String test = "Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy] [5] [testuser] [13] [1] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		response.execute(packet);
		Mockito.verify(session).receivedTypingNotification(contact, true);
	}

	@Test
	public void testYahooDone() {
		String test = "Magic:YMSG Version:16 Length:56 Service:NOTIFY Status:SERVER_ACK SessionId:0x45130f  [4] [testbuddy] [5] [testuser] [13] [0] [14] [ ] [49] [TYPING]";
		YMSG9Packet packet = PacketReader.readString(test);
		response.execute(packet);
		Mockito.verify(session).receivedTypingNotification(contact, false);
	}

	@Test
	public void testMsnTyping() {
		Assert.fail("not implemented");
	}
}
